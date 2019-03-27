package gov.va.api.health.providerdirectory.service.controller.practitioner;

import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PractitionerTransformer implements PractitionerController.Transformer {
  private final RestTemplate restTemplate = null;

  @Override
  public Practitioner apply(Practitioner practitioner) {

    return null;
  }

  private Practitioner practitioner(String source) {
    return Practitioner.builder().id(source).resourceType("Practitioner").build();
  }
}
