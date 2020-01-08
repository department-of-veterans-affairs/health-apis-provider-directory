package gov.va.api.health.providerdirectory.service.controller.endpoint;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.stu3.api.datatypes.CodeableConcept;
import gov.va.api.health.stu3.api.datatypes.Coding;
import gov.va.api.health.stu3.api.resources.Endpoint;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EndpointTransformer implements EndpointController.Transformer {
  @Override
  public Endpoint apply(EndpointWrapper vlerData) {
    // managingOrganization will map to 'facility' field from VLER
    // connectionType and payloadType are defaults for VLER
    AddressResponse.Contacts response = vlerData.addressResponse();
    return Endpoint.builder()
        .resourceType("Endpoint")
        .id(response.uid())
        .status(Endpoint.Status.active)
        .connectionType(Coding.builder().code("direct-project").build())
        .name(response.displayName())
        .payloadType(payloadCodeCodings("VLER Direct"))
        .address(response.emailAddress())
        .build();
  }

  List<CodeableConcept> payloadCodeCodings(String payloadTypeResponse) {
    if (payloadTypeResponse == null) {
      return null;
    }
    return Arrays.asList(
        CodeableConcept.builder()
            .coding(
                Arrays.asList(
                    Coding.builder()
                        .display(payloadTypeResponse)
                        .code(payloadTypeResponse)
                        .build()))
            .build());
  }
}
