package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProviderResponse {
  /** Map to Identifier Value* */
  private String providerIdentifier;
  /** Map to Identifier System */
  private String providerIdentifierType;
  /** Map to Family and Given Name* */
  private String name;

  private String qualityRankingTotalScore;
  private String qualityRankingLastUpdated;
  private String mainPhone;
  private String email;
  private String primaryDirectMessagingAddress;
  private String secondaryDirectMessagingAddress;
  private String emrSystem;
  private String emrSytemOther;
  /** Msp to Address */
  private String address;

  private String addressStreet;
  private String addressCity;
  private String addressStateProvince;
  private String addressPostalCode;
  private String addressCounty;
  private String addressCountry;
  /** Map to active* */
  private String providerStatusReason;

  private Boolean primaryCarePhysician;
  private Boolean isAcceptingNewPatients;
  private String providerEthnicity;
  /** Map to provider gender */
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
  private Boolean contactMethodHSRM;
  private Boolean contactMethodPhone;
  private Boolean contactMethodMail;
  private Boolean contactMethodRefDoc;
  private Boolean bulkEmails;
  private Boolean bulkMails;
  private Boolean emails;
  private Boolean mails;
  private Boolean phoneCalls;
  private Boolean faxes;
  private Boolean preferredMeansReceivingReferralHSRM;
  private Boolean preferredMeansReceivingReferralSecuredEmail;
  private Boolean preferredMeansReceivingReferralMail;
  private String preferredMeansReceivingReferralDirectMessaging;
  private Boolean preferredMeansReceivingReferralFax;
  private String id;
  private String modifiedOnDate;
  private String terminationDate;
}
