package gov.va.api.health.providerdirectory.api.datatypes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.health.providerdirectory.api.Fhir;
import gov.va.api.health.providerdirectory.api.elements.Element;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import gov.va.api.health.providerdirectory.api.elements.Reference;
import gov.va.api.health.providerdirectory.api.validation.ZeroOrOneOf;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "http://hl7.org/fhir/DSTU2/datatypes.html#Signature")
@ZeroOrOneOf(fields = {"whoUri", "whoReference"})
public class Signature implements Element {
  @Pattern(regexp = Fhir.ID)
  String id;

  @Valid List<Extension> extension;

  @Valid @NotEmpty List<Coding> type;

  @NotNull
  @Pattern(regexp = Fhir.INSTANT)
  String when;

  @Pattern(regexp = Fhir.URI)
  String whoUri;

  @Valid Reference whoReference;

  @NotEmpty
  @Pattern(regexp = Fhir.CODE)
  String contentType;

  @NotEmpty
  @Pattern(regexp = Fhir.BASE64)
  String blob;
}