package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import org.springframework.stereotype.Service;

import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;

@Service
public class PractitionerRoleTransformer implements PractitionerRoleController.Transformer {
  @Override
  public PractitionerRole apply(PpmsPractitionerRole ppms) {
    return null;
  }
}
