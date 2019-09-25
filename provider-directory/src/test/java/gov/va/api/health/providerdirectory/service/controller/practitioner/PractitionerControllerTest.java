package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import gov.va.api.health.stu3.api.bundle.AbstractBundle;
import gov.va.api.health.stu3.api.bundle.AbstractEntry;
import gov.va.api.health.stu3.api.bundle.BundleLink;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.resources.Practitioner;
import javax.validation.ConstraintViolationException;
import org.junit.Test;

public final class PractitionerControllerTest {
  PpmsClient ppmsClient = mock(PpmsClient.class);

  PractitionerController controller =
      new PractitionerController(
          new PractitionerTransformer(),
          new Bundler(new ConfigurableBaseUrlPageLinks("", "")),
          ppmsClient);

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
                            .mainPhone("1234567890")
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
    when(ppmsClient.providerServicesById("1285621557"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(asList(ProviderServicesResponse.Value.builder().build()))
                .build());
    Practitioner actual = controller.readByIdentifier("identifier");

    assertThat(actual)
        .isEqualTo(
            Practitioner.builder()
                .resourceType("Practitioner")
                .id("1285621557")
                .identifier(
                    asList(
                        Practitioner.PractitionerIdentifier.builder()
                            .system("Npi")
                            .value("1285621557")
                            .build()))
                .active(true)
                .name(
                    Practitioner.PractitionerHumanName.builder()
                        .family("Klingerman")
                        .given(asList("Michael"))
                        .build())
                .telecom(
                    asList(
                        ContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.phone)
                            .value("1234567890")
                            .build()))
                .gender(Practitioner.Gender.male)
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
    when(ppmsClient.providerServicesById("1285621557"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(asList(ProviderServicesResponse.Value.builder().build()))
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
                            .mobilePhone("1234567890")
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

    Practitioner.Bundle actual = controller.searchByFamilyAndGiven("Klingerman", "Michael", 1, 1);

    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            Practitioner.builder()
                .resourceType("Practitioner")
                .id("1285621557")
                .identifier(
                    asList(
                        Practitioner.PractitionerIdentifier.builder()
                            .system("Npi")
                            .value("1285621557")
                            .build()))
                .active(true)
                .name(
                    Practitioner.PractitionerHumanName.builder()
                        .family("Klingerman")
                        .given(asList("Michael"))
                        .build())
                .telecom(
                    asList(
                        ContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.phone)
                            .value("1234567890")
                            .build()))
                .gender(Practitioner.Gender.male)
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
    when(ppmsClient.providerServicesById("1285621557"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .careSitePhoneNumber("1234567890")
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

    Practitioner.Bundle actual = controller.searchByIdentifier("identifier", 1, 1);

    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            Practitioner.builder()
                .resourceType("Practitioner")
                .id("1285621557")
                .identifier(
                    asList(
                        Practitioner.PractitionerIdentifier.builder()
                            .system("Npi")
                            .value("1285621557")
                            .build()))
                .active(true)
                .name(
                    Practitioner.PractitionerHumanName.builder()
                        .family("Klingerman")
                        .given(asList("Michael"))
                        .build())
                .telecom(
                    asList(
                        ContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.phone)
                            .value("1234567890")
                            .build()))
                .gender(Practitioner.Gender.male)
                .build());
  }

  @Test
  public void searchByName() {
    when(ppmsClient.providersForName("Klingerman, Michael"))
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
    when(ppmsClient.providerServicesById("1285621557"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .careSitePhoneNumber("1234567890")
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

    Practitioner.Bundle actual = controller.searchByName("Klingerman, Michael", 1, 1);

    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            Practitioner.builder()
                .resourceType("Practitioner")
                .id("1285621557")
                .identifier(
                    asList(
                        Practitioner.PractitionerIdentifier.builder()
                            .system("Npi")
                            .value("1285621557")
                            .build()))
                .active(true)
                .name(
                    Practitioner.PractitionerHumanName.builder()
                        .family("Klingerman")
                        .given(asList("Michael"))
                        .build())
                .telecom(
                    asList(
                        ContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.phone)
                            .value("1234567890")
                            .build()))
                .gender(Practitioner.Gender.male)
                .build());
  }

  @Test
  public void validateAcceptsValidBundle() {
    assertThat(
            controller.validate(
                Practitioner.Bundle.builder()
                    .resourceType("Bundle")
                    .type(AbstractBundle.BundleType.searchset)
                    .total(1)
                    .link(
                        asList(
                            BundleLink.builder()
                                .relation(BundleLink.LinkRelation.first)
                                .url("//Practitioner?name=Klingerman, Michael&page=1&_count=1")
                                .build(),
                            BundleLink.builder()
                                .relation(BundleLink.LinkRelation.self)
                                .url("//Practitioner?name=Klingerman, Michael&page=1&_count=1")
                                .build(),
                            BundleLink.builder()
                                .relation(BundleLink.LinkRelation.last)
                                .url("//Practitioner?name=Klingerman, Michael&page=1&_count=1")
                                .build()))
                    .entry(
                        asList(
                            Practitioner.Entry.builder()
                                .fullUrl("//Practitioner/1285621557")
                                .resource(
                                    Practitioner.builder()
                                        .resourceType("Practitioner")
                                        .id("1285621557")
                                        .identifier(
                                            asList(
                                                Practitioner.PractitionerIdentifier.builder()
                                                    .system("Npi")
                                                    .value("1285621557")
                                                    .build()))
                                        .active(true)
                                        .name(
                                            Practitioner.PractitionerHumanName.builder()
                                                .family("Klingerman")
                                                .given(asList("Michael"))
                                                .build())
                                        .telecom(
                                            asList(
                                                ContactPoint.builder()
                                                    .system(ContactPoint.ContactPointSystem.email)
                                                    .value("dustin.lehman@email.com")
                                                    .build()))
                                        .gender(Practitioner.Gender.male)
                                        .build())
                                .search(
                                    AbstractEntry.Search.builder()
                                        .mode(AbstractEntry.SearchMode.match)
                                        .build())
                                .build()))
                    .build()))
        .isEqualTo(Validator.ok());
  }

  @Test(expected = ConstraintViolationException.class)
  public void validateThrowsExceptionForInvalidBundle() {
    controller.validate(Practitioner.Bundle.builder().build());
  }
}
