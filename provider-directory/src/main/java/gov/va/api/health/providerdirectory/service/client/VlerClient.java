package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.AddressResponse;

/** This is the abstraction for communicating with PPMS through their Restful API. */
public interface VlerClient extends ExceptionClient {
  AddressResponse endpointByAddress(String searchFunction);
}
