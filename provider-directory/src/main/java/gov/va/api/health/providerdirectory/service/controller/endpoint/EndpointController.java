package gov.va.api.health.providerdirectory.service.controller.endpoint;

import static java.util.Collections.singletonList;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.providerdirectory.service.CountParameter;
import gov.va.api.health.providerdirectory.service.client.VlerClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import gov.va.api.health.stu3.api.resources.Endpoint;
import gov.va.api.health.stu3.api.resources.OperationOutcome;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Request Mappings for Endpoint Resource, see
 * http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-endpoint.html for implementation
 * details.
 */
@RestController
@RequestMapping(
  value = {"/api/Endpoint"},
  produces = {"application/json", "application/fhir+json", "application/json+fhir"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class EndpointController {

  private Transformer transformer;

  private Bundler bundler;

  private VlerClient vlerClient;

  private Endpoint.Bundle bundle(MultiValueMap<String, String> parameters, int page, int count) {
    Pair<List<EndpointWrapper>, Integer> root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Endpoint")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(root.getRight())
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig, root.getLeft(), transformer, Endpoint.Entry::new, Endpoint.Bundle::new));
  }

  private Pair<List<EndpointWrapper>, Integer> search(MultiValueMap<String, String> parameters) {
    if (parameters.containsKey("name")) {
      return searchName(parameters);
    } else if (parameters.containsKey("identifier")) {
      return searchIdentifier(parameters);
    } else {
      return searchOrganization(parameters);
    }
  }

  /** Placeholder for Identifier search. */
  @GetMapping(params = {"identifier"})
  public Endpoint.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    throw new UnsupportedOperationException();
  }

  /** Search by Name. */
  @GetMapping(params = {"name"})
  public Endpoint.Bundle searchByName(
      @RequestParam("name") String name,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @CountParameter @Min(0) int count) {
    return bundle(
        Parameters.builder().add("name", name).add("page", page).add("_count", count).build(),
        page,
        count);
  }

  /** Placeholder for Organization search. */
  @GetMapping(params = {"organization"})
  public Endpoint.Bundle searchByOrganization(
      @RequestParam("organization") String organization,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    throw new UnsupportedOperationException();
  }

  /** Placeholder for search by Identifier. */
  private Pair<List<EndpointWrapper>, Integer> searchIdentifier(
      MultiValueMap<String, String> parameters) {
    throw new UnsupportedOperationException();
  }

  /** Logic for search by Name. */
  private Pair<List<EndpointWrapper>, Integer> searchName(
      MultiValueMap<String, String> parameters) {
    String name = parameters.getFirst("name");
    AddressResponse addressResponse = vlerClient.endpointByAddress(name);
    int filteredCount = 0;
    /* Retrieve the VLER response, which is the full unfiltered body. */
    List<AddressResponse.Contacts> unfilteredAddressResponsePages =
        addressResponse.contacts().subList(0, addressResponse.contacts().size());
    /* Filter the list for contacts that include the search name. */
    List<AddressResponse.Contacts> addressResponsePages = new ArrayList<>();
    for (int i = 0; i < unfilteredAddressResponsePages.size(); i++) {
      if (StringUtils.containsIgnoreCase(
          unfilteredAddressResponsePages.get(i).displayName(), name)) {
        addressResponsePages.add(addressResponse.contacts().get(i));
        filteredCount++;
      }
    }

    List<EndpointWrapper> endpointWrapperPages = new ArrayList<>();
    for (int i = 0; i < addressResponsePages.size(); i++) {
      endpointWrapperPages.add(
          (EndpointWrapper.builder()
                  .addressResponse(
                      AddressResponse.builder()
                          .contacts(singletonList(addressResponsePages.get(i)))
                          .build()))
              .build());
    }
    return Pair.of(endpointWrapperPages, filteredCount);
  }
  /*LocationWrapper.builder()
                .providerResponse(currentProviderResponse)
                .careSitesResponse(
                    CareSitesResponse.builder()
                        .value(singletonList(careSiteResponsePages.get(i)))
                        .build())
                .providerServicesResponse(providerServicesResponsePages.get(i))
                .build());*/

  /** Placeholder for search by Organization. */
  private Pair<List<EndpointWrapper>, Integer> searchOrganization(
      MultiValueMap<String, String> parameters) {
    throw new UnsupportedOperationException();
  }

  /** Hey, this is a validate endpoint. It validates. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody Endpoint.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer extends Function<EndpointWrapper, Endpoint> {}
}
