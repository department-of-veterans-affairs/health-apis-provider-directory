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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    PractitionerRoleWrapper root = search(parameters);
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("PractitionerRole")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(1)
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            Collections.singletonList(root),
            transformer,
            PractitionerRole.Entry::new,
            PractitionerRole.Bundle::new));
  }

  /** Read by identifier. */
  @GetMapping(value = {"/{publicId}"})
  public PractitionerRole readByIdentifier(@PathVariable("publicId") String publicId) {
    return transformer.apply(search(Parameters.forIdentity((publicId))));
  }

  private PractitionerRoleWrapper search(MultiValueMap<String, String> parameters) {
    ProviderResponse providerResponse;
    if (parameters.containsKey("identifier")) {
      String identifier = parameters.getFirst("identifier");
      providerResponse = ppmsClient.providersForId(identifier);
    } else if (parameters.containsKey("name")) {
      String name = parameters.getFirst("name");
      providerResponse = ppmsClient.providersForName(name);
    } else if (parameters.containsKey("family") && parameters.containsKey("given")) {
      String familyName = parameters.getFirst("family");
      String givenName = parameters.get("given").get(0).toLowerCase();
      providerResponse = ppmsClient.providersForName(familyName);
      List<ProviderResponse.Value> providerResponseFiltered = new ArrayList<>();
      for (int i = 0; i < providerResponse.value().size(); i++) {
        if (providerResponse.value().get(i).name().toLowerCase().contains(givenName)) {
          providerResponseFiltered.add(providerResponse.value().get(i));
        }
      }
      providerResponse.value(providerResponseFiltered);
    } else {
      return null;
    }
    String providerIdentifier = providerResponse.value().get(0).providerIdentifier().toString();
    ProviderContactsResponse providerContactsResponse =
        ppmsClient.providerContactsForId(
            providerResponse.value().get(0).providerIdentifier().toString());
    ProviderSpecialtiesResponse providerSpecialty =
        ppmsClient.providerSpecialtySearch(providerIdentifier);
    return PractitionerRoleWrapper.builder()
        .providerContactsResponse(providerContactsResponse)
        .providerResponse(providerResponse)
        .providerSpecialtiesResponse(providerSpecialty)
        .build();
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
