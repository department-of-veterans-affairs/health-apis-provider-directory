package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;
import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.trimToNull;

import gov.va.api.health.providerdirectory.api.datatypes.Address;
import gov.va.api.health.providerdirectory.api.datatypes.ContactPoint;
import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import gov.va.api.health.providerdirectory.api.resources.Practitioner.Gender;
import gov.va.api.health.providerdirectory.api.resources.Practitioner.PractitionerHumanName;
import gov.va.api.health.providerdirectory.api.resources.Practitioner.PractitionerIdentifier;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.PractitionerWrapper;
import gov.va.api.health.providerdirectory.service.controller.EnumSearcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PractitionerTransformer implements PractitionerController.Transformer {
  Boolean active(String active) {
    if ( active.equalsIgnoreCase("active")) {
      return true;
    }
    return false;
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
    if (value == null || allBlank(value.addressCity(), value.addressCountry(), value.addressStateProvince())) {
        return null;
      }
    return singletonList(
          Address.builder()
                  .city(trimToNull(value.addressCity()))
                  .country(trimToNull(value.addressCountry()))
                  .state(trimToNull(value.addressStateProvince())).build());
  }

  @Override
  public Practitioner apply(PractitionerWrapper ppmsData) {
    return practitioner(ppmsData);
  }

  Practitioner.Gender gender(String gender) {
      return convert(gender, ppms -> EnumSearcher.of(Gender.class).find(ppms.trim().toLowerCase()));
  }

  PractitionerIdentifier identifier(ProviderResponse.Value value) {
    return convert(
        value,
        ppms ->
            PractitionerIdentifier.builder()
                .system(ppms.providerIdentifierType())
                .value(ppms.providerIdentifier().toString())
                .build());
  }

  PractitionerHumanName name(String name) {
    List<String> splitName = Arrays.asList(name.trim().split(","));
    return convert(
        splitName,
        ppms ->
            PractitionerHumanName.builder()
                .family(ppms.get(0))
                .given(ppms.subList(1, ppms.size()))
                .build());
  }

  private Practitioner practitioner(PractitionerWrapper ppmsData) {
    ProviderResponse.Value response = ppmsData.providerResponse().value().get(0);
    ProviderContactsResponse.Value contacts;
    if (ppmsData.providerContactsResponse().value().size() > 0) {
      contacts = ppmsData.providerContactsResponse().value().get(0);
    } else { contacts = null; }
    List<PractitionerIdentifier> identifiers = new ArrayList<>();
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

  ContactPoint telecom(String system, String value) {
    if (value == null) return null;
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
    if (telecoms == null) return null;
    else { return telecoms; }
  }
}
