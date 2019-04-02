package gov.va.api.health.providerdirectory.service.controller.practitioner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.resources.OperationOutcome;
import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderLicenses;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderWrapper;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import java.util.Collections;
import java.util.function.Function;
import javax.validation.constraints.Min;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Request Mappings for Location Resource, see
 * http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitioner.html for
 * implementation details.
 */
@Slf4j
@RestController
@RequestMapping(
        value = {"/api/Practitioner"},
        produces = {"application/json","application/fhir+json","application/json+fhir"}
)
public class PractitionerController {

  private final RestTemplate restTemplate;
  private final String baseUrl;
  private Transformer transformer;
  private Bundler bundler;

  public PractitionerController(@Value("${ppms.url}") String baseUrl, @Autowired RestTemplate restTemplate,@Autowired Transformer transformer,@Autowired Bundler bundler ) {
    this.restTemplate = restTemplate;
    this.baseUrl = baseUrl;
    this.transformer = transformer;
    this.bundler = bundler;
  }

  private static ObjectMapper objectMapper() {
    return JacksonConfig.createMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  // TODO: bundle function and search will be fairly different since ProviderDirectory calls out to
  private Practitioner.Bundle bundle(
     MultiValueMap<String, String> parameters, int page, int count) {
    ProviderResponse providerResponse = ppmsProvider(parameters.get("identifier").toArray()[0].toString());
    ProviderContacts providerContacts = ppmsProviderContact(parameters.get("identifier").toArray()[0].toString());
    ProviderLicenses providerLicenses = ppmsProviderLicense(parameters.get("identifier").toArray()[0].toString());
    ProviderWrapper root = ProviderWrapper.builder().providerContacts(providerContacts).providerLicenses(providerLicenses).providerResponse(providerResponse).build();

    LinkConfig linkConfig =
        LinkConfig.builder()
            .path("Practitioner")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(0)
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
  private ProviderResponse ppmsProvider(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")")
            .build()
            .toUriString();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<ProviderResponse> entity =
            restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderResponse.class);
    ProviderResponse responseObject = entity.getBody();
    return responseObject;
  }

  @SneakyThrows
  private ProviderContacts ppmsProviderContact(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl(
                baseUrl + "Providers(" + id + ")/ProviderContacts")
            .build()
            .toUriString();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<ProviderContacts> entity =
            restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderContacts.class);
    ProviderContacts responseObject = entity.getBody();

    return responseObject;
  }

  @SneakyThrows
  private ProviderLicenses ppmsProviderLicense(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl(
                baseUrl + "Providers(" + id + ")/ProviderLicenses")
            .build()
            .toUriString();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<ProviderLicenses> entity =
            restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderLicenses.class);
    ProviderLicenses responseObject = entity.getBody();
    return responseObject;
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

  // ** Search by family & given name. */
  @GetMapping(params = {"family", "given"})
  public Practitioner.Bundle searchByFailyAndGiven(
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

   /** Search by name. */
  @GetMapping(params = {"name"})
  public Practitioner.Bundle searchByName(
          @RequestParam("name") String name,
          @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
          @RequestParam(value = "_count", defaultValue = "1") @Min(0) int count) {
    return bundle(
            Parameters.builder()
                    .add("name", name)
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
  public OperationOutcome validate(@RequestBody Practitioner.Bundle bundle) {
    return Validator.create().validate(bundle);
  }


  public interface Transformer extends Function<ProviderWrapper, Practitioner> {}
}
