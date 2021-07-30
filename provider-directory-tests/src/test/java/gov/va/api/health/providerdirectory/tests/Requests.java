package gov.va.api.health.providerdirectory.tests;

import static gov.va.api.health.providerdirectory.tests.SystemDefinitions.systemDefinition;
import static gov.va.api.health.sentinel.ExpectedResponse.logAllWithTruncatedBody;

import gov.va.api.health.sentinel.ExpectedResponse;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.specification.RequestSpecification;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
final class Requests {
  public static ExpectedResponse doGet(
      String acceptHeader, String request, Integer expectedStatus) {
    SystemDefinitions.Service svc = systemDefinition().internal();
    RequestSpecification spec =
        RestAssured.given().baseUri(svc.url()).port(svc.port()).relaxedHTTPSValidation();
    log.info(
        "Expect {} with accept header ({}) is status code ({})",
        svc.urlWithApiPath() + request,
        acceptHeader,
        expectedStatus);
    if (acceptHeader != null) {
      spec = spec.accept(acceptHeader);
    }
    ExpectedResponse response =
        ExpectedResponse.of(spec.request(Method.GET, svc.urlWithApiPath() + request))
            .logAction(logAllWithTruncatedBody(2000));
    if (expectedStatus != null) {
      response.expect(expectedStatus);
    }
    return response;
  }
}
