package gov.va.api.health.providerdirectory.api.datatypes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.health.providerdirectory.api.Fhir;
import gov.va.api.health.providerdirectory.api.elements.Element;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "http://hl7.org/fhir/DSTU2/datatypes.html#Attachment")
public class Attachment implements Element {
  @Pattern(regexp = Fhir.ID)
  String id;

  @Valid List<Extension> extension;

  @Pattern(regexp = Fhir.CODE)
  String contentType;

  @Pattern(regexp = Fhir.CODE)
  String language;

  @Pattern(regexp = Fhir.BASE64)
  String data;

  @Pattern(regexp = Fhir.URI)
  String url;

  @Min(0)
  Integer size;

  @Pattern(regexp = Fhir.BASE64)
  String hash;

  String title;

  @Pattern(regexp = Fhir.DATETIME)
  String creation;
}
