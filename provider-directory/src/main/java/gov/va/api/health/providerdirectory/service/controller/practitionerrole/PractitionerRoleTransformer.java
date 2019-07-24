package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
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
  public PractitionerRole apply(PractitionerRoleWrapper ppms) {
    // TODO organization reference is required
    // location could be populated by caresites
    ProviderResponse.Value provider = ppms.providerResponse().value().get(0);

    return PractitionerRole.builder()
        .resourceType("PractitionerRole")
        .active(StringUtils.equalsIgnoreCase(provider.providerStatusReason(), "active"))
        .practitioner(practitionerReference(provider))
        .code(
            CodeableConcept.builder()
                .coding(codeCodings(ppms.providerSpecialtiesResponse()))
                .build())
        .id(provider.providerIdentifier().toString())
        .telecom(
            ppms.providerContactsResponse().value().isEmpty()
                ? telecoms(null)
                : ppms.providerContactsResponse()
                    .value()
                    .stream()
                    .flatMap(v -> telecoms(v).stream())
                    .collect(Collectors.toList()))
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

  PractitionerContactPoint telecom(String system, String value) {
    if (value.isEmpty()) {
      return null;
    }
    return PractitionerContactPoint.builder()
        .system(EnumSearcher.of(ContactPoint.ContactPointSystem.class).find(system))
        .value(value)
        .build();
  }

  List<PractitionerContactPoint> telecoms(ProviderContactsResponse.Value source) {
    if (source == null
        || allBlank(source.mobilePhone(), source.businessPhone(), source.email(), source.fax())) {
      return null;
    }
    List<PractitionerContactPoint> telecoms = new ArrayList<>();

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
      telecoms.add(telecom("businessPhone", source.businessPhone()));
    }
    return telecoms;
  }
}
