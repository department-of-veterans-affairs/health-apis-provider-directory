package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;
import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.controller.EnumSearcher;
import gov.va.api.health.stu3.api.datatypes.Address;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.resources.Practitioner;
import java.util.ArrayList;
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
    ProviderResponse.Value response = ppmsData.providerResponse().value().get(0);
    ProviderContactsResponse.Value contacts;
    if (ppmsData.providerContactsResponse().value().size() > 0) {
      contacts = ppmsData.providerContactsResponse().value().get(0);
    } else {
      contacts = null;
    }
    List<Practitioner.PractitionerIdentifier> identifiers = new ArrayList<>();
    identifiers.add(identifier(response));
    return Practitioner.builder()
        .resourceType("Practitioner")
        .active(active(response.providerStatusReason()))
        .id(response.providerIdentifier().toString())
        .identifier(identifiers)
        .name(name(response.name()))
        .gender(gender(response.providerGender()))
        .address(addresses(response))
        .birthDate((contacts == null) ? null : contacts.birthday())
        .telecom(telecoms(contacts))
        .build();
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

  ContactPoint telecom(String system, String value) {
    if (value == null) {
      return null;
    }
    return ContactPoint.builder()
        .system(EnumSearcher.of(ContactPoint.ContactPointSystem.class).find(system))
        .value(value)
        .build();
  }

  List<ContactPoint> telecoms(ProviderContactsResponse.Value source) {
    if (source == null
        || allBlank(source.mobilePhone(), source.businessPhone(), source.email(), source.fax())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.mobilePhone() != null) {
      telecoms.add(telecom("phone", source.mobilePhone()));
    }
    if (source.fax() != null) {
      telecoms.add(telecom("fax", source.fax()));
    }
    if (source.email() != null) {
      telecoms.add(telecom("email", source.email()));
    }
    if (source.businessPhone() != null) {
      telecoms.add(telecom("phone", source.businessPhone()));
    }
    return telecoms;
  }
}
