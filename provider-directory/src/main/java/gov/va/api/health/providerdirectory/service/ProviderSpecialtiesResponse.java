package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class ProviderSpecialtiesResponse implements PpmsResponse {
  @JsonProperty("@odata.context")
  private String odataContext;

  private List<Value> value;

  private Error error;

  /** Lazy getter. */
  public List<Value> getValue() {
    return value;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Value {
    @JsonProperty("CodedSpecialty")
    private String codedSpecialty;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Grouping")
    private String grouping;

    @JsonProperty("Classification")
    private String classification;

    @JsonProperty("Specialization")
    private String specialization;

    @JsonProperty("SpecialtyDescription")
    private String specialtyDescription;

    @JsonProperty("ProviderName")
    private String providerName;

    @JsonProperty("IsPrimaryType")
    private String isPrimaryType;

    @JsonProperty("SpecialtyName")
    private String specialtyName;
  }
}
