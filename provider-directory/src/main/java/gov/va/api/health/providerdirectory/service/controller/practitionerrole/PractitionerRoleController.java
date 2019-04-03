package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.Fhir;
import gov.va.api.health.providerdirectory.api.datatypes.CodeableConcept;
import gov.va.api.health.providerdirectory.api.datatypes.Identifier;
import gov.va.api.health.providerdirectory.api.datatypes.Period;
import gov.va.api.health.providerdirectory.api.datatypes.SimpleResource;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import gov.va.api.health.providerdirectory.api.elements.Meta;
import gov.va.api.health.providerdirectory.api.elements.Narrative;
import gov.va.api.health.providerdirectory.api.elements.Reference;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole.PractitionerAvailableTime;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole.PractitionerContactPoint;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole.PractitionerNotAvailable;
import gov.va.api.health.providerdirectory.service.PpmsPractitionerRole;
import gov.va.api.health.providerdirectory.service.PpmsProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.PageLinks;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PpmsClient;
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

  /** Read by identifier. */
  @GetMapping(value = {"/{publicId}"})
  public PractitionerRole read(@PathVariable("publicId") String publicId) {
    return null;

    // PETERTODO make calls to get care sites and contacts

    //    if (BooleanUtils.isTrue(BooleanUtils.toBooleanObject(database20Mode))) {
    //      return jpaRead(publicId);
    //    }
    //    CdwDiagnosticReport102Root mrAndersonCdw =
    // mrAndersonSearch(Parameters.forIdentity(publicId));
    //    DiagnosticReport mrAndersonReport =
    //        transformer.apply(
    //            firstPayloadItem(
    //                hasPayload(mrAndersonCdw.getDiagnosticReports()).getDiagnosticReport()));
    //    if ("both".equalsIgnoreCase(database20Mode)) {
    //      DiagnosticReport jpaReport = jpaRead(publicId);
    //      if (!jpaReport.equals(mrAndersonReport)) {
    //        log.warn("jpa read and mr-anderson read do not match.");
    //        log.warn("jpa report is {}",
    // JacksonConfig.createMapper().writeValueAsString(jpaReport));
    //        log.warn(
    //            "mr-anderson report is {}",
    //            JacksonConfig.createMapper().writeValueAsString(mrAndersonReport));
    //      }
    //    }
    //    return mrAndersonReport;
  }

  /** Search by identifier. */
  @SneakyThrows
  @GetMapping(params = {"identifier"})
  public PractitionerRole.Bundle searchByIdentifier(
      @RequestParam("identifier") String identifier,
      @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
      @RequestParam(value = "_count", defaultValue = "15") @Min(0) int count) {
    MultiValueMap<String, String> parameters =
        Parameters.builder()
            .add("identifier", identifier)
            .add("page", page)
            .add("_count", count)
            .build();

    // call https://dws.ppms.va.gov/v1.0/Providers(1679592604)
    // call https://dev.dws.ppms.va.gov/v1.0/Providers(1285621557)/ProviderServices

    //  GET [base]/PractitionerRole?practitioner.identifier=[system]|[code]
    ProviderResponse providerResponse =
        JacksonConfig.createMapper()
            .readValue(new File("c:/tmp/sample-providers.json"), ProviderResponse.class);
    log.error(
        JacksonConfig.createMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(providerResponse));

    PpmsProviderSpecialtiesResponse ppmsProviderSpecialtiesResponse =
        JacksonConfig.createMapper()
            .readValue(
                new File("c:/tmp/sample-provider-specialties.json"),
                PpmsProviderSpecialtiesResponse.class);
    log.error(
        JacksonConfig.createMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(ppmsProviderSpecialtiesResponse));

    if (true) {
      return null;
    }

    PpmsPractitionerRole ppmsPractitionerRole =
        PpmsPractitionerRole.builder()
            .providerResponse(providerResponse)
            .providerSpecialtiesResponse(ppmsProviderSpecialtiesResponse)
            .build();

    int totalRecords = 0;
    PageLinks.LinkConfig linkConfig =
        PageLinks.LinkConfig.builder()
            .path("DiagnosticReport")
            .queryParams(parameters)
            .page(page)
            .recordsPerPage(count)
            .totalRecords(totalRecords)
            .build();
    return bundler.bundle(
        BundleContext.of(
            linkConfig,
            Collections.singletonList(ppmsPractitionerRole),
            transformer,
            PractitionerRole.Entry::new,
            PractitionerRole.Bundle::new));
  }

  //  GET [base]/PractitionerRole?practitioner.family=[string]&given=[string]
  // {&_include=PractitionerRole:practitioner&_include=PractitionerRole:endpoint}
  //
  //  GET [base]/PractitionerRole?specialty=[system]|[code]
  // {&_include=PractitionerRole:practitioner&_include=PractitionerRole:endpoint}

  public interface Transformer extends Function<PpmsPractitionerRole, PractitionerRole> {}
}
