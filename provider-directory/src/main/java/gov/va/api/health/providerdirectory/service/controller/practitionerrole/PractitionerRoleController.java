package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

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
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.PageLinks;
import gov.va.api.health.providerdirectory.service.controller.Parameters;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PpmsClient;
import lombok.AllArgsConstructor;
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

    // PD PractitionerRole	PPMS Fields(s)
    // active			Providers.ProviderStatusReason
    // period			Providers(Identifier)/ProviderLicenses.ExpirationDate
    // practitioner		Reference to Practitioner
    // organization		Reference to Organization
    // code				Providers(Identifier)?$expand=ProviderSpecialties.SpecialtyCode
    // specialty		Providers(Identifier)?$expand=ProviderSpecialties
    // location			Providers(Identifier)?$expand=CareSites
    // healthcareService	HealthcareService is another resource, FHIR R4, not required
    // telecom					Providers(Identifier)/ProviderContacts
    // availableTime			Possibly ServiceAvailabilities, not available in PPMS, not req
    // notAvailable				Possibly ServiceAvailabilities, not available in PPMS, not req
    // availabiltyExceptions	Possibly ServiceAvailabilities, not available in PPMS, not req
    // endpoint				Reference to endpoint

    //  GET [base]/PractitionerRole?practitioner.identifier=[system]|[code]

    //	  @NotBlank String resourceType;
    //	  @Pattern(regexp = Fhir.ID)
    //	  String id;
    //	  @Valid Meta meta;
    //	  @Pattern(regexp = Fhir.URI)
    //	  String implicitRules;
    //	  @Pattern(regexp = Fhir.CODE)
    //	  String language;
    //	  @Valid Narrative text;
    //	  @Valid List<SimpleResource> contained;
    //	  @Valid List<Extension> modifierExtension;
    //	  @Valid List<Extension> extension;
    //	  @Valid List<Identifier> identifier;
    //	  Boolean active;
    //	  @Valid Period period;
    //	  @Valid @NotNull Reference practitioner;
    //	  @Valid @NotNull Reference organization;
    //	  @Valid @NotNull CodeableConcept code;
    //	  @Valid @NotNull CodeableConcept specialty;
    //	  @Valid List<Reference> location;
    //	  @Valid List<Reference> healthcareService;
    //	  @Valid List<PractitionerContactPoint> telecom;
    //	  List<PractitionerAvailableTime> availableTime;
    //	  List<PractitionerNotAvailable> notAvailable;
    //	  String availabilityExceptions;
    //	  @Valid List<Reference> endpoint;

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
            Collections.emptyList(),
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
