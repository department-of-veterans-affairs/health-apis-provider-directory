package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static java.util.Collections.singletonList;
import gov.va.api.health.providerdirectory.service.CountParameter;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import gov.va.api.health.stu3.api.resources.OperationOutcome;
import gov.va.api.health.stu3.api.resources.Practitioner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
 * Request Mappings for Location Resource, see
 * http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitioner.html for
 * implementation details.
 */
@RestController
@RequestMapping(value = { "/api/Practitioner" }, produces = { "application/json", "application/fhir+json", "application/json+fhir" })
@AllArgsConstructor(onConstructor = @__({ @Autowired }))
public class PractitionerController {

  private Transformer transformer;

  private Bundler bundler;

  private PpmsClient ppmsClient;

  private Practitioner.Bundle bundle(MultiValueMap<String, String> parameters, int page, int count) {
    Pair<List<PractitionerWrapper>, Integer> root = search(parameters);
    LinkConfig linkConfig = LinkConfig.builder().path("Practitioner").queryParams(parameters).page(page).recordsPerPage(count).totalRecords(root.getRight()).build();
    return bundler.bundle(BundleContext.of(linkConfig, root.getLeft(), transformer, Practitioner.Entry::new, Practitioner.Bundle::new));
  }

  /**
   * Builds the PractitionerWrapper and returns it
   */
  private PractitionerWrapper practitionerWrapperBuilder(ProviderContactsResponse providerContactsResponse, ProviderResponse.Value providerResponse) {
    PractitionerWrapper.PractitionerWrapperBuilder filteredResults = new PractitionerWrapper.PractitionerWrapperBuilder();
    filteredResults.providerResponse(ProviderResponse.builder().value(singletonList(providerResponse)).build()).providerContactsResponse(providerContactsResponse);
    return filteredResults.build();
  }

  /**
   * Read by identifier.
   */
  @GetMapping(value = { "/{publicId}" })
  public Practitioner readByIdentifier(@PathVariable("publicId") String publicId) {
    return transformer.apply(search(Parameters.forIdentity((publicId))).getKey().get(0));
  }

  private Pair<List<PractitionerWrapper>, Integer> search(MultiValueMap<String, String> parameters) {
    if (parameters.containsKey("identifier")) {
      return searchIdentifier(parameters);
    } else if (parameters.containsKey("name")) {
      return searchName(parameters);
    } else {
      return searchFamilyGiven(parameters);
    }
  }

  /**
   * Search by family & given name.
   */
  @GetMapping(params = { "family", "given" })
  public Practitioner.Bundle searchByFamilyAndGiven(@RequestParam("family") String familyName, @RequestParam("given") String givenName, @RequestParam(value = "page", defaultValue = "1") @Min(1) int page, @CountParameter @Min(0) int count) {
    return bundle(Parameters.builder().add("family", familyName).add("given", givenName).add("page", page).add("_count", count).build(), page, count);
  }

  /**
   * Search by identifier.
   */
  @GetMapping(params = { "identifier" })
  public Practitioner.Bundle searchByIdentifier(@RequestParam("identifier") String identifier, @RequestParam(value = "page", defaultValue = "1") @Min(1) int page, @CountParameter @Min(0) int count) {
    return bundle(Parameters.builder().add("identifier", identifier).add("page", page).add("_count", count).build(), page, count);
  }

  /**
   * Search by name.
   */
  @GetMapping(params = { "name" })
  public Practitioner.Bundle searchByName(@RequestParam("name") String name, @RequestParam(value = "page", defaultValue = "1") @Min(1) int page, @CountParameter @Min(0) int count) {
    return bundle(Parameters.builder().add("name", name).add("page", page).add("_count", count).build(), page, count);
  }

  /**
   * Logic for search by Family and Given
   */
  private Pair<List<PractitionerWrapper>, Integer> searchFamilyGiven(MultiValueMap<String, String> parameters) {
    String familyName = parameters.getFirst("family");
    String givenName = parameters.getFirst("given");
    ProviderResponse providerResponse = ppmsClient.providersForName(familyName);
    int page = Integer.parseInt(parameters.getOrDefault("page", singletonList("1")).get(0));
    int count = Integer.parseInt(parameters.getOrDefault("_count", singletonList("15")).get(0));
    int fromIndex = Math.min((page - 1) * count, providerResponse.value().size());
    int toIndex = Math.min((fromIndex + count), providerResponse.value().size());
    /**
     * Retrieve a list of providerResponse from PPMS using familyName.
     */
    List<ProviderResponse.Value> providerResponseUnfilteredPages = providerResponse.value().subList(fromIndex, toIndex);
    /**
     * Remove any providerResponse that doesn't contain the givenName
     */
    List<ProviderResponse.Value> providerResponsePages = new ArrayList<>();
    for (int i = 0; i < providerResponseUnfilteredPages.size(); i++) {
      if (StringUtils.containsIgnoreCase(providerResponseUnfilteredPages.get(i).name(), givenName)) {
        providerResponsePages.add(providerResponse.value().get(i));
      }
    }
    /**
     * Using providerResponse, retrieve a list of providerContactsResponse from PPMS.
     */
    List<ProviderContactsResponse> providerContactsResponsePages = providerResponsePages.parallelStream().map(prv -> {
      return ppmsClient.providerContactsForId(prv.providerIdentifier().toString());
    }).collect(Collectors.toList());
    /**
     * Wrap providerResponse and providerContacts together to create a list of Practitioner (FHIR).
     */
    List<PractitionerWrapper> practitionerWrapperPages = IntStream.range(0, providerContactsResponsePages.size()).parallel().mapToObj(i -> practitionerWrapperBuilder(providerContactsResponsePages.get(i), providerResponsePages.get(i))).collect(Collectors.toList());
    return Pair.of(practitionerWrapperPages, providerResponse.value().size());
  }

  /**
   * Logic for search by Identifier
   */
  private Pair<List<PractitionerWrapper>, Integer> searchIdentifier(MultiValueMap<String, String> parameters) {
    PractitionerWrapper.PractitionerWrapperBuilder practitionerWrapper = new PractitionerWrapper.PractitionerWrapperBuilder();
    String identifier = parameters.getFirst("identifier");
    ProviderResponse providerResponse = ppmsClient.providersForId(identifier);
    String providerIdentifier = providerResponse.value().get(0).providerIdentifier().toString();
    ProviderContactsResponse providerContactsResponse = ppmsClient.providerContactsForId(providerIdentifier);
    return Pair.of(singletonList(practitionerWrapper.providerContactsResponse(providerContactsResponse).providerResponse(providerResponse).build()), 1);
  }

  /**
   * Logic for search by Name
   */
  private Pair<List<PractitionerWrapper>, Integer> searchName(MultiValueMap<String, String> parameters) {
    String name = parameters.getFirst("name");
    ProviderResponse providerResponse = ppmsClient.providersForName(name);
    int page = Integer.parseInt(parameters.getOrDefault("page", singletonList("1")).get(0));
    int count = Integer.parseInt(parameters.getOrDefault("_count", singletonList("15")).get(0));
    int fromIndex = Math.min((page - 1) * count, providerResponse.value().size());
    int toIndex = Math.min((fromIndex + count), providerResponse.value().size());
    /**
     * Retrieve a list of providerResponse from PPMS using name.
     */
    List<ProviderResponse.Value> providerResponsePages = providerResponse.value().subList(fromIndex, toIndex);
    /**
     * Using providerResponse, retrieve a list of providerContactsResponse from PPMS.
     */
    List<ProviderContactsResponse> providerContactsResponsePages = providerResponsePages.parallelStream().map(prv -> {
      return ppmsClient.providerContactsForId(prv.providerIdentifier().toString());
    }).collect(Collectors.toList());
    /**
     * Wrap providerResponse and providerContacts together to create a list of Practitioner (FHIR).
     */
    List<PractitionerWrapper> practitionerWrapperPages = IntStream.range(0, providerContactsResponsePages.size()).parallel().mapToObj(i -> practitionerWrapperBuilder(providerContactsResponsePages.get(i), providerResponsePages.get(i))).collect(Collectors.toList());
    return Pair.of(practitionerWrapperPages, providerResponse.value().size());
  }

  /**
   * Hey, this is a validate endpoint. It validates.
   */
  @PostMapping(value = "/$validate", consumes = { "application/json", "application/json+fhir", "application/fhir+json" })
  public OperationOutcome validate(@RequestBody Practitioner.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer extends Function<PractitionerWrapper, Practitioner> {
  }
}
