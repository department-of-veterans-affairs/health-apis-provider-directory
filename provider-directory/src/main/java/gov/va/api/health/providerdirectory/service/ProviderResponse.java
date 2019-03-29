package gov.va.api.health.providerdirectory.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public final class ProviderResponse {
  @JsonProperty("@odata.context")
  private String odataContext;

  private List<Value> value;

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Value {
    @JsonProperty("Status")
    private String status;

    @JsonProperty("TotalResults")
    private int totalResults;

    @JsonProperty("ReturnedResults")
    private int returnedResults;

    @JsonProperty("PageNumber")
    private int pageNumber;

    @JsonProperty("TotalPages")
    private int totalPages;

    @JsonProperty("Timestamp")
    private String timestamp;

    @JsonProperty("TimeToReturnResultsPPMS")
    private long timeToReturnResultsPPMS;

    @JsonProperty("TimeToMapReturnedResults")
    private long timeToMapReturnedResults;

    @JsonProperty("Providers")
    private List<Provider> providers;
  }

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Provider {
    /* Map to Identifier Value. */
    @JsonProperty("ProviderIdentifier")
    private Integer providerIdentifier;

    /* Map to Identifier System. */
    @JsonProperty("ProviderIdentifierType")
    private String providerIdentifierType;

    /* Map to Family and Given Name. */
    @JsonProperty("Name")
    private String name;

    @JsonProperty("ProviderType")
    private String providerType;

    @JsonProperty("QualityRankingTotalScore")
    private String qualityRankingTotalScore;

    @JsonProperty("QualityRankingLastUpdated")
    private String qualityRankingLastUpdated;

    @JsonProperty("MainPhone")
    private String mainPhone;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("PrimaryDirectMessagingAddress")
    private String primaryDirectMessagingAddress;

    @JsonProperty("SecondaryDirectMessagingAddress")
    private String secondaryDirectMessagingAddress;

    @JsonProperty("EMRSystem")
    private String emrSystem;

    @JsonProperty("EMRSystemOther")
    private String emrSystemOther;

    /* Msp to Address. */
    @JsonProperty("Address")
    private String address;

    @JsonProperty("AddressStreet")
    private String addressStreet;

    @JsonProperty("AddressCity")
    private String addressCity;

    @JsonProperty("AddressStateProvince")
    private String addressStateProvince;

    @JsonProperty("AddressPostalCode")
    private String addressPostalCode;

    @JsonProperty("AddressCounty")
    private String addressCounty;

    @JsonProperty("AddressCountry")
    private String addressCountry;

    /* Map to active. */
    @JsonProperty("ProviderStatusReason")
    private String providerStatusReason;

    @JsonProperty("PrimaryCarePhysician")
    private Boolean primaryCarePhysician;

    @JsonProperty("IsAcceptingNewPatients")
    private Boolean isAcceptingNewPatients;

    @JsonProperty("ProviderEthnicity")
    private String providerEthnicity;

    /* Map to provider gender. */
    @JsonProperty("ProviderGender")
    private String providerGender;

    @JsonProperty("Religion")
    private String religion;

    @JsonProperty("OrganizationId")
    private String organizationId;

    @JsonProperty("ServiceProviderType")
    private String serviceProviderType;

    @JsonProperty("SpecialInstruction")
    private String specialInstruction;

    @JsonProperty("OwnedCareSiteName")
    private String ownedCareSiteName;

    @JsonProperty("OrganizationFax")
    private String organizationFax;

    @JsonProperty("OrganizationStatus")
    private String organizationStatus;

    @JsonProperty("IsExternal")
    private Boolean isExternal;

    @JsonProperty("InternalType")
    private String internalType;

    @JsonProperty("LicensingJuristicion")
    private String licensingJuristicion;

    @JsonProperty("CanCreateHealthCareOrders")
    private String canCreateHealthCareOrders;

    @JsonProperty("InternalAppointmentStatus")
    private String internalAppointmentStatus;

    @JsonProperty("ExternalHealthProviderType")
    private String externalHealthProviderType;

    @JsonProperty("OnLeie")
    private String onLeie;

    @JsonProperty("ExternalInstitutionDeaNumber")
    private String externalInstitutionDeaNumber;

    @JsonProperty("ExternalLeieCheckDate")
    private String externalLeieCheckDate;

    @JsonProperty("ValidationSource")
    private String validationSource;

    @JsonProperty("ContactMethodEmail")
    private Boolean contactMethodEmail;

    @JsonProperty("ContactMethodFax")
    private Boolean contactMethodFax;

    @JsonProperty("ContactMethodVirtuPro")
    private Boolean contactMethodVirtuPro;

    @JsonProperty("ContactMethodHSRM")
    private Boolean contactMethodHsrm;

    @JsonProperty("ContactMethodPhone")
    private Boolean contactMethodPhone;

    @JsonProperty("ContactMethodMail")
    private Boolean contactMethodMail;

    @JsonProperty("ContactMethodRefDoc")
    private Boolean contactMethodRefDoc;

    @JsonProperty("BulkEmails")
    private Boolean bulkEmails;

    @JsonProperty("BulkMails")
    private Boolean bulkMails;

    @JsonProperty("Emails")
    private Boolean emails;

    @JsonProperty("Mails")
    private Boolean mails;

    @JsonProperty("PhoneCalls")
    private Boolean phoneCalls;

    @JsonProperty("Faxes")
    private Boolean faxes;

    @JsonProperty("PreferredMeansReceivingReferralHSRM")
    private Boolean preferredMeansReceivingReferralHsrm;

    @JsonProperty("PreferredMeansReceivingReferralSecuredEmail")
    private Boolean preferredMeansReceivingReferralSecuredEmail;

    @JsonProperty("PreferredMeansReceivingReferralMail")
    private Boolean preferredMeansReceivingReferralMail;

    @JsonProperty("PreferredMeansReceivingReferralDirectMessaging")
    private String preferredMeansReceivingReferralDirectMessaging;

    @JsonProperty("PreferredMeansReceivingReferralFax")
    private Boolean preferredMeansReceivingReferralFax;

    @JsonProperty("Id")
    private String id;

    @JsonProperty("ModifiedOnDate")
    private String modifiedOnDate;

    @JsonProperty("TerminationDate")
    private String terminationDate;
  }
}
