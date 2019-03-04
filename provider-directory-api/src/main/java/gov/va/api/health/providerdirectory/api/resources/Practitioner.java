package gov.va.api.health.providerdirectory.api.resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gov.va.api.health.providerdirectory.api.Fhir;
import gov.va.api.health.providerdirectory.api.bundle.AbstractBundle;
import gov.va.api.health.providerdirectory.api.bundle.AbstractEntry;
import gov.va.api.health.providerdirectory.api.bundle.BundleLink;
import gov.va.api.health.providerdirectory.api.datatypes.Address;
import gov.va.api.health.providerdirectory.api.datatypes.Attachment;
import gov.va.api.health.providerdirectory.api.datatypes.CodeableConcept;
import gov.va.api.health.providerdirectory.api.datatypes.ContactPoint;
import gov.va.api.health.providerdirectory.api.datatypes.HumanName;
import gov.va.api.health.providerdirectory.api.datatypes.Identifier;
import gov.va.api.health.providerdirectory.api.datatypes.Period;
import gov.va.api.health.providerdirectory.api.datatypes.Signature;
import gov.va.api.health.providerdirectory.api.datatypes.SimpleResource;
import gov.va.api.health.providerdirectory.api.elements.BackboneElement;
import gov.va.api.health.providerdirectory.api.elements.Element;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import gov.va.api.health.providerdirectory.api.elements.Meta;
import gov.va.api.health.providerdirectory.api.elements.Narrative;
import gov.va.api.health.providerdirectory.api.elements.Reference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(
  description = "http://hl7.org/fhir/practitioner.html"
)
public class Practitioner implements DomainResource {
  @NotBlank String resourceType;

  @Pattern(regexp = Fhir.ID)
  String id;

  @Valid Meta meta;

  @Pattern(regexp = Fhir.URI)
  String implicitRules;

  @Pattern(regexp = Fhir.CODE)
  String language;

  @Valid Narrative text;

  @Valid List<SimpleResource> contained;

  @Valid List<Extension> extension;

  @Valid List<Extension> modifierExtension;

  @NotNull @Valid List<PractitionerIdentifier> identifier;

  boolean active;

  @Valid PractitionerHumanName name;

  @Valid List<ContactPoint> telecom;

  @Valid List<Address> address;

  Gender gender;

  @Pattern(regexp = Fhir.DATE)
  String birthDate;

  @Valid List<Attachment> photo;

  @Valid List<PractitionerRole> practitionerRole;

  @Valid List<Qualification> qualification;

  @Valid List<CodeableConcept> communication;

  public enum Gender {
    male,
    female,
    other,
    unknown
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(builder = Practitioner.Entry.EntryBuilder.class)
  @Schema(name = "PractitionerEntry", example = "SWAGGER_EXAMPLE_PRACTITIONER_BUNDLE")
  public static class Entry extends AbstractEntry<Practitioner> {
    @Builder
    public Entry(
        @Pattern(regexp = Fhir.ID) String id,
        @Valid List<Extension> extension,
        @Valid List<Extension> modifierExtension,
        @Valid List<BundleLink> link,
        @Pattern(regexp = Fhir.URI) String fullUrl,
        @Valid Practitioner resource,
        @Valid Search search,
        @Valid Request request,
        @Valid Response response) {
      super(id, extension, modifierExtension, link, fullUrl, resource, search, request, response);
    }
  }

  @Data
  @EqualsAndHashCode(callSuper = true)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  @JsonDeserialize(builder = Practitioner.Bundle.BundleBuilder.class)
  @Schema(name = "Practitioner")
  public static class Bundle extends AbstractBundle<Entry> {
    @Builder
    public Bundle(
        @NotBlank String resourceType,
        @Pattern(regexp = Fhir.ID) String id,
        @Valid Meta meta,
        @Pattern(regexp = Fhir.URI) String implicitRules,
        @Pattern(regexp = Fhir.CODE) String language,
        @NotNull BundleType type,
        @Min(0) Integer total,
        @Valid List<BundleLink> link,
        @Valid List<Entry> entry,
        @Valid Signature signature) {
      super(resourceType, id, meta, implicitRules, language, type, total, link, entry, signature);
    }
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Qualification implements BackboneElement {
    @Pattern(regexp = Fhir.ID)
    String id;

    @Valid List<Extension> extension;

    @Valid List<Extension> modifierExtension;

    @Valid List<Identifier> identifier;

    @Valid @NotNull CodeableConcept code;

    @Valid Period period;

    @Valid Reference issuer;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class PractitionerHumanName implements Element {
    @Pattern(regexp = Fhir.ID)
    String id;
    @Valid List<Extension> extension;
    HumanName.NameUse use;
    String text;
    @NotNull String family;
    List<String> given;
    List<String> prefix;
    List<String> suffix;
    @Valid Period period;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class PractitionerIdentifier {
    @Pattern(regexp = Fhir.ID)
    String id;

    @Valid List<Extension> extension;

    Identifier.IdentifierUse use;

    @Valid CodeableConcept type;

    @Pattern(regexp = Fhir.URI)
    @NotNull String system;

    @NotNull String value;
    @Valid Period period;
    @Valid Reference assigner;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class PractitionerRole implements BackboneElement {
    @Pattern(regexp = Fhir.ID)
    String id;

    @Valid List<Extension> extension;

    @Valid List<Extension> modifierExtension;

    @Valid Reference managingOrganization;

    @Valid CodeableConcept role;

    @Valid List<CodeableConcept> specialty;

    @Valid Period period;

    @Valid List<Reference> location;

    @Valid List<Reference> healthcareService;
  }
}
