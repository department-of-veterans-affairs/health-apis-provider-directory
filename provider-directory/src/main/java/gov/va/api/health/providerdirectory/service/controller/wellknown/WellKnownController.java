package gov.va.api.health.providerdirectory.service.controller.wellknown;

import gov.va.api.health.providerdirectory.api.information.WellKnown;
import gov.va.api.health.providerdirectory.service.controller.capabilitystatement.CapabilityStatementProperties;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
  value = {".well-known/smart-configuration"},
  produces = {"application/json", "application/fhir+json", "application/json+fhir"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
class WellKnownController {

  private final WellKnownProperties wellKnownProperties;
  private final CapabilityStatementProperties capabilityStatementProperties;

  @GetMapping
  WellKnown read() {
    return WellKnown.builder()
        .authorizationEndpoint(capabilityStatementProperties.getSecurity().getAuthorizeEndpoint())
        .tokenEndpoint(capabilityStatementProperties.getSecurity().getTokenEndpoint())
        .capabilities(wellKnownProperties.getCapabilities())
        .responseTypeSupported(wellKnownProperties.getResponseTypeSupported())
        .scopesSupported(wellKnownProperties.getScopesSupported())
        .build();
  }
}
