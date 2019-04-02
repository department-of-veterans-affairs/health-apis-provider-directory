package gov.va.api.health.providerdirectory.service.controller.practitioner;

import gov.va.api.health.providerdirectory.api.datatypes.Address;
import gov.va.api.health.providerdirectory.api.datatypes.Identifier;
import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import gov.va.api.health.providerdirectory.api.resources.Practitioner.PractitionerIdentifier;
import gov.va.api.health.providerdirectory.api.resources.Practitioner.PractitionerHumanName;
import gov.va.api.health.providerdirectory.service.ProviderResponse;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;



@Service
public class PractitionerTransformer implements PractitionerController.Transformer {

  PractitionerHumanName name(String name){
    List<String> splitName = Arrays.asList(name.trim().split(","));
    return convert(
       splitName,
       ppms ->
            PractitionerHumanName.builder()
            .family(ppms.get(0))
            .given(ppms.subList(1,ppms.size()))
            .build());
  }


  List<Address> addresses(ProviderResponse.Value value){
    List<String> fullAddress = new ArrayList<>();
    fullAddress.add(value.address() + " " + value.addressStreet());
    return convert(
            value,
            ppms ->
            (List)Address.builder()
            .city(ppms.addressCity())
            .country(ppms.addressCountry())
            .state(ppms.addressStateProvince())
            .line(fullAddress)
            .build());
  }

  PractitionerIdentifier identifier(ProviderResponse.Value value){

    return convert(
            value,
            ppms ->
                    PractitionerIdentifier.builder()
                            .system(ppms.providerIdentifierType())
                            .value(ppms.providerIdentifier().toString())
                            .build());
  }

  private Practitioner practitioner(ProviderResponse ppmsData) {
    ProviderResponse.Value response = ppmsData.value().get(0);
    List<PractitionerIdentifier> identifiers = new ArrayList<>();
    identifiers.add(identifier(response));

    return Practitioner.builder()
        .resourceType("Practitioner")
        .active(false)
        .identifier(identifiers)
        .name(name(response.name()))
       // .address(addresses(response))
        .build();
  }

    @Override
    public Practitioner apply(ProviderResponse ppmsData) {
        return practitioner(ppmsData);
    }

}
