package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.stu3.api.datatypes.CodeableConcept;
import gov.va.api.health.stu3.api.datatypes.Coding;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.elements.Reference;
import gov.va.api.health.stu3.api.resources.PractitionerRole;
import gov.va.api.health.stu3.api.resources.PractitionerRole.Bundle;
import org.junit.Test;

@SuppressWarnings("WeakerAccess")
public class PractitionerRoleControllerTest {
  PpmsClient ppmsClient = mock(PpmsClient.class);

  PractitionerRoleController controller =
      new PractitionerRoleController(
          ppmsClient,
          new PractitionerRoleTransformer(),
          new Bundler(new ConfigurableBaseUrlPageLinks("", "")));

  @Test
  public void readByIdentifier() {
    when(ppmsClient.providersForId("identifier"))
        .thenReturn(
            ProviderResponse.builder()
                .value(
                    asList(
                        ProviderResponse.Value.builder()
                            .providerIdentifier(1285621557)
                            .providerIdentifierType("Npi")
                            .name("Klingerman, Michael")
                            .providerType("Individual")
                            .providerStatusReason("Active")
                            .primaryCarePhysician(false)
                            .isAcceptingNewPatients(true)
                            .providerGender("Male")
                            .isExternal(true)
                            .contactMethodEmail(false)
                            .contactMethodFax(false)
                            .contactMethodVirtuPro(false)
                            .contactMethodHsrm(false)
                            .contactMethodPhone(false)
                            .contactMethodMail(false)
                            .contactMethodRefDoc(false)
                            .bulkEmails(true)
                            .bulkMails(false)
                            .emails(true)
                            .mails(true)
                            .phoneCalls(true)
                            .faxes(true)
                            .preferredMeansReceivingReferralHsrm(false)
                            .preferredMeansReceivingReferralSecuredEmail(false)
                            .preferredMeansReceivingReferralMail(false)
                            .preferredMeansReceivingReferralFax(false)
                            .modifiedOnDate("2019-02-09T02:08:16Z")
                            .build()))
                .build());

    when(ppmsClient.providerContactsForId("1285621557"))
        .thenReturn(
            ProviderContactsResponse.builder()
                .value(
                    asList(
                        ProviderContactsResponse.Value.builder()
                            .fullName("Dustin Lehman")
                            .companyName("NEWBURYPORT MA RADIATION CENTER LLC")
                            .isVeteran(false)
                            .contactRole("Other")
                            .isOtherPartyContact(false)
                            .providerName("NEWBURYPORT MA RADIATION CENTER LLC")
                            .email("dustin.lehman@email.com")
                            .preferredMethodOfContact("Any")
                            .deliveryStatus("NoProblemsHaveOccurred")
                            .isAddressActive(false)
                            .isMailingAddress(false)
                            .addressInvalidReason("AddressDoesNotExist")
                            .gender("NotSpecified")
                            .maritalStatus("Single")
                            .allowEmail(false)
                            .allowFollowEmail(false)
                            .allowBulkEmail(false)
                            .allowPhone(false)
                            .allowFax(false)
                            .allowMail(false)
                            .isTextingAcceptable(false)
                            .build()))
                .build());

    when(ppmsClient.providerSpecialtySearch("1285621557"))
        .thenReturn(
            ProviderSpecialtiesResponse.builder()
                .value(
                    asList(
                        ProviderSpecialtiesResponse.Value.builder()
                            .codedSpecialty("207R00000X")
                            .name("Internal Medicine")
                            .grouping("Allopathic & Osteopathic Physicians")
                            .classification("Internal Medicine")
                            .specialtyDescription(
                                "A physician who provides long-term, comprehensive care in the office and the hospital, managing both common and complex illness of adolescents, adults and the elderly. Internists are trained in the diagnosis and treatment of cancer, infections and diseases affecting the heart, blood, kidneys, joints and digestive, respiratory and vascular systems. They are also trained in the essentials of primary care internal medicine, which incorporates an understanding of disease prevention, wellness, substance abuse, mental health and effective treatment of common problems of the eyes, ears, skin, nervous system and reproductive organs.")
                            .providerName("Klingerman, Michael ")
                            .isPrimaryType("Yes")
                            .specialtyName("Internal Medicine")
                            .build()))
                .build());

    PractitionerRole expected = controller.readByIdentifier("identifier");
    assertThat(expected)
        .isEqualTo(
            PractitionerRole.builder()
                .resourceType("PractitionerRole")
                .id("1285621557")
                .active(true)
                .practitioner(Reference.builder().reference("null/Practitioner/1285621557").build())
                .code(
                    CodeableConcept.builder()
                        .coding(
                            asList(
                                Coding.builder()
                                    .code("207R00000X")
                                    .display("Internal Medicine")
                                    .build()))
                        .build())
                .telecom(
                    asList(
                        PractitionerRole.PractitionerContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.email)
                            .value("dustin.lehman@email.com")
                            .build()))
                .build());
  }

  @Test
  public void searchByFamilyAndGiven() {
    when(ppmsClient.providersForName("Klingerman"))
        .thenReturn(
            ProviderResponse.builder()
                .value(
                    asList(
                        ProviderResponse.Value.builder()
                            .providerIdentifier(1285621557)
                            .providerIdentifierType("Npi")
                            .name("Klingerman, Michael")
                            .providerType("Individual")
                            .providerStatusReason("Active")
                            .primaryCarePhysician(false)
                            .isAcceptingNewPatients(true)
                            .providerGender("Male")
                            .isExternal(true)
                            .contactMethodEmail(false)
                            .contactMethodFax(false)
                            .contactMethodVirtuPro(false)
                            .contactMethodHsrm(false)
                            .contactMethodPhone(false)
                            .contactMethodMail(false)
                            .contactMethodRefDoc(false)
                            .bulkEmails(true)
                            .bulkMails(false)
                            .emails(true)
                            .mails(true)
                            .phoneCalls(true)
                            .faxes(true)
                            .preferredMeansReceivingReferralHsrm(false)
                            .preferredMeansReceivingReferralSecuredEmail(false)
                            .preferredMeansReceivingReferralMail(false)
                            .preferredMeansReceivingReferralFax(false)
                            .modifiedOnDate("2019-02-09T02:08:16Z")
                            .build()))
                .build());

    when(ppmsClient.providerContactsForId("1285621557"))
        .thenReturn(
            ProviderContactsResponse.builder()
                .value(
                    asList(
                        ProviderContactsResponse.Value.builder()
                            .fullName("Dustin Lehman")
                            .companyName("NEWBURYPORT MA RADIATION CENTER LLC")
                            .isVeteran(false)
                            .contactRole("Other")
                            .isOtherPartyContact(false)
                            .providerName("NEWBURYPORT MA RADIATION CENTER LLC")
                            .email("dustin.lehman@email.com")
                            .preferredMethodOfContact("Any")
                            .deliveryStatus("NoProblemsHaveOccurred")
                            .isAddressActive(false)
                            .isMailingAddress(false)
                            .addressInvalidReason("AddressDoesNotExist")
                            .gender("NotSpecified")
                            .maritalStatus("Single")
                            .allowEmail(false)
                            .allowFollowEmail(false)
                            .allowBulkEmail(false)
                            .allowPhone(false)
                            .allowFax(false)
                            .allowMail(false)
                            .isTextingAcceptable(false)
                            .build()))
                .build());

    when(ppmsClient.providerSpecialtySearch("1285621557"))
        .thenReturn(
            ProviderSpecialtiesResponse.builder()
                .value(
                    asList(
                        ProviderSpecialtiesResponse.Value.builder()
                            .codedSpecialty("207R00000X")
                            .name("Internal Medicine")
                            .grouping("Allopathic & Osteopathic Physicians")
                            .classification("Internal Medicine")
                            .specialtyDescription(
                                "A physician who provides long-term, comprehensive care in the office and the hospital, managing both common and complex illness of adolescents, adults and the elderly. Internists are trained in the diagnosis and treatment of cancer, infections and diseases affecting the heart, blood, kidneys, joints and digestive, respiratory and vascular systems. They are also trained in the essentials of primary care internal medicine, which incorporates an understanding of disease prevention, wellness, substance abuse, mental health and effective treatment of common problems of the eyes, ears, skin, nervous system and reproductive organs.")
                            .providerName("Klingerman, Michael ")
                            .isPrimaryType("Yes")
                            .specialtyName("Internal Medicine")
                            .build()))
                .build());

    Bundle actual = controller.searchByFamilyAndGiven("Klingerman", "Michael", 1, 1);

    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            PractitionerRole.builder()
                .resourceType("PractitionerRole")
                .id("1285621557")
                .active(true)
                .practitioner(Reference.builder().reference("null/Practitioner/1285621557").build())
                .code(
                    CodeableConcept.builder()
                        .coding(
                            asList(
                                Coding.builder()
                                    .code("207R00000X")
                                    .display("Internal Medicine")
                                    .build()))
                        .build())
                .telecom(
                    asList(
                        PractitionerRole.PractitionerContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.email)
                            .value("dustin.lehman@email.com")
                            .build()))
                .build());
  }

  @Test
  public void searchByIdentifier() {
    when(ppmsClient.providersForId("identifier"))
        .thenReturn(
            ProviderResponse.builder()
                .value(
                    asList(
                        ProviderResponse.Value.builder()
                            .providerIdentifier(1285621557)
                            .providerIdentifierType("Npi")
                            .name("Klingerman, Michael")
                            .providerType("Individual")
                            .providerStatusReason("Active")
                            .primaryCarePhysician(false)
                            .isAcceptingNewPatients(true)
                            .providerGender("Male")
                            .isExternal(true)
                            .contactMethodEmail(false)
                            .contactMethodFax(false)
                            .contactMethodVirtuPro(false)
                            .contactMethodHsrm(false)
                            .contactMethodPhone(false)
                            .contactMethodMail(false)
                            .contactMethodRefDoc(false)
                            .bulkEmails(true)
                            .bulkMails(false)
                            .emails(true)
                            .mails(true)
                            .phoneCalls(true)
                            .faxes(true)
                            .preferredMeansReceivingReferralHsrm(false)
                            .preferredMeansReceivingReferralSecuredEmail(false)
                            .preferredMeansReceivingReferralMail(false)
                            .preferredMeansReceivingReferralFax(false)
                            .modifiedOnDate("2019-02-09T02:08:16Z")
                            .build()))
                .build());

    when(ppmsClient.providerContactsForId("1285621557"))
        .thenReturn(
            ProviderContactsResponse.builder()
                .value(
                    asList(
                        ProviderContactsResponse.Value.builder()
                            .fullName("Dustin Lehman")
                            .companyName("NEWBURYPORT MA RADIATION CENTER LLC")
                            .isVeteran(false)
                            .contactRole("Other")
                            .isOtherPartyContact(false)
                            .providerName("NEWBURYPORT MA RADIATION CENTER LLC")
                            .email("dustin.lehman@email.com")
                            .preferredMethodOfContact("Any")
                            .deliveryStatus("NoProblemsHaveOccurred")
                            .isAddressActive(false)
                            .isMailingAddress(false)
                            .addressInvalidReason("AddressDoesNotExist")
                            .gender("NotSpecified")
                            .maritalStatus("Single")
                            .allowEmail(false)
                            .allowFollowEmail(false)
                            .allowBulkEmail(false)
                            .allowPhone(false)
                            .allowFax(false)
                            .allowMail(false)
                            .isTextingAcceptable(false)
                            .build()))
                .build());

    when(ppmsClient.providerSpecialtySearch("1285621557"))
        .thenReturn(
            ProviderSpecialtiesResponse.builder()
                .value(
                    asList(
                        ProviderSpecialtiesResponse.Value.builder()
                            .codedSpecialty("207R00000X")
                            .name("Internal Medicine")
                            .grouping("Allopathic & Osteopathic Physicians")
                            .classification("Internal Medicine")
                            .specialtyDescription(
                                "A physician who provides long-term, comprehensive care in the office and the hospital, managing both common and complex illness of adolescents, adults and the elderly. Internists are trained in the diagnosis and treatment of cancer, infections and diseases affecting the heart, blood, kidneys, joints and digestive, respiratory and vascular systems. They are also trained in the essentials of primary care internal medicine, which incorporates an understanding of disease prevention, wellness, substance abuse, mental health and effective treatment of common problems of the eyes, ears, skin, nervous system and reproductive organs.")
                            .providerName("Klingerman, Michael ")
                            .isPrimaryType("Yes")
                            .specialtyName("Internal Medicine")
                            .build()))
                .build());

    Bundle actual = controller.searchByIdentifier("identifier", 1, 1);

    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            PractitionerRole.builder()
                .resourceType("PractitionerRole")
                .id("1285621557")
                .active(true)
                .practitioner(Reference.builder().reference("null/Practitioner/1285621557").build())
                .code(
                    CodeableConcept.builder()
                        .coding(
                            asList(
                                Coding.builder()
                                    .code("207R00000X")
                                    .display("Internal Medicine")
                                    .build()))
                        .build())
                .telecom(
                    asList(
                        PractitionerRole.PractitionerContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.email)
                            .value("dustin.lehman@email.com")
                            .build()))
                .build());
  }
}
