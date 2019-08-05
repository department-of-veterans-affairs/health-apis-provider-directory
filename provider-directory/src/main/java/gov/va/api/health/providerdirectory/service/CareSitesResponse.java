package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
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
public final class CareSitesResponse implements PpmsResponse {
  @JsonProperty("@odata.context")
  private String odataContext;

  private List<Value> value;

  private Error error;

  /** Lazy getter. */
  public List<Value> value() {
    if (value == null) {
      value = new ArrayList<>();
    }
    return value;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Value {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("CareSiteType")
    private String careSiteType;

    @JsonProperty("VaCareSite")
    private Boolean vaCareSite;

    @JsonProperty("CenterOfExcellence")
    private String centerOfExcellence;

    @JsonProperty("SiteContactName")
    private String siteContactName;

    @JsonProperty("OwningOrganizationName")
    private String owningOrganizationName;

    @JsonProperty("OtherName")
    private String otherName;

    @JsonProperty("IsHandicapAccessible")
    private Boolean isHandicapAccessible;

    @JsonProperty("IsExternal")
    private Boolean isExternal;

    @JsonProperty("Street")
    private String street;

    @JsonProperty("City")
    private String city;

    @JsonProperty("State")
    private String state;

    @JsonProperty("ZipCode")
    private String zipCode;

    @JsonProperty("County")
    private String county;

    @JsonProperty("Country")
    private String country;

    @JsonProperty("Latitude")
    private Double latitude;

    @JsonProperty("Longitude")
    private Double longitude;

    @JsonProperty("Geocoded")
    private Boolean geocoded;

    @JsonProperty("Visn")
    private String visn;

    @JsonProperty("Facility")
    private String facility;

    @JsonProperty("ParentStationNumber")
    private String parentStationNumber;

    @JsonProperty("StationNumber")
    private String stationNumber;

    @JsonProperty("StationName")
    private String stationName;

    @JsonProperty("MainSitePhone")
    private String mainSitePhone;
  }
}
