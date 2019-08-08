package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.stu3.api.resources.PractitionerRole;
import java.util.ArrayList;
import org.apache.commons.lang3.tuple.Pair;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Collections.singletonList;

/**
 * Request Mappings for Practitioner Role Profile, see
 * https://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitionerrole.html for
 * implementation details.
 */
@SuppressWarnings("WeakerAccess")
@Validated
@RestController
@RequestMapping(
  value = {"/api/PractitionerRole"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class PractitionerRoleController {

  private PpmsClient ppmsClient;

  private Transformer transformer;

  private Bundler bundler;

  private PractitionerRole.Bundle bundle(
      MultiValueMap<String, String> parameters, int page, int count) {
    Pair<List<PractitionerRoleWrapper>, Integer> root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("PractitionerRole")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(root.getRight())
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            root.getLeft(),
            transformer,
            PractitionerRole.Entry::new,
            PractitionerRole.Bundle::new));
  }

  /** Builds the PractitionerWrapper and returns it. */
  private PractitionerRoleWrapper practitionerRoleWrapperBuilder(
          ProviderContactsResponse providerContactsResponse, ProviderResponse.Value providerResponse, ProviderSpecialtiesResponse providerSpecialtiesResponse) {
    PractitionerRoleWrapper.PractitionerRoleWrapperBuilder filteredResults =
            new PractitionerRoleWrapper.PractitionerRoleWrapperBuilder();
    filteredResults
            .providerResponse(ProviderResponse.builder().value(singletonList(providerResponse)).build())
            .providerContactsResponse(providerContactsResponse)
            .providerSpecialtiesResponse(providerSpecialtiesResponse);
    return filteredResults.build();
  }

  /** Read by identifier. */
  @GetMapping(value = {"/{publicId}"})
  public PractitionerRole readByIdentifier(@PathVariable("publicId") String publicId) {
    return transformer.apply(search(Parameters.forIdentity((publicId))).getKey().get(0));
  }

  private Pair<List<PractitionerRoleWrapper>, Integer> search(
          MultiValueMap<String, String> parameters) {
      if (parameters.containsKey("identifier")) {
        return searchIdentifier(parameters);
      } else {
        return searchFamilyGiven(parameters);
      }
    }

  /** Logic for search by Family and Given. */
  private Pair<List<PractitionerRoleWrapper>, Integer> searchFamilyGiven(
          MultiValueMap<String, String> parameters) {
    String familyName = parameters.getFirst("family");
    String givenName = parameters.getFirst("given");
    ProviderResponse providerResponse = ppmsClient.providersForName(familyName);
    int page = Integer.parseInt(parameters.getOrDefault("page", singletonList("1")).get(0));
    int count = Integer.parseInt(parameters.getOrDefault("_count", singletonList("15")).get(0));
    int fromIndex = Math.min((page - 1) * count, providerResponse.value().size());
    int toIndex = Math.min((fromIndex + count), providerResponse.value().size());
    /** Retrieve a list of providerResponse from PPMS using familyName. */
    List<ProviderResponse.Value> providerResponseUnfilteredPages =
            providerResponse.value().subList(fromIndex, toIndex);
    /** Remove any providerResponse that doesn't contain the givenName. */
    List<ProviderResponse.Value> providerResponsePages = new ArrayList<>();
    for (int i = 0; i < providerResponseUnfilteredPages.size(); i++) {
      if (StringUtils.containsIgnoreCase(
              providerResponseUnfilteredPages.get(i).name(), givenName)) {
        providerResponsePages.add(providerResponse.value().get(i));
      }
    }
    /** Using providerResponse, retrieve a list of providerContactsResponse from PPMS. */
    List<ProviderContactsResponse> providerContactsResponsePages =
            providerResponsePages
                    .parallelStream()
                    .map(
                            prv -> {
                              return ppmsClient.providerContactsForId(prv.providerIdentifier().toString());
                            })
                    .collect(Collectors.toList());

    /** Using providerResponse, retrieve a list of providerSpecialtyResponse from PPMS. */
    List<ProviderSpecialtiesResponse> providerSpecialtiesResponsePages =
            providerResponsePages
                    .parallelStream()
                    .map(
                            prv -> {
                              return ppmsClient.providerSpecialtySearch(prv.providerIdentifier().toString());
                            })
                    .collect(Collectors.toList());
    /**
     * Wrap providerResponse and providerContacts together to create a list of Practitioner (FHIR).
     */
    List<PractitionerRoleWrapper> practitionerWrapperPages =
            IntStream.range(0, providerContactsResponsePages.size())
                    .parallel()
                    .mapToObj(
                            i ->
                                    practitionerRoleWrapperBuilder(
                                            providerContactsResponsePages.get(i), providerResponsePages.get(i), providerSpecialtiesResponsePages.get(i)))
                    .collect(Collectors.toList());
    return Pair.of(practitionerWrapperPages, providerResponse.value().size());
  }

  /** Logic for search by Identifier. */
  private Pair<List<PractitionerRoleWrapper>, Integer> searchIdentifier(
          MultiValueMap<String, String> parameters) {
    PractitionerRoleWrapper.PractitionerRoleWrapperBuilder practitionerRoleWrapper =
            new PractitionerRoleWrapper.PractitionerRoleWrapperBuilder();
    String identifier = parameters.getFirst("identifier");
    ProviderResponse providerResponse = ppmsClient.providersForId(identifier);
    String providerIdentifier = providerResponse.value().get(0).providerIdentifier().toString();
    ProviderContactsResponse providerContactsResponse =
            ppmsClient.providerContactsForId(providerIdentifier);
    ProviderSpecialtiesResponse providerSpecialtiesResponse = ppmsClient.providerSpecialtySearch(providerIdentifier);
    return Pair.of(
            singletonList(
                    practitionerRoleWrapper
                            .providerContactsResponse(providerContactsResponse)
                            .providerResponse(providerResponse)
                            .providerSpecialtiesResponse(providerSpecialtiesResponse)
                            .build()),
            1);
  }

  /** Search by family and given. */
  @GetMapping(params = {"family", "given"})
  public PractitionerRole.Bundle searchByFamilyAndGiven(
      @RequestParam("family") String family,
      @RequestParam("given") String given,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("family", family)
            .add("given", given)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by identifier. */
  @SneakyThrows
  @GetMapping(params = {"identifier"})
  public PractitionerRole.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("identifier", identifier)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Placeholder for specialty search. */
  @GetMapping(params = {"specialty"})
  public PractitionerRole.Bundle searchBySpecialty(
      @RequestParam("specialty") String specialty,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    throw new UnsupportedOperationException();
  }

  public interface Transformer extends Function<PractitionerRoleWrapper, PractitionerRole> {}
}
