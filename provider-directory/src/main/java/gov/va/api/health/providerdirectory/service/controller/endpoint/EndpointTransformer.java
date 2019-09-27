package gov.va.api.health.providerdirectory.service.controller.endpoint;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.providerdirectory.service.Application;
import gov.va.api.health.stu3.api.resources.Endpoint;

import gov.va.api.health.stu3.api.datatypes.CodeableConcept;
import gov.va.api.health.stu3.api.datatypes.Coding;

import gov.va.api.health.providerdirectory.service.AddressResponse;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class EndpointTransformer implements EndpointController.Transformer {

    Endpoint.Status status(String status) {
        return Endpoint.Status.active;
    }

    Coding codeCodings(String connectionTypeResponse) {
        if (connectionTypeResponse == null) {
            return null;
        }
        Coding vlerDefault = Coding.builder().code(connectionTypeResponse).build();
        return vlerDefault;
    }

//    Practitioner.PractitionerHumanName name(String name) {
//        if (name == null) {
//            return null;
//        }
//        List<String> splitNames = new ArrayList<>();
//        for (String s : name.split(",")) {
//            splitNames.add(s.trim());
//        }
//        return convert(
//                splitNames,
//                ppms ->
//                        Practitioner.PractitionerHumanName.builder()
//                                .family(ppms.get(0))
//                                .given(ppms.subList(1, ppms.size()))
//                                .build());
//    }

  @Override
  public Endpoint apply(EndpointWrapper vlerData) {
    AddressResponse.Contacts response = vlerData.addressResponse().contacts().get(0);
    return Endpoint.builder()
        .resourceType("Endpoint")
            .status(status("active"))  // active
            .connectionType(codeCodings("direct-project"))  // direct-project
            .name(response.displayname())  // 'display-name'
            //.payloadType()  // ? REQUIRED ?
            .address(response.mail())  // 'mail'
        .build();
  }

    /** Will map to Organization once that resource is completed */
    Reference managingOrganization(String value) {
      return null;
      // Map to 'facility' field from VLER
    }

    List<CodeableConcept> payloadType(AddressResponse payload) {
      return null;
    }

//    List<Address> addresses(ProviderResponse.Value value) {
//        if (value == null
//                || allBlank(value.addressCity(), value.addressCountry(), value.addressStateProvince())) {
//            return null;
//        }
//        return singletonList(
//                Address.builder()
//                        .city(trimToNull(value.addressCity()))
//                        .country(trimToNull(value.addressCountry()))
//                        .state(trimToNull(value.addressStateProvince()))
//                        .build());
//    }

    String address(AddressResponse.Contacts contacts) {
      return null;
      // Map to 'mail'
    }
}