package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
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

  public interface Transformer extends Function<PpmsPractitionerRole, PractitionerRole> {}
}
