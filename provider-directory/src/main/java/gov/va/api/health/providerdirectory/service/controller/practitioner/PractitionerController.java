package gov.va.api.health.providerdirectory.service.controller.practitioner;

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
import javax.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
@RequestMapping(
  value = {"/api/Practitioner"},
  produces = {"application/json", "application/fhir+json", "application/json+fhir"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
public class PractitionerController {

  private Transformer transformer;

  private Bundler bundler;

  private PpmsClient ppmsClient;

  private Practitioner.Bundle bundle(
      MultiValueMap<String, String> parameters, int page, int count) {
    PractitionerWrapper root = search(parameters);
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

  private PractitionerWrapper search(MultiValueMap<String, String> parameters) {
    ProviderResponse providerResponse;
    if (parameters.containsKey("identifier")) {
      String identifier = parameters.getFirst("identifier");
      providerResponse = ppmsClient.providersForId(identifier);
    } else if (parameters.containsKey("name")) {
      String name = parameters.getFirst("name");
      providerResponse = ppmsClient.providersForName(name);
    } else {
      String familyName = parameters.getFirst("family");
      String givenName = parameters.getFirst("given");
      providerResponse = ppmsClient.providersForName(familyName);
      List<ProviderResponse.Value> providerResponseFiltered = new ArrayList<>();
      for (ProviderResponse.Value val : providerResponse.value()){
        if (StringUtils.containsIgnoreCase(val.name(), givenName)) {
          providerResponseFiltered.add(val);
        }
      }
      if (providerResponseFiltered.size() == 0) {
        throw new PpmsClient.PpmsException("No family name and given name found for combination '" + familyName + "' and '" + givenName + "'.");
      }
      providerResponse.value(providerResponseFiltered);
    }
    String providerIdentifier = providerResponse.value().get(0).providerIdentifier().toString();
    ProviderContactsResponse providerContactsResponse =
        ppmsClient.providerContactsForId(providerIdentifier);
    return PractitionerWrapper.builder()
        .providerContactsResponse(providerContactsResponse)
        .providerResponse(providerResponse)
        .build();
  }
  
  /** Read by identifier. */
  @GetMapping(value = {"/{publicId}"})
  public Practitioner readByIdentifier(@PathVariable("publicId") String publicId) {
    return transformer.apply(search(Parameters.forIdentity(publicId)));
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

  public interface Transformer extends Function<PractitionerWrapper, Practitioner> {}
}
