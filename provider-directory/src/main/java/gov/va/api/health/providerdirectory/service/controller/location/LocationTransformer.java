package gov.va.api.health.providerdirectory.service.controller.location;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;
import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;

import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.controller.EnumSearcher;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.resources.Location;
import gov.va.api.health.stu3.api.resources.Location.LocationAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LocationTransformer implements LocationController.Transformer {

  LocationAddress address(ProviderServicesResponse.Value value) {
    return convert(
        value,
        ppms ->
            LocationAddress.builder()
                .city(ppms.careSiteAddressCity())
                .line(Collections.singletonList(ppms.careSiteAddressStreet()))
                .text(ppms.careSiteLocationAddress())
                .state(ppms.careSiteAddressState())
                .postalCode(ppms.careSiteAddressZipCode())
                .build());
  }

  List<LocationAddress> addresses(ProviderServicesResponse.Value value) {
    if (value.careSiteAddressCity() == null) {
      return null;
    }
    List<LocationAddress> addressList = new ArrayList<>();
    addressList.add(address(value));
    return addressList;
  }

  @Override
  public Location apply(ProviderServicesResponse ppmsData) {
    return location(ppmsData);
  }

  private Location location(ProviderServicesResponse ppmsData) {
    ProviderServicesResponse.Value providerServices = ppmsData.value().get(0);
    return Location.builder()
        .resourceType("Location")
        .name(providerServices.careSiteName())
        .status(Location.Status.active)
        .address(address(providerServices))
        .telecom(telecoms(providerServices))
        .build();
  }

  ContactPoint telecom(String system, String value) {
    return ContactPoint.builder()
        .system(EnumSearcher.of(ContactPoint.ContactPointSystem.class).find(system))
        .value(value)
        .build();
  }

  List<ContactPoint> telecoms(ProviderServicesResponse.Value source) {
    if (source == null || allBlank(source.careSitePhoneNumber())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.careSitePhoneNumber() != null) {
      telecoms.add(telecom("phone", source.careSitePhoneNumber()));
    }
    return telecoms;
  }
}
