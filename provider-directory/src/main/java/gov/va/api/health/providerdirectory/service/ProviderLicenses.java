package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties
public class ProviderLicenses {
  @JsonProperty("@odata.context")
  private String odataContext;
  List<Value> value;

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Value {
    @JsonProperty("LicenseNumber")
    String licenseNumber;
    @JsonProperty("LicensingState")
    String licensingState;
    @JsonProperty("ExpirationDate")
    String expirationDate;
  }
}
