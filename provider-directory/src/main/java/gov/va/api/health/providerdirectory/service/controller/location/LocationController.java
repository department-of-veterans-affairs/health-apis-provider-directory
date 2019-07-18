package gov.va.api.health.providerdirectory.service.controller.location;

import gov.va.api.health.providerdirectory.api.resources.Location;
import gov.va.api.health.providerdirectory.api.resources.OperationOutcome;
import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.CountParameter;
import gov.va.api.health.providerdirectory.service.LocationWrapper;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Validator;
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

  private PpmsClient client;

  private Bundler bundler;

  /** Controller setup. */
  public LocationController(
      @Autowired LocationController.Transformer transformer,
      @Autowired Bundler bundler,
      @Autowired PpmsClient client) {
    this.transformer = transformer;
    this.bundler = bundler;
    this.client = client;
  }

  private Location.Bundle bundle(MultiValueMap<String, String> parameters, int page, int count) {
    LocationWrapper locationWrapper = LocationWrapper.builder().build();
    CareSitesResponse careSitesResponse;
    List<LocationWrapper> paged = new ArrayList<>();
    int recordCount = 1;

    if (parameters.get("identifier") != null) {
      String identifier = parameters.get("identifier").toArray()[0].toString();
      locationWrapper = client.careSitesById(identifier);
    } else if (parameters.get("name") != null) {
      String name = parameters.get("name").toArray()[0].toString();
      locationWrapper = client.careSitesByName(name);
    } else {
      if (parameters.get("city") != null) {
        String city = parameters.get("city").toArray()[0].toString();
        careSitesResponse = client.careSitesByCity(city);
      } else if (parameters.get("state") != null) {
        String state = parameters.get("state").toArray()[0].toString();
        careSitesResponse = client.careSitesByState(state);
      } else {
        String zip = parameters.get("zip").toArray()[0].toString();
        careSitesResponse = client.careSitesByZip(zip);
      }
      recordCount = careSitesResponse.value().size();
      int fromIndex = Math.min((page - 1) * count, careSitesResponse.value().size());
      int toIndex = Math.min((fromIndex + count), careSitesResponse.value().size());
      paged =
          IntStream.range(fromIndex, toIndex)
              .parallel()
              .mapToObj(i -> client.careSitesByName(careSitesResponse.value().get(i).name()))
              .collect(Collectors.toList());
    }

    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Location")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(recordCount)
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            paged.isEmpty() ? Collections.singletonList(locationWrapper) : paged,
            transformer,
            Location.Entry::new,
            Location.Bundle::new));
  }

  /** Search by family & given name. */
  @GetMapping(params = {"city"})
  public Location.Bundle searchByCity(
      @RequestParam("city") String city,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder().add("city", city).add("page", page).add("_count", count).build(),
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
  @GetMapping(params = {"state"})
  public Location.Bundle searchByState(
      @RequestParam("state") String state,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder().add("state", state).add("page", page).add("_count", count).build(),
        page,
        count);
  }

  /** Search by family & given name. */
  @GetMapping(params = {"zip"})
  public Location.Bundle searchByZip(
      @RequestParam("zip") String zip,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder().add("zip", zip).add("page", page).add("_count", count).build(),
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
