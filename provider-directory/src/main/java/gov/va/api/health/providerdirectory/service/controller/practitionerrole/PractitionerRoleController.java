package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import java.util.Collections;
import java.util.function.Function;
import javax.validation.constraints.Min;

import gov.va.api.health.providerdirectory.service.PpmsProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;
import gov.va.api.health.providerdirectory.service.PpmsPractitionerRole;

import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Request Mappings for Practitioner Role Profile, see
 * https://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitionerrole.html for
 * implementation details.
 */
@Slf4j
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
    String ppmsLookupParam;
    ProviderResponse providerResponse;

    if (parameters.get("identifier") != null) {
      ppmsLookupParam = parameters.get("identifier").toArray()[0].toString();
      providerResponse = ppmsProvider(ppmsLookupParam, true);
    } else if (parameters.get("specialty") != null) {
      ppmsLookupParam = parameters.get("specialty").toArray()[0].toString();
      providerResponse = ppmsProvider(ppmsLookupParam, false);
    } else {
      ppmsLookupParam =
              parameters.get("family").toArray()[0].toString()
                      + ", "
                      + parameters.get("given").toArray()[0].toString();
      providerResponse = ppmsProvider(ppmsLookupParam, false);
    }

    String providerIdentifier = providerResponse.value().get(0).providerIdentifier().toString();

    ProviderContacts providerContacts = ppmsProviderContact(providerIdentifier);
    PpmsProviderSpecialtiesResponse providerSpecialty = ppmsProviderSpecialty(providerIdentifier);


    PpmsPractitionerRole root =
            PpmsPractitionerRole.builder()
                    .providerContacts(providerContacts)
                    .providerResponse(providerResponse)
                    .providerSpecialtiesResponse(providerSpecialty)
                    .build();
    int totalRecords = 0;
    LinkConfig linkConfig =
            LinkConfig.builder()
                    .path("PractitionerRole")
                    .queryParams(parameters)
                    .page(page)
                    .recordsPerPage(count)
                    .totalRecords(totalRecords)
                    .build();
    return bundler.bundle(
            BundleContext.of(
                    linkConfig,
                    Collections.singletonList(root),
                    transformer,
                    PractitionerRole.Entry::new,
                    PractitionerRole.Bundle::new));
  }

  @SneakyThrows
  private ProviderResponse ppmsProvider(String id, Boolean identifier) {
    return ppmsClient.providerResponseSearch(id, identifier);
  }

  @SneakyThrows
  private PpmsProviderSpecialtiesResponse ppmsProviderSpecialty(String id) {
    return ppmsClient.providerSpecialtySearch(id);
  }

  @SneakyThrows
  private ProviderContacts ppmsProviderContact(String id) {
    return ppmsClient.providerContactsSearch(id);
  }

  /** Read by identifier. */
  @GetMapping(value = {"/{publicId}"})
  public PractitionerRole read(@PathVariable("publicId") String publicId) {
    throw new UnsupportedOperationException();
  }

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

  @GetMapping(params = {"specialty"})
  public PractitionerRole.Bundle searchBySpecialty(
      @RequestParam("specialty") String specialty,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    throw new UnsupportedOperationException();
  }

  public interface Transformer extends Function<PpmsPractitionerRole, PractitionerRole> {}
}
