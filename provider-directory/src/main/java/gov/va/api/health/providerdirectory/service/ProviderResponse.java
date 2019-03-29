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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)

public class ProviderResponse {
  @JsonProperty("@odata.context")
  private String odatacontext;

  private List<Value> value;

  @Data
  @Builder
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static class Value {
    /* Map to Identifier Value. */
    private Integer providerIdentifier;
    /* Map to Identifier System. */
    private String providerIdentifierType;
    /* Map to Family and Given Name. */
    private String name;

    private String qualityRankingTotalScore;
    private String qualityRankingLastUpdated;
    private String mainPhone;
    private String email;
    private String primaryDirectMessagingAddress;
    private String secondaryDirectMessagingAddress;
    private String emrSystem;
    private String emrSystemOther;
    /* Msp to Address. */
    private String address;

    private String addressStreet;
    private String addressCity;
    private String addressStateProvince;
    private String addressPostalCode;
    private String addressCounty;
    private String addressCountry;
    /* Map to active. */
    private String providerStatusReason;

    private Boolean primaryCarePhysician;
    private Boolean isAcceptingNewPatients;
    private String providerEthnicity;
    /* Map to provider gender. */
    private String providerGender;

    private String religion;
    private String organizationId;
    private String serviceProviderType;
    private String specialInstruction;
    private String ownedCareSiteName;
    private String organizationFax;
    private String organizationStatus;
    private Boolean isExternal;
    private String internalType;
    private String licensingJuristicion;
    private String canCreateHealthCareOrders;
    private String internalAppointmentStatus;
    private String externalHealthProviderType;
    private String onLeie;
    private String externalInstitutionDeaNumber;
    private String externalLeieCheckDate;
    private String validationSource;
    private Boolean contactMethodEmail;
    private Boolean contactMethodFax;
    private Boolean contactMethodVirtuPro;
    private Boolean contactMethodHsrm;
    private Boolean contactMethodPhone;
    private Boolean contactMethodMail;
    private Boolean contactMethodRefDoc;
    private Boolean bulkEmails;
    private Boolean bulkMails;
    private Boolean emails;
    private Boolean mails;
    private Boolean phoneCalls;
    private Boolean faxes;
    private Boolean preferredMeansReceivingReferralHsrm;
    private Boolean preferredMeansReceivingReferralSecuredEmail;
    private Boolean preferredMeansReceivingReferralMail;
    private String preferredMeansReceivingReferralDirectMessaging;
    private Boolean preferredMeansReceivingReferralFax;
    private String id;
    private String modifiedOnDate;
    private String terminationDate;
  }
}
