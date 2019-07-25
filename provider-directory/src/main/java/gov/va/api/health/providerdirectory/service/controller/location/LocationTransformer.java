package gov.va.api.health.providerdirectory.service.controller.location;

import static gov.va.api.health.providerdirectory.service.controller.Transformers.allBlank;
import static gov.va.api.health.providerdirectory.service.controller.Transformers.convert;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
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

  @Override
  public Location apply(LocationWrapper ppmsData) {
    return location(ppmsData);
  }

  LocationAddress careSiteResponseAddress(CareSitesResponse.Value value) {
    return convert(
        value,
        ppms ->
            LocationAddress.builder()
                .city(ppms.city())
                .line(Collections.singletonList(ppms.street()))
                .text(
                    ppms.street()
                        + ", "
                        + ppms.city()
                        + ", "
                        + ppms.state()
                        + ", "
                        + ppms.zipCode())
                .state(ppms.state())
                .postalCode(ppms.zipCode())
                .build());
  }

  List<ContactPoint> careSiteTelecoms(CareSitesResponse.Value source) {
    if (source == null || allBlank(source.mainSitePhone())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.mainSitePhone() != null) {
      telecoms.add(telecom("phone", source.mainSitePhone()));
    }
    return telecoms;
  }

  private Location location(LocationWrapper ppmsData) {
    ProviderServicesResponse.Value providerServices =
        ppmsData.providerServicesResponse().value() == null
                || ppmsData.providerServicesResponse().value().isEmpty()
            ? null
            : ppmsData.providerServicesResponse().value().get(0);
    ProviderResponse.Value providerResponse =
        ppmsData.providerResponse().value() == null || ppmsData.providerResponse().value().isEmpty()
            ? null
            : ppmsData.providerResponse().value().get(0);
    CareSitesResponse.Value careSiteResponse =
        ppmsData.careSitesResponse().value() == null
                || ppmsData.careSitesResponse().value().isEmpty()
            ? null
            : ppmsData.careSitesResponse().value().get(0);
    return Location.builder()
        .resourceType("Location")
        .name(
            providerServices != null
                ? providerServices.careSiteName()
                : careSiteResponse != null ? careSiteResponse.name() : providerResponse.name())
        .status(Location.Status.active)
        .address(
            (providerServices != null && providerServices.careSiteAddressCity() != null)
                ? providerServicesResponseAddress(providerServices)
                : (careSiteResponse != null && careSiteResponse.city() != null)
                    ? careSiteResponseAddress(careSiteResponse)
                    : providerResponseAddress(providerResponse))
        .telecom(
            providerServicesTelecoms(providerServices) != null
                ? providerServicesTelecoms(providerServices)
                : providerTelecoms(providerResponse) != null
                    ? providerTelecoms(providerResponse)
                    : careSiteTelecoms(careSiteResponse))
        .build();
  }

  LocationAddress providerResponseAddress(ProviderResponse.Value value) {
    return convert(
        value,
        ppms ->
            LocationAddress.builder()
                .city(ppms.addressCity())
                .line(Collections.singletonList(ppms.addressStreet()))
                .text(ppms.address())
                .state(ppms.addressStateProvince())
                .postalCode(ppms.addressPostalCode())
                .build());
  }

  LocationAddress providerServicesResponseAddress(ProviderServicesResponse.Value value) {
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

  List<ContactPoint> providerServicesTelecoms(ProviderServicesResponse.Value source) {
    if (source == null || allBlank(source.careSitePhoneNumber())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.careSitePhoneNumber() != null) {
      telecoms.add(telecom("phone", source.careSitePhoneNumber()));
    }
    return telecoms;
  }

  List<ContactPoint> providerTelecoms(ProviderResponse.Value source) {
    if (source == null || allBlank(source.mainPhone())) {
      return null;
    }
    List<ContactPoint> telecoms = new ArrayList<>();
    if (source.mainPhone() != null) {
      telecoms.add(telecom("phone", source.mainPhone()));
    }
    return telecoms;
  }

  ContactPoint telecom(String system, String value) {
    return ContactPoint.builder()
        .system(EnumSearcher.of(ContactPoint.ContactPointSystem.class).find(system))
        .value(value)
        .build();
  }
}
