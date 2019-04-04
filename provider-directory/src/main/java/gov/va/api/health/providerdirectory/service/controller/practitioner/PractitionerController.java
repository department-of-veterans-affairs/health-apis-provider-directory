package gov.va.api.health.providerdirectory.service.controller.practitioner;

import gov.va.api.health.providerdirectory.api.resources.OperationOutcome;
import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderWrapper;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import java.util.Collections;
import java.util.function.Function;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping(
  value = {"/api/Practitioner"},
  produces = {"application/json", "application/fhir+json", "application/json+fhir"}
)
public class PractitionerController {
  private Transformer transformer;

  private Bundler bundler;

  private PpmsClient client;

  /** Controller setup. */
  public PractitionerController(
      @Autowired Transformer transformer,
      @Autowired Bundler bundler,
      @Autowired PpmsClient client) {
    this.transformer = transformer;
    this.bundler = bundler;
    this.client = client;
  }

  private Practitioner.Bundle bundle(
      MultiValueMap<String, String> parameters, int page, int count) {
    ProviderResponse providerResponse;
    if (parameters.get("identifier") != null) {
      String identifier = parameters.get("identifier").toArray()[0].toString();
      providerResponse = client.providersForId(identifier);
    } else if (parameters.get("name") != null) {
      String name = parameters.get("name").toArray()[0].toString();
      providerResponse = client.providersForName(name);
    } else {
      String familyAndGiven =
          parameters.get("family").toArray()[0].toString()
              + ", "
              + parameters.get("given").toArray()[0].toString();
      providerResponse = client.providersForName(familyAndGiven);
    }

    String providerIdentifier = providerResponse.value().get(0).providerIdentifier().toString();

    ProviderContacts providerContacts = ppmsProviderContact(providerIdentifier);

    ProviderWrapper root =
        ProviderWrapper.builder()
            .providerContacts(providerContacts)
            .providerResponse(providerResponse)
            .build();
    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Practitioner")
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
            Practitioner.Entry::new,
            Practitioner.Bundle::new));
  }

  @SneakyThrows
  private ProviderContacts ppmsProviderContact(String id) {
    return client.providerContactsForId(id);
  }

  /** Search by family & given name. */
  @GetMapping(params = {"family", "given"})
  public Practitioner.Bundle searchByFamilyAndGiven(
      @RequestParam("family") String familyName,
      @RequestParam("given") String givenName,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "1") @Min(0) int count) {
    return bundle(
        Parameters.builder()
            .add("family", familyName)
            .add("given", givenName)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  /** Search by identifier. */
  @GetMapping(params = {"identifier"})
  public Practitioner.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "1") @Min(0) int count) {
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
  public Practitioner.Bundle searchByName(
      @RequestParam("name") String name,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "1") @Min(0) int count) {
    return bundle(
        Parameters.builder().add("name", name).add("page", page).add("_count", count).build(),
        page,
        count);
  }

  /** Hey, this is a validate endpoint. It validates. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody Practitioner.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  public interface Transformer extends Function<ProviderWrapper, Practitioner> {}
}
