package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.AddressResponse;

public interface VlerClient {
  AddressResponse endpointByAddress(String searchFunction);
}
