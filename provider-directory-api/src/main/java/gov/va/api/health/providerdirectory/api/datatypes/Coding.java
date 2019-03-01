package gov.va.api.health.providerdirectory.api.datatypes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.health.providerdirectory.api.Fhir;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "http://hl7.org/fhir/DSTU2/datatypes.html#Coding")
public class Coding {
  @Pattern(regexp = Fhir.URI)
  String system;

  String version;

  @Pattern(regexp = Fhir.CODE)
  String code;

  String display;

  Boolean userSelected;

  /** All-args builder constructor. */
  @Builder
  public Coding(String system, String version, String code, String display, Boolean userSelected) {
    this.system = defaultIfBlank(system, null);
    this.version = defaultIfBlank(version, null);
    this.code = defaultIfBlank(code, null);
    this.display = defaultIfBlank(display, null);
    this.userSelected = userSelected;
  }
}
