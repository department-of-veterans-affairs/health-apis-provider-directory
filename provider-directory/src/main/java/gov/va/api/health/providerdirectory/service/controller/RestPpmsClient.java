package gov.va.api.health.providerdirectory.service.controller;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** REST implementation of PPMS client. */
@Component
public class RestPpmsClient implements PpmsClient {
  private final RestTemplate restTemplate;

  private final String baseUrl;

  public RestPpmsClient(
      @Value("${ppms.url}") String baseUrl, @Autowired RestTemplate restTemplate) {
    this.baseUrl = baseUrl;
    this.restTemplate = restTemplate;
  }

  private HttpEntity<Void> requestEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    return new HttpEntity<>(headers);
  }

  //  @Override
  //  public <T> T search(Query<T> query) {
  //    try {
  //      ResponseEntity<T> entity =
  //          restTemplate.exchange(
  //              urlOf(query),
  //              HttpMethod.GET,
  //              requestEntity(),
  //              ParameterizedTypeReference.forType(query.type()));
  //      return entity.getBody();
  //    } catch (HttpClientErrorException.NotFound e) {
  //      throw new NotFound(query);
  //    } catch (HttpClientErrorException.BadRequest e) {
  //      throw new BadRequest(query);
  //    } catch (HttpStatusCodeException e) {
  //      throw new SearchFailed(query);
  //    }
  //  }

  //  private String urlOf(Query<?> query) {
  //    return baseUrl + "/api/v1/resources" + query.toQueryString();
  //  }
}
