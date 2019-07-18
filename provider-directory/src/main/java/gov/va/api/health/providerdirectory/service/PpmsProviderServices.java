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
public final class PpmsProviderServices {
  @JsonProperty("@odata.context")
  private String odataContext;

  private List<Value> value;

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Value {

    @JsonProperty("Name")
    private String name;

    @JsonProperty("AffiliationName")
    private String affiliationName;

    @JsonProperty("RelationshipName")
    private String relationshipName;

    @JsonProperty("ProviderName")
    private String providerName;

    @JsonProperty("ProviderAgreementName")
    private String providerAgreementName;

    @JsonProperty("SpecialtyName")
    private String specialtyName;

    @JsonProperty("HPP")
    private String hpp;

    @JsonProperty("HighPerformingProvider")
    private String highPerformingProvider;

    @JsonProperty("CareSiteName")
    private String careSiteName;

    @JsonProperty("CareSiteAddressStreet")
    private String careSiteAddressStreet;

    @JsonProperty("CareSiteLocationAddress")
    private String careSiteLocationAddress;

    @JsonProperty("CareSiteAddressCity")
    private String careSiteAddressCity;

    @JsonProperty("CareSiteAddressState")
    private String careSiteAddressState;

    @JsonProperty("CareSiteAddressZipCode")
    private String careSiteAddressZipCode;

    @JsonProperty("Latitude")
    private String latitude;

    @JsonProperty("Longitude")
    private String longitude;

    @JsonProperty("CareSitePhoneNumber")
    private String careSitePhoneNumber;

    @JsonProperty("OrganiztionGroupName")
    private String organiztionGroupName;

    @JsonProperty("DescriptionOfService")
    private String descriptionOfService;

    @JsonProperty("Limitation")
    private String limitation;
  }
}
