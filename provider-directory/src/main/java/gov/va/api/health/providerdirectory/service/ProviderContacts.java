package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProviderContacts {
  /** Not sure what should map to telecom */
  private String fullName;

  private String companyName;
  private Boolean isVeteran;
  private String contactRole;
  private String emergencyResponderIndicator;
  private Boolean isOtherPartyContact;
  private String providerName;
  private String businessPhone;
  private String mobilePhone;
  private String email;
  private String fax;
  private String preferredMethodOfContact;
  private String deliveryStatus;
  private String address;
  private String addressStreet1;
  private String addressStreet2;
  private String addressCity;
  private String addressState;
  private String addressZip;
  private String addressCountryRegion;
  private Boolean isAddressActive;
  private Boolean isMailingAddress;
  private String addressInvalidReason;
  private String countryCodeName;
  private String gender;
  private String maritalStatus;
  private String spousePartnerName;
  /** Map to birthday */
  private String birthday;

  private String anniversary;
  private String dateOfDeath;
  private Boolean allowEmail;
  private Boolean allowFollowEmail;
  private Boolean allowBulkEmail;
  private Boolean allowPhone;
  private Boolean allowFax;
  private Boolean allowMail;
  private Boolean isTextingAcceptable;
  private String personalNotes;
}
