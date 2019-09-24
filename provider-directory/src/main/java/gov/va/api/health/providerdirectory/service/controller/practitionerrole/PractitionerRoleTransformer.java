package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.controller.EnumSearcher;
import gov.va.api.health.stu3.api.datatypes.CodeableConcept;
import gov.va.api.health.stu3.api.datatypes.Coding;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.elements.Reference;
import gov.va.api.health.stu3.api.resources.PractitionerRole;
import gov.va.api.health.stu3.api.resources.PractitionerRole.PractitionerContactPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PractitionerRoleTransformer implements PractitionerRoleController.Transformer {

  @Value("${provider-directory.url}")
  String baseUrl;

  @Override
  public PractitionerRole apply(PractitionerRoleWrapper ppmsData) {
    // TODO organization reference is required
    // location could be populated by caresites
    ProviderResponse.Value providerResponse = ppmsData.providerResponse().value().get(0);
    ProviderContactsResponse.Value providerContacts =
        ppmsData.providerContactsResponse().value().isEmpty()
            ? null
            : ppmsData.providerContactsResponse().value().get(0);
    ProviderServicesResponse.Value providerServices =
        ppmsData.providerServicesResponse().value().isEmpty()
            ? null
            : ppmsData.providerServicesResponse().value().get(0);
    return PractitionerRole.builder()
        .resourceType("PractitionerRole")
        .active(StringUtils.equalsIgnoreCase(providerResponse.providerStatusReason(), "active"))
        .practitioner(practitionerReference(providerResponse))
        .code(
            CodeableConcept.builder()
                .coding(codeCodings(ppmsData.providerSpecialtiesResponse()))
                .build())
        .id(providerResponse.providerIdentifier().toString())
        .telecom(
            providerServicesTelecoms(providerServices) != null
                ? providerServicesTelecoms(providerServices)
                : providerTelecoms(providerResponse) != null
                    ? providerTelecoms(providerResponse)
                    : providerContactsTelecom(providerContacts))
        .build();
  }

  private List<Coding> codeCodings(ProviderSpecialtiesResponse specialtiesResponse) {
    if (specialtiesResponse == null) {
      return null;
    }
    return specialtiesResponse
        .value()
        .stream()
        .map(v -> Coding.builder().code(v.codedSpecialty()).display(v.name()).build())
        .collect(Collectors.toList());
  }

  private Reference practitionerReference(ProviderResponse.Value provider) {
    if (provider == null || provider.providerIdentifier() == null) {
      return null;
    }
    return Reference.builder()
        .reference(baseUrl + "/Practitioner/" + provider.providerIdentifier())
        .build();
  }

  List<PractitionerContactPoint> providerContactsTelecom(ProviderContactsResponse.Value source) {
    if (source == null || allBlank(source.mobilePhone())) {
      return null;
    }
    List<PractitionerContactPoint> telecoms = new ArrayList<>();
    if (source.mobilePhone() != null) {
      telecoms.add(telecom("phone", source.mobilePhone()));
    }
    return telecoms.isEmpty() ? null : telecoms;
  }

  List<PractitionerContactPoint> providerServicesTelecoms(ProviderServicesResponse.Value source) {
    if (source == null || allBlank(source.careSitePhoneNumber())) {
      return null;
    }
    List<PractitionerContactPoint> telecoms = new ArrayList<>();
    if (source.careSitePhoneNumber() != null) {
      telecoms.add(telecom("phone", source.careSitePhoneNumber()));
    }
    return telecoms.isEmpty() ? null : telecoms;
  }

  List<PractitionerContactPoint> providerTelecoms(ProviderResponse.Value source) {
    if (source == null || allBlank(source.mainPhone())) {
      return null;
    }
    List<PractitionerContactPoint> telecoms = new ArrayList<>();
    if (source.mainPhone() != null) {
      telecoms.add(telecom("phone", source.mainPhone()));
    }
    return telecoms.isEmpty() ? null : telecoms;
  }

  PractitionerContactPoint telecom(String system, String value) {
    return PractitionerContactPoint.builder()
        .system(EnumSearcher.of(ContactPoint.ContactPointSystem.class).find(system))
        .value(value)
        .build();
  }
}
