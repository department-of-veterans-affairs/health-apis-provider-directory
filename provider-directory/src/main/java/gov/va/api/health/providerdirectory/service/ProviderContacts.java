package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties
public class ProviderContacts {

  @JsonProperty("@odata.context")
  private String odataContext;
  private List<Value> value;

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Value {
    /* Not sure what should map to telecom. */
    @JsonProperty("FullName")
    private String fullName;

    @JsonProperty("CompanyName")
    private String companyName;

    @JsonProperty("IsVeteran")
    private Boolean isVeteran;

    @JsonProperty("ContactRole")
    private String contactRole;

    @JsonProperty("EmergencyResponderIndicator")
    private String emergencyResponderIndicator;

    @JsonProperty("IsOtherPartyContact")
    private Boolean isOtherPartyContact;

    @JsonProperty("ProviderName")
    private String providerName;

    @JsonProperty("BusinessPhone")
    private String businessPhone;

    @JsonProperty("MobilePhone")
    private String mobilePhone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Fax")
    private String fax;

    @JsonProperty("PreferredMethodOfContact")
    private String preferredMethodOfContact;

    @JsonProperty("DeliveryStatus")
    private String deliveryStatus;

    @JsonProperty("Address")
    private String address;

    @JsonProperty("AddressStreet1")
    private String addressStreet1;

    @JsonProperty("AddressStreet2")
    private String addressStreet2;

    @JsonProperty("AddressCity")
    private String addressCity;

    @JsonProperty("AddressState")
    private String addressState;

    @JsonProperty("AddressZip")
    private String addressZip;

    @JsonProperty("AddressCountryRegion")
    private String addressCountryRegion;

    @JsonProperty("IsAddressActive")
    private Boolean isAddressActive;

    @JsonProperty("IsMailingAddress")
    private Boolean isMailingAddress;

    @JsonProperty("AddressInvalidReason")
    private String addressInvalidReason;

    @JsonProperty("CountryCodeName")
    private String countryCodeName;

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("MaritalStatus")
    private String maritalStatus;

    @JsonProperty("SpousePartnerName")
    private String spousePartnerName;
    /* Map to birthday. */
    @JsonProperty("Birthday")
    private String birthday;

    @JsonProperty("Anniversary")
    private String anniversary;

    @JsonProperty("DateOfDeath")
    private String dateOfDeath;

    @JsonProperty("AllowEmail")
    private Boolean allowEmail;

    @JsonProperty("AllowFollowEmail")
    private Boolean allowFollowEmail;

    @JsonProperty("AllowBulkEmail")
    private Boolean allowBulkEmail;

    @JsonProperty("AllowPhone")
    private Boolean allowPhone;

    @JsonProperty("AllowFax")
    private Boolean allowFax;

    @JsonProperty("AllowMail")
    private Boolean allowMail;

    @JsonProperty("IsTextingAcceptable")
    private Boolean isTextingAcceptable;

    @JsonProperty("PersonalNotes")
    private String personalNotes;
  }
}
