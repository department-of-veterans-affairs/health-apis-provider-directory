package gov.va.api.health.providerdirectory.service.controller.location;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.CountParameter;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
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
    ProviderServicesResponse providerServicesResponse = ProviderServicesResponse.builder().build();
    CareSitesResponse careSitesResponse;
    List<ProviderServicesResponse> paged = new ArrayList<>();
    int totalRecords = 1;

    if (parameters.get("identifier") != null) {
      String identifier = parameters.get("identifier").toArray()[0].toString();
      providerServicesResponse = ppmsClient.careSitesById(identifier);
    } else if (parameters.get("name") != null) {
      String name = parameters.get("name").toArray()[0].toString();
      providerServicesResponse = ppmsClient.careSitesByName(name);
    } else {
      if (parameters.get("address-city") != null) {
        String city = parameters.get("address-city").toArray()[0].toString();
        careSitesResponse = ppmsClient.careSitesByCity(city);
      } else if (parameters.get("address-state") != null) {
        String state = parameters.get("address-state").toArray()[0].toString();
        careSitesResponse = ppmsClient.careSitesByState(state);
      } else {
        String zip = parameters.get("address-postalcode").toArray()[0].toString();
        careSitesResponse = ppmsClient.careSitesByZip(zip);
      }
      totalRecords = careSitesResponse.value().size();
      int fromIndex = Math.min((page - 1) * count, careSitesResponse.value().size());
      int toIndex = Math.min((fromIndex + count), careSitesResponse.value().size());
      paged =
          IntStream.range(fromIndex, toIndex)
              .parallel()
              .mapToObj(i -> ppmsClient.careSitesByName(careSitesResponse.value().get(i).name()))
              .collect(Collectors.toList());
    }

    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Location")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(totalRecords)
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            paged.isEmpty() ? Collections.singletonList(providerServicesResponse) : paged,
            transformer,
            Location.Entry::new,
            Location.Bundle::new));
  }

  /** Search by family & given name. */
  @GetMapping(params = {"address-city"})
  public Location.Bundle searchByCity(
      @RequestParam("address-city") String city,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder().add("address-city", city).add("page", page).add("_count", count).build(),
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
        Parameters.builder().add("address-state", state).add("page", page).add("_count", count).build(),
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
        Parameters.builder().add("address-postalcode", zip).add("page", page).add("_count", count).build(),
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

  public interface Transformer extends Function<ProviderServicesResponse, Location> {}
}
