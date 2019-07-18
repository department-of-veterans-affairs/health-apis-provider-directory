package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;

<<<<<<< Updated upstream
import gov.va.api.health.providerdirectory.api.datatypes.CodeableConcept;
import gov.va.api.health.providerdirectory.api.datatypes.Coding;
import gov.va.api.health.providerdirectory.api.datatypes.ContactPoint;
import gov.va.api.health.providerdirectory.api.elements.Reference;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole.PractitionerContactPoint;
import gov.va.api.health.providerdirectory.service.PpmsPractitionerRole;
import gov.va.api.health.providerdirectory.service.PpmsProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
=======
import gov.va.api.health.providerdirectory.service.PractitionerRoleWrapper;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
>>>>>>> Stashed changes
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.controller.EnumSearcher;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
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
    return specialtiesResponse
        .value()
        .stream()
        .map(v -> Coding.builder().code(v.codedSpecialty()).display(v.name()).build())
        .collect(Collectors.toList());
  }

  private Reference practitionerReference(ProviderResponse.Value provider) {
    return Reference.builder()
        .reference(baseUrl + "/Practitioner/" + provider.providerIdentifier())
        .build();
  }

  PractitionerContactPoint telecom(String system, String value) {
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
      telecoms.add(telecom("phone", source.businessPhone()));
    }
    return telecoms;
  }
}
