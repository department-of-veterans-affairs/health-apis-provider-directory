package gov.va.api.health.providerdirectory.api.resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.health.providerdirectory.api.Fhir;
import gov.va.api.health.providerdirectory.api.datatypes.CodeableConcept;
import gov.va.api.health.providerdirectory.api.datatypes.SimpleResource;
import gov.va.api.health.providerdirectory.api.elements.BackboneElement;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import gov.va.api.health.providerdirectory.api.elements.Meta;
import gov.va.api.health.providerdirectory.api.elements.Narrative;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(
  description = "https://www.hl7.org/fhir/STU3/operationoutcome.html"
)
public class OperationOutcome implements DomainResource {

  @Pattern(regexp = Fhir.ID)
  String id;

  @NotBlank String resourceType;
  @Valid Meta meta;

  @Pattern(regexp = Fhir.URI)
  String implicitRules;

  @Pattern(regexp = Fhir.CODE)
  String language;

  @Valid Narrative text;

  @Valid List<SimpleResource> contained;

  @Valid List<Extension> modifierExtension;

  @Valid List<Extension> extension;

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @Schema(
    description =
        "https://www.hl7.org/fhir/STU3/operationoutcome-definitions.html#OperationOutcome.issue"
  )
  public static class Issue implements BackboneElement {
    @Pattern(regexp = Fhir.ID)
    String id;

    @Valid List<Extension> modifierExtension;

    @Valid List<Extension> extension;

    @NotNull IssueSeverity severity;

    @NotBlank
    @Pattern(regexp = Fhir.CODE)
    String code;

    @Valid CodeableConcept details;

    String diagnostics;

    List<String> location;

    List<String> expression;

    @SuppressWarnings("unused")
    public enum IssueSeverity {
      fatal,
      error,
      warning,
      information
    }
  }
}
