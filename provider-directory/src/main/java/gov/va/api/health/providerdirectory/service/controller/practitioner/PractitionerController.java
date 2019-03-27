package gov.va.api.health.providerdirectory.service.controller.practitioner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.resources.OperationOutcome;
import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderLicenses;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Request Mappings for Location Resource, see
 * http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitioner.html for
 * implementation details.
 */
@Slf4j
@Controller
public class PractitionerController {

  private final RestTemplate restTemplate;

  private Transformer transformer;

  private Bundler bundler;

  public PractitionerController(@Autowired RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private static ObjectMapper objectMapper() {
    return JacksonConfig.createMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
  }

  // TODO: bundle function and search will be fairly different since ProviderDirectory calls out to
  // PPMS endpoint
  private Practitioner.Bundle bundle(
      MultiValueMap<String, String> parameters, int page, int count) {
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
            Collections.emptyList(),
            transformer,
            Practitioner.Entry::new,
            Practitioner.Bundle::new));
  }

  @SneakyThrows
  private ProviderResponse ppmsProvider(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl("https://dev.dws.ppms.va.gov/v1.0/Providers(" + id + ")")
            .build()
            .toUriString();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> entity =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
    String body = entity.getBody();
    log.error(
        "PPMS API response: "
            + objectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper().readTree(body)));
    ProviderResponse responseObject = objectMapper().readValue(body, ProviderResponse.class);
    log.error(
        "response object: "
            + objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseObject));
    return responseObject;
  }

  @SneakyThrows
  private ProviderContacts ppmsProviderContact(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl(
                "https://dev.dws.ppms.va.gov/v1.0/Providers(" + id + ")/ProviderContacts")
            .build()
            .toUriString();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> entity =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
    String body = entity.getBody();
    log.error(
        "PPMS API response: "
            + objectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper().readTree(body)));
    ProviderContacts responseObject = objectMapper().readValue(body, ProviderContacts.class);
    log.error(
        "response object: "
            + objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseObject));
    return responseObject;
  }

  @SneakyThrows
  private ProviderLicenses ppmsProviderLicense(String id) {
    String url =
        UriComponentsBuilder.fromHttpUrl(
                "https://dev.dws.ppms.va.gov/v1.0/Providers(" + id + ")/ProviderLicenses")
            .build()
            .toUriString();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
    ResponseEntity<String> entity =
        restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
    String body = entity.getBody();
    log.error(
        "PPMS API response: "
            + objectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(objectMapper().readTree(body)));
    ProviderLicenses responseObject = objectMapper().readValue(body, ProviderLicenses.class);
    log.error(
        "response object: "
            + objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(responseObject));
    return responseObject;
  }

  /** Search by identifier. */
  @GetMapping(params = {"identifier"})
  public Practitioner.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "1") @Min(0) int count) {
    // ProviderResponse providerResponse = ppmsProvider(identifier);
    return bundle(
        Parameters.builder()
            .add("identifier", identifier)
            .add("page", page)
            .add("_count", count)
            .build(),
        page,
        count);
  }

  // /** Search by family & given name. */
  // @GetMapping(params = {"family", "given"})
  // public Practitioner.Bundle searchByName(
  // @RequestParam("family") String familyName,
  // @RequestParam("given") String givenName,
  // @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
  // @RequestParam(value = "_count", defaultValue = "1") @Min(0) int count) {
  // return bundle(
  // Parameters.builder()
  // .add("family", familyName)
  // .add("given", givenName)
  // .add("page", page)
  // .add("_count", count)
  // .build(),
  // page,
  // count);
  // }
  /** Hey, this is a validate endpoint. It validates. */
  @PostMapping(
    value = "/$validate",
    consumes = {"application/json", "application/json+fhir", "application/fhir+json"}
  )
  public OperationOutcome validate(@RequestBody Practitioner.Bundle bundle) {
    return Validator.create().validate(bundle);
  }

  // TODO: Actually implement transformer
  public interface Transformer extends Function<Practitioner, Practitioner> {}
}
