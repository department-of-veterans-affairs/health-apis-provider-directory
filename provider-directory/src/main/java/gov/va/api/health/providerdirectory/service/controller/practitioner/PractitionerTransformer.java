package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;
import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.controller.EnumSearcher;
import gov.va.api.health.stu3.api.datatypes.Address;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.resources.Practitioner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PractitionerTransformer implements PractitionerController.Transformer {

  Boolean active(String active) {
    return equalsIgnoreCase(active, "active");
  }

  Address address(ProviderResponse.Value value) {
    return convert(
        value,
        ppms ->
            Address.builder()
                .city(ppms.addressCity())
                .country(ppms.addressCountry())
                .state(ppms.addressStateProvince())
                .build());
  }

  List<Address> addresses(ProviderResponse.Value value) {
    if (value == null
        || allBlank(value.addressCity(), value.addressCountry(), value.addressStateProvince())) {
      return null;
    }
    return singletonList(
        Address.builder()
            .city(trimToNull(value.addressCity()))
            .country(trimToNull(value.addressCountry()))
            .state(trimToNull(value.addressStateProvince()))
            .build());
  }

  @Override
  public Practitioner apply(PractitionerWrapper ppmsData) {
    ProviderResponse.Value providerResponse = ppmsData.providerResponse().value().get(0);
    ProviderContactsResponse.Value providerContacts =
        ppmsData.providerContactsResponse().value().isEmpty()
            ? null
            : ppmsData.providerContactsResponse().value().get(0);
    ProviderServicesResponse.Value providerServices =
        ppmsData.providerServicesResponse().value().isEmpty()
            ? null
            : ppmsData.providerServicesResponse().value().get(0);

    return Practitioner.builder()
        .resourceType("Practitioner")
        .active(active(providerResponse.providerStatusReason()))
        .id(providerResponse.providerIdentifier().toString())
        .identifier(Arrays.asList(identifier(providerResponse)))
        .name(name(providerResponse.name()))
        .gender(gender(providerResponse.providerGender()))
        .address(addresses(providerResponse))
        .birthDate(providerContacts == null ? null : providerContacts.birthday())
        .telecom(checkForTelecom(providerServices, providerContacts, providerResponse))
        .build();
  }

  private List<ContactPoint> checkForTelecom(
      ProviderServicesResponse.Value providerServices,
      ProviderContactsResponse.Value providerContacts,
      ProviderResponse.Value providerResponse) {
    if (providerServicesTelecoms(providerServices) != null) {
      return providerServicesTelecoms(providerServices);
    } else if (providerTelecoms(providerResponse) != null) {
      return providerTelecoms(providerResponse);
    } else {
      return providerContactsTelecom(providerContacts);
    }
  }

  Practitioner.Gender gender(String gender) {
    if (isBlank(gender)) {
      return Practitioner.Gender.unknown;
    }
    if (equalsIgnoreCase(gender, "female")) {
      return Practitioner.Gender.female;
    }
    if (equalsIgnoreCase(gender, "male")) {
      return Practitioner.Gender.male;
    }
    return Practitioner.Gender.unknown;
  }

  Practitioner.PractitionerIdentifier identifier(ProviderResponse.Value value) {
    return convert(
        value,
        ppms ->
            Practitioner.PractitionerIdentifier.builder()
                .system(ppms.providerIdentifierType())
                .value(ppms.providerIdentifier().toString())
                .build());
  }

  Practitioner.PractitionerHumanName name(String name) {
    if (name == null) {
      return null;
    }
    List<String> splitNames = new ArrayList<>();
    for (String s : name.split(",")) {
      splitNames.add(s.trim());
    }
    return convert(
        splitNames,
        ppms ->
            Practitioner.PractitionerHumanName.builder()
                .family(ppms.get(0))
                .given(ppms.subList(1, ppms.size()))
                .build());
  }

  List<ContactPoint> providerContactsTelecom(ProviderContactsResponse.Value source) {
    if (source == null || allBlank(source.mobilePhone())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.mobilePhone() != null) {
      telecoms.add(telecom("phone", source.mobilePhone()));
    }
    return telecoms.isEmpty() ? null : telecoms;
  }

  List<ContactPoint> providerServicesTelecoms(ProviderServicesResponse.Value source) {
    if (source == null || allBlank(source.careSitePhoneNumber())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.careSitePhoneNumber() != null) {
      telecoms.add(telecom("phone", source.careSitePhoneNumber()));
    }
    return telecoms.isEmpty() ? null : telecoms;
  }

  List<ContactPoint> providerTelecoms(ProviderResponse.Value source) {
    if (source == null || allBlank(source.mainPhone())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.mainPhone() != null) {
      telecoms.add(telecom("phone", source.mainPhone()));
    }
    return telecoms.isEmpty() ? null : telecoms;
  }

  ContactPoint telecom(String system, String value) {
    return ContactPoint.builder()
        .system(EnumSearcher.of(ContactPoint.ContactPointSystem.class).find(system))
        .value(value)
        .build();
  }
}
