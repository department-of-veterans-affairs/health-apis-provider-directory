package gov.va.api.health.providerdirectory.service.controller.location;

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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
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
    List<LocationWrapper> root = search(parameters, page, count);
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
    return transformer.apply(search(Parameters.forIdentity((publicId)), 1, 1).get(0));
  }

  private List<LocationWrapper> search(
      MultiValueMap<String, String> parameters, int page, int count) {
    LocationWrapper.LocationWrapperBuilder locationWrapper =
        LocationWrapper.builder()
            .careSitesResponse(CareSitesResponse.builder().build())
            .providerResponse(ProviderResponse.builder().build())
            .providerServicesResponse(ProviderServicesResponse.builder().build());
    List<LocationWrapper> filteredResults = new ArrayList<>();
    List<ProviderServicesResponse> providerServicesResponsePages;
    List<ProviderResponse.Value> providerResponsePages;
    List<CareSitesResponse.Value> careSiteResponsePages;
    LocationWrapper currentPage;
    totalRecords = 1;
    int fromIndex;
    int toIndex;
    if (parameters.get("identifier") != null) {
      String identifier = parameters.get("identifier").toArray()[0].toString();
      locationWrapper.providerServicesResponse(ppmsClient.providerServicesById(identifier));
      locationWrapper.providerResponse(
          ProviderResponse.builder()
              .value(
                  Collections.singletonList(
                      ProviderResponse.Value.builder()
                          .providerIdentifier(
                              Integer.parseInt(
                                  parameters.get("identifier").toArray()[0].toString()))
                          .build()))
              .build());
      if (locationWrapper.build().providerServicesResponse().value() == null
          || locationWrapper.build().providerServicesResponse().value().isEmpty()) {
        locationWrapper.providerResponse(ppmsClient.providersForId(identifier));
        locationWrapper.careSitesResponse(ppmsClient.careSitesById(identifier));
      }
    } else if (parameters.get("name") != null) {
      String name = parameters.get("name").toArray()[0].toString();
      locationWrapper.providerResponse(ppmsClient.providersForName(name.split("'")[0].split("/")[0].split("#")[0]));
      totalRecords = locationWrapper.build().providerResponse().value().size();
      fromIndex =
          Math.min((page - 1) * count, locationWrapper.build().providerResponse().value().size());
      toIndex =
          Math.min((fromIndex + count), locationWrapper.build().providerResponse().value().size());

      providerServicesResponsePages =
          IntStream.range(fromIndex, toIndex)
              .parallel()
              .mapToObj(
                  i -> {
                    try {
                      return ppmsClient.providerServicesByName(
                          locationWrapper.build().providerResponse().value().get(i).name().split("'")[0].split("/")[0].split("#")[0]);
                    } catch (Exception e) {
                      return ProviderServicesResponse.builder().build();
                    }
                  })
              .collect(Collectors.toList());
      providerResponsePages =
          locationWrapper.build().providerResponse().value().subList(fromIndex, toIndex);
      ProviderServicesResponse currentProviderServiceResponse;
      CareSitesResponse currenCareSiteResponse;
      for (int i = 0; i < providerServicesResponsePages.size(); i++) {
        currentProviderServiceResponse = providerServicesResponsePages.get(i);
        if (currentProviderServiceResponse.value() == null
            || currentProviderServiceResponse.value().isEmpty()) {
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
                        .value(Collections.singletonList(providerResponsePages.get(i)))
                        .build())
                .providerServicesResponse(currentProviderServiceResponse)
                .build());
        currentPage = filteredResults.get(filteredResults.size() - 1);
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
            currenCareSiteResponse =
                currentPage.providerServicesResponse().value().get(0).careSiteName() == null
                    ? CareSitesResponse.builder().build()
                    : ppmsClient.careSitesByName(
                        currentPage.providerServicesResponse().value().get(0).careSiteName().split("'")[0].split("/")[0].split("#")[0]);
            if (currenCareSiteResponse == null
                || currenCareSiteResponse.value() == null
                || currenCareSiteResponse.value().isEmpty()) {
              currenCareSiteResponse =
                  currentPage.providerServicesResponse().value().get(0).organiztionGroupName()
                          == null
                      ? CareSitesResponse.builder().build()
                      : ppmsClient.careSitesByName(
                          currentPage
                              .providerServicesResponse()
                              .value()
                              .get(0)
                              .organiztionGroupName().split("'")[0].split("/")[0].split("#")[0]);
            }
            if (currenCareSiteResponse.value() == null
                || currenCareSiteResponse.value().isEmpty()
                || currenCareSiteResponse.value().get(0).mainSitePhone() == null) {
              filteredResults.remove(filteredResults.size() - 1);
            } else {
              filteredResults.remove(filteredResults.size() - 1);
              filteredResults.add(
                  LocationWrapper.builder()
                          .careSitesResponse(
                          CareSitesResponse.builder()
                              .value(
                                  Collections.singletonList(currenCareSiteResponse.value().get(0)))
                              .build())
                      .providerResponse(
                          ProviderResponse.builder()
                              .value(Collections.singletonList(providerResponsePages.get(i)))
                              .build())
                      .providerServicesResponse(currentProviderServiceResponse)
                      .build());
            }
          }
        }
      }
    } else {
      if (parameters.get("address-city") != null) {
        String city = parameters.get("address-city").toArray()[0].toString();
        locationWrapper.careSitesResponse(ppmsClient.careSitesByCity(city));
      } else if (parameters.get("address-state") != null) {
        String state = parameters.get("address-state").toArray()[0].toString();
        locationWrapper.careSitesResponse(ppmsClient.careSitesByState(state));
      } else {
        String zip = parameters.get("address-postalcode").toArray()[0].toString();
        locationWrapper.careSitesResponse(ppmsClient.careSitesByZip(zip));
      }
      totalRecords = locationWrapper.build().careSitesResponse().value().size();
      fromIndex =
          Math.min((page - 1) * count, locationWrapper.build().careSitesResponse().value().size());
      toIndex =
          Math.min((fromIndex + count), locationWrapper.build().careSitesResponse().value().size());
      providerServicesResponsePages =
          IntStream.range(fromIndex, toIndex)
              .parallel()
              .mapToObj(
                  i ->
                      ppmsClient.providerServicesByName(
                          locationWrapper.build().careSitesResponse().value().get(i).name().split("'")[0].split("/")[0].split("#")[0]))
              .collect(Collectors.toList());
      careSiteResponsePages =
          locationWrapper.build().careSitesResponse().value().subList(fromIndex, toIndex);
      ProviderResponse currentProviderResponse;
      ProviderServicesResponse providerServicesResponse;
      for (int i = 0; i < providerServicesResponsePages.size(); i++) {
        filteredResults.add(
            LocationWrapper.builder()
                .providerResponse(ProviderResponse.builder().build())
                .careSitesResponse(
                    CareSitesResponse.builder()
                        .value(Collections.singletonList(careSiteResponsePages.get(i)))
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
                    == null)) {
          if (currentPage.careSitesResponse().value() == null
              || currentPage.careSitesResponse().value().isEmpty()
              || currentPage.careSitesResponse().value().get(0).name() == null) {
            filteredResults.remove(filteredResults.size() - 1);
          } else {
            try {
              currentProviderResponse =
                  currentPage.careSitesResponse().value().get(0).owningOrganizationName() == null
                      ? ProviderResponse.builder().build()
                      : ppmsClient.providersForName(
                          currentPage.careSitesResponse().value().get(0).owningOrganizationName().split("'")[0].split("/")[0].split("#")[0]);
            } catch (Exception e) {
              currentProviderResponse = ProviderResponse.builder().build();
            }
            if (currentProviderResponse.value() == null
                || currentProviderResponse.value().isEmpty()) {
              try {
                currentProviderResponse =
                    ppmsClient.providersForName(
                        currentPage.careSitesResponse().value().get(0).name().split("'")[0].split("/")[0].split("#")[0]);
              } catch (Exception e) {
                currentProviderResponse = ProviderResponse.builder().build();
              }
            }
            if (currentProviderResponse.value() == null
                || currentProviderResponse.value().isEmpty()) {
              filteredResults.remove(filteredResults.size() - 1);
            } else if (currentProviderResponse.value().get(0).mainPhone() != null) {
              filteredResults.remove(filteredResults.size() - 1);
              filteredResults.add(
                  LocationWrapper.builder()
                      .providerResponse(
                          ProviderResponse.builder().value(currentProviderResponse.value()).build())
                      .careSitesResponse(
                          CareSitesResponse.builder()
                              .value(Collections.singletonList(careSiteResponsePages.get(i)))
                              .build())
                      .providerServicesResponse(providerServicesResponsePages.get(i))
                      .build());
            } else {
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
                        .providerResponse(
                            ProviderResponse.builder()
                                .value(currentProviderResponse.value())
                                .build())
                        .careSitesResponse(
                            CareSitesResponse.builder()
                                .value(Collections.singletonList(careSiteResponsePages.get(i)))
                                .build())
                        .providerServicesResponse(providerServicesResponse)
                        .build());
              }
            }
          }
        }
      }
    }
    return parameters.get("identifier") != null
        ? Collections.singletonList(locationWrapper.build())
        : filteredResults;
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
