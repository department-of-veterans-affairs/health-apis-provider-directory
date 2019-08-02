package gov.va.api.health.providerdirectory.service.controller.location;

import static java.util.Collections.singletonList;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.CountParameter;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import gov.va.api.health.stu3.api.resources.Location;
import gov.va.api.health.stu3.api.resources.OperationOutcome;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for the Location Profile, see
 * http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-location.html for implementation
 * details.
 */
@SuppressWarnings("WeakerAccess")
@RestController
@RequestMapping(
  value = {"/api/Location"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
public class LocationController {

  private Transformer transformer;

  private PpmsClient ppmsClient;

  private Bundler bundler;

  private int totalRecords;

  /** Controller setup. */
  public LocationController(
      @Autowired LocationController.Transformer transformer,
      @Autowired Bundler bundler,
      @Autowired PpmsClient ppmsClient) {
    this.transformer = transformer;
    this.bundler = bundler;
    this.ppmsClient = ppmsClient;
  }

  private Location.Bundle bundle(MultiValueMap<String, String> parameters, int page, int count) {
    List<LocationWrapper> root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Location")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(totalRecords)
            .build();
    return bundler.bundle(
        BundleContext.of(linkConfig, root, transformer, Location.Entry::new, Location.Bundle::new));
  }

  /** Read by identifier. */
  @GetMapping(value = {"/{publicId}"})
  public Location readByIdentifier(@PathVariable("publicId") String publicId) {
    return transformer.apply(search(Parameters.forIdentity((publicId))).get(0));
  }

  private List<LocationWrapper> search(MultiValueMap<String, String> parameters) {
    if (parameters.get("identifier") != null) {
      return searchIdentifier(parameters);
    } else if (parameters.get("name") != null) {
      return searchName(parameters);
    } else {
      return searchAddress(parameters);
    }
  }

  private List<LocationWrapper> searchAddress(MultiValueMap<String, String> parameters) {
    LocationWrapper.LocationWrapperBuilder locationWrapper = new LocationWrapper.LocationWrapperBuilder();
    List<LocationWrapper> filteredResults = new ArrayList<>();
    LocationWrapper currentPage;
    totalRecords = 1;
    if (parameters.get("address-city") != null) {
      String city = parameters.getFirst("address-city");
      locationWrapper.careSitesResponse(ppmsClient.careSitesByCity(city));
    } else if (parameters.get("address-state") != null) {
      String state = parameters.getFirst("address-state");
      locationWrapper.careSitesResponse(ppmsClient.careSitesByState(state));
    } else {
      String zip = parameters.getFirst("address-postalcode");
      locationWrapper.careSitesResponse(ppmsClient.careSitesByZip(zip));
    }
    totalRecords = locationWrapper.build().careSitesResponse().value().size();
    int page = Integer.parseInt(parameters.getOrDefault("page", singletonList("1")).get(0));
    int count = Integer.parseInt(parameters.getOrDefault("_count", singletonList("15")).get(0));
    int fromIndex =
        Math.min((page - 1) * count, locationWrapper.build().careSitesResponse().value().size());
    int toIndex =
        Math.min((fromIndex + count), locationWrapper.build().careSitesResponse().value().size());
    List<ProviderServicesResponse> providerServicesResponsePages =
        IntStream.range(fromIndex, toIndex)
            .parallel()
            .mapToObj(
                i ->
                    ppmsClient.providerServicesByName(
                        trimIllegalCharacters(
                            locationWrapper.build().careSitesResponse().value().get(i).name())))
            .collect(Collectors.toList());
    List<CareSitesResponse.Value> careSiteResponsePages =
        locationWrapper.build().careSitesResponse().value().subList(fromIndex, toIndex);
    ProviderResponse currentProviderResponse;
    ProviderServicesResponse providerServicesResponse;
    for (int i = 0; i < providerServicesResponsePages.size(); i++) {
      try {
        currentProviderResponse =
            careSiteResponsePages.get(i).owningOrganizationName() == null
                ? ProviderResponse.builder().build()
                : ppmsClient.providersForName(
                    trimIllegalCharacters(careSiteResponsePages.get(i).owningOrganizationName()));
      } catch (Exception e) {
        currentProviderResponse = ProviderResponse.builder().build();
      }
      if (currentProviderResponse.value() == null || currentProviderResponse.value().isEmpty()) {
        try {
          currentProviderResponse =
              ppmsClient.providersForName(
                  trimIllegalCharacters(careSiteResponsePages.get(i).name()));
        } catch (Exception e) {
          currentProviderResponse = ProviderResponse.builder().build();
        }
      }
      if (currentProviderResponse.value() == null || currentProviderResponse.value().isEmpty()) {
        try {
          currentProviderResponse =
              ppmsClient.providersForName(
                  trimIllegalCharacters(
                      providerServicesResponsePages.get(i).value().get(0).providerName()));
        } catch (Exception e) {
          currentProviderResponse = ProviderResponse.builder().build();
        }
      }
      if (currentProviderResponse.value() != null && !currentProviderResponse.value().isEmpty()) {
        filteredResults.add(
            LocationWrapper.builder()
                .providerResponse(currentProviderResponse)
                .careSitesResponse(
                    CareSitesResponse.builder()
                        .value(singletonList(careSiteResponsePages.get(i)))
                        .build())
                .providerServicesResponse(providerServicesResponsePages.get(i))
                .build());
        currentPage = filteredResults.get(filteredResults.size() - 1);
        if ((currentPage.careSitesResponse().value() == null
                || currentPage.careSitesResponse().value().isEmpty()
                || currentPage.careSitesResponse().value().get(0).mainSitePhone() == null)
            && (currentPage.providerServicesResponse().value() == null
                || currentPage.providerServicesResponse().value().isEmpty()
                || currentPage.providerServicesResponse().value().get(0).careSitePhoneNumber()
                    == null)
            && (currentPage.providerResponse().value() == null
                || currentPage.providerResponse().value().isEmpty()
                || currentPage.providerResponse().value().get(0).mainPhone() == null)) {
          providerServicesResponse =
              ppmsClient.providerServicesById(
                  currentProviderResponse.value().get(0).providerIdentifier().toString());
          if (providerServicesResponse.value() == null
              || providerServicesResponse.value().isEmpty()
              || providerServicesResponse.value().get(0).careSitePhoneNumber() == null) {
            filteredResults.remove(filteredResults.size() - 1);
          } else {
            filteredResults.remove(filteredResults.size() - 1);
            filteredResults.add(
                LocationWrapper.builder()
                    .providerResponse(currentProviderResponse)
                    .careSitesResponse(
                        CareSitesResponse.builder()
                            .value(singletonList(careSiteResponsePages.get(i)))
                            .build())
                    .providerServicesResponse(providerServicesResponse)
                    .build());
          }
        }
      }
    }
    return filteredResults;
  }

  /** Search by family & given name. */
  @GetMapping(params = {"address-city"})
  public Location.Bundle searchByCity(
      @RequestParam("address-city") String city,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("address-city", city)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by Identifier. */
  @GetMapping(params = {"identifier"})
  public Location.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("identifier", identifier)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by name. */
  @GetMapping(params = {"name"})
  public Location.Bundle searchByName(
      @RequestParam("name") String name,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder().add("name", name).add("page", page).add("_count", count).build(),
        page,
        count);
  }

  /** Search by family & given name. */
  @GetMapping(params = {"address-state"})
  public Location.Bundle searchByState(
      @RequestParam("address-state") String state,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("address-state", state)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by family & given name. */
  @GetMapping(params = {"address-postalcode"})
  public Location.Bundle searchByZip(
      @RequestParam("address-postalcode") String zip,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("address-postalcode", zip)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  private List<LocationWrapper> searchIdentifier(MultiValueMap<String, String> parameters) {
    LocationWrapper.LocationWrapperBuilder locationWrapper = new LocationWrapper.LocationWrapperBuilder();
    String identifier = parameters.getFirst("identifier");
    ProviderServicesResponse providerServicesResponse = ppmsClient.providerServicesById(identifier);
    if (providerServicesResponse.value().isEmpty()) {
      return singletonList(locationWrapper.providerResponse(ppmsClient.providersForId(identifier)).careSitesResponse(ppmsClient.careSitesById(identifier)).build());
    }
    else {
      return singletonList(locationWrapper.providerResponse(
              ProviderResponse.builder()
                      .value(
                              singletonList(
                                      ProviderResponse.Value.builder()
                                              .providerIdentifier(
                                                      Integer.parseInt(identifier))
                                              .build()))
                      .build()).providerServicesResponse(providerServicesResponse).build());
    }
  }

  private List<LocationWrapper> searchName(MultiValueMap<String, String> parameters) {
    LocationWrapper.LocationWrapperBuilder locationWrapper = new LocationWrapper.LocationWrapperBuilder();
    List<LocationWrapper> filteredResults = new ArrayList<>();
    String name = parameters.getFirst("name");
    locationWrapper.providerResponse(ppmsClient.providersForName(trimIllegalCharacters(name)));
    totalRecords = locationWrapper.build().providerResponse().value().size();
    int page = Integer.parseInt(parameters.getOrDefault("page", singletonList("1")).get(0));
    int count = Integer.parseInt(parameters.getOrDefault("_count", singletonList("15")).get(0));
    int fromIndex =
        Math.min((page - 1) * count, locationWrapper.build().providerResponse().value().size());
    int toIndex =
        Math.min((fromIndex + count), locationWrapper.build().providerResponse().value().size());
    List<ProviderServicesResponse> providerServicesResponsePages =
        IntStream.range(fromIndex, toIndex)
            .parallel()
            .mapToObj(
                i -> {
                  try {
                    return ppmsClient.providerServicesByName(
                        trimIllegalCharacters(
                            locationWrapper.build().providerResponse().value().get(i).name()));
                  } catch (Exception e) {
                    return ProviderServicesResponse.builder().build();
                  }
                })
            .collect(Collectors.toList());
    List<ProviderResponse.Value> providerResponsePages =
        locationWrapper.build().providerResponse().value().subList(fromIndex, toIndex);

    for (int i = 0; i < providerServicesResponsePages.size(); i++) {
      ProviderServicesResponse currentProviderServiceResponse = providerServicesResponsePages.get(i);
      if (currentProviderServiceResponse.value().isEmpty()) {
        try {
          currentProviderServiceResponse =
              ppmsClient.providerServicesById(
                  locationWrapper
                      .build()
                      .providerResponse()
                      .value()
                      .get(i)
                      .providerIdentifier()
                      .toString());
        } catch (Exception e) {
          currentProviderServiceResponse = ProviderServicesResponse.builder().build();
        }
      }
      filteredResults.add(
          LocationWrapper.builder()
              .careSitesResponse(CareSitesResponse.builder().build())
              .providerResponse(
                  ProviderResponse.builder()
                      .value(singletonList(providerResponsePages.get(i)))
                      .build())
              .providerServicesResponse(currentProviderServiceResponse)
              .build());
      LocationWrapper currentPage = filteredResults.get(filteredResults.size() - 1);
      if ((currentPage.providerResponse().value() == null
              || currentPage.providerResponse().value().isEmpty()
              || currentPage.providerResponse().value().get(0).mainPhone() == null)
          && (currentPage.providerServicesResponse().value() == null
              || currentPage.providerServicesResponse().value().isEmpty()
              || currentPage.providerServicesResponse().value().get(0).careSitePhoneNumber()
                  == null)) {
        if (currentPage.providerServicesResponse().value() == null
            || currentPage.providerServicesResponse().value().isEmpty()
            || currentPage.providerServicesResponse().value().get(0).name() == null) {
          filteredResults.remove(filteredResults.size() - 1);
        } else {
          CareSitesResponse currentCareSiteResponse =
              currentPage.providerServicesResponse().value().get(0).careSiteName() == null
                  ? CareSitesResponse.builder().build()
                  : ppmsClient.careSitesByName(
                      trimIllegalCharacters(
                          currentPage.providerServicesResponse().value().get(0).careSiteName()));
          if (currentCareSiteResponse == null
              || currentCareSiteResponse.value() == null
              || currentCareSiteResponse.value().isEmpty()) {
            currentCareSiteResponse =
                currentPage.providerServicesResponse().value().get(0).organizationGroupName()
                        == null
                    ? CareSitesResponse.builder().build()
                    : ppmsClient.careSitesByName(
                        trimIllegalCharacters(
                            currentPage
                                .providerServicesResponse()
                                .value()
                                .get(0)
                                .organizationGroupName()));
          }
          if (currentCareSiteResponse.value() == null
              || currentCareSiteResponse.value().isEmpty()
              || currentCareSiteResponse.value().get(0).mainSitePhone() == null) {
            filteredResults.remove(filteredResults.size() - 1);
          } else {
            filteredResults.remove(filteredResults.size() - 1);
            filteredResults.add(
                LocationWrapper.builder()
                    .careSitesResponse(
                        CareSitesResponse.builder()
                            .value(singletonList(currentCareSiteResponse.value().get(0)))
                            .build())
                    .providerResponse(
                        ProviderResponse.builder()
                            .value(singletonList(providerResponsePages.get(i)))
                            .build())
                    .providerServicesResponse(currentProviderServiceResponse)
                    .build());
          }
        }
      }
    }
    return filteredResults;
  }

  private String trimIllegalCharacters(String name) {
    return name.split("'")[0].split("/")[0].split("#")[0];
  }

  /** Hey, this is a validate endpoint. It validates. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody Location.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer extends Function<LocationWrapper, Location> {}
}
