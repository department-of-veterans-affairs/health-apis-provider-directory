package gov.va.api.health.providerdirectory.service.controller.endpoint;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.stu3.api.datatypes.CodeableConcept;
import gov.va.api.health.stu3.api.datatypes.Coding;
import gov.va.api.health.stu3.api.resources.Endpoint;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EndpointTransformer implements EndpointController.Transformer {

  /* managingOrganization will map to 'facility' field from VLER.
   * connectionType and payloadType are defaults for VLER. */
  @Override
  public Endpoint apply(EndpointWrapper vlerData) {
    AddressResponse.Contacts response = vlerData.addressResponse().contacts().get(0);
    return Endpoint.builder()
        .resourceType("Endpoint")
        .status(status("active"))
        .connectionType(connectionCodeCodings("direct-project"))
        .name(response.displayname())
        .payloadType(payloadCodeCodings("VLER Direct"))
        .address(response.mail())
        .build();
  }

  Coding connectionCodeCodings(String connectionTypeResponse) {
    if (connectionTypeResponse == null) {
      return null;
    }
    Coding vlerDefault = Coding.builder().code(connectionTypeResponse).build();
    return vlerDefault;
  }

  List<CodeableConcept> payloadCodeCodings(String payloadTypeResponse) {
    if (payloadTypeResponse == null) {
      return null;
    }
    List<Coding> codings = new ArrayList<>();
    codings.add(Coding.builder().display(payloadTypeResponse).code(payloadTypeResponse).build());
    List<CodeableConcept> payload = new ArrayList<>();
    payload.add(CodeableConcept.builder().coding(codings).build());
    return payload;
  }

  Endpoint.Status status(String status) {
    return Endpoint.Status.active;
  }
}
