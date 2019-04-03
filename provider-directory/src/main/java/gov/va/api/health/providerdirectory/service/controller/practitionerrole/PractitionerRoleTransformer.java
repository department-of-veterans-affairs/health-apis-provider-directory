package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import org.springframework.stereotype.Service;

import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;

@Service
public class PractitionerRoleTransformer implements PractitionerRoleController.Transformer {
  @Override
  public PractitionerRole apply(PpmsPractitionerRole ppms) {
    // active			Providers.ProviderStatusReason
    // period			Providers(Identifier)/ProviderLicenses.ExpirationDate
    // telecom					Providers(Identifier)/ProviderContacts
    // these are on practitioner as well

    // location			Providers(Identifier)?$expand=CareSites

    // required:
    // practitioner		Reference to Practitioner
    // organization		Reference to Organization
    // code				Providers(Identifier)?$expand=ProviderSpecialties.SpecialtyCode
    // specialty		Providers(Identifier)?$expand=ProviderSpecialties

    return PractitionerRole.builder()
        .resourceType("PractitionerRole")
        .id(null)
        .meta(null)
        .implicitRules(null)
        .language(null)
        .text(null)
        .contained(null)
        .modifierExtension(null)
        .extension(null)
        .identifier(null)
        .active(null)
        .period(null)
        .practitioner(null)
        .organization(null)
        .code(null)
        .specialty(null)
        .location(null)
        // .healthcareService(null)
        .telecom(null)
        // .availableTime(null)
        // .notAvailable(null)
        // .availabilityExceptions(null)
        // .endpoint(null)
        .build();
  }
}
