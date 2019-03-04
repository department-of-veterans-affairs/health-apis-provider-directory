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
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "http://hl7.org/fhir/DSTU2/datatypes.html#Address")
public class Address implements Element {
  @Pattern(regexp = Fhir.ID)
  String id;

  @Valid List<Extension> extension;

  AddressUse use;
  AddressType type;
  String text;
  List<String> line;
  String city;
  String district;
  String state;
  String postalCode;
  String country;
  @Valid Period period;

  @SuppressWarnings("unused")
  public enum AddressUse {
    home,
    work,
    temp,
    old
  }

  @SuppressWarnings("unused")
  public enum AddressType {
    postal,
    physical,
    both
  }
}