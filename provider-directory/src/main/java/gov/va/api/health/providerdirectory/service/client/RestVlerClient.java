package gov.va.api.health.providerdirectory.service.client;

import static org.apache.commons.lang3.StringUtils.isNotBlank;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.*;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.Callable;

import gov.va.api.health.providerdirectory.service.VlerResponse;
import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.stu3.api.resources.CapabilityStatement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/** REST implementation of PPMS client. */
@Slf4j
@Component
public class RestVlerClient implements VlerClient {

    private final String baseUrl;

    private final RestTemplate restTemplate;

    @Value("${vler.pubkey}") String pubKey;
    @Value("${vler.prvkey}") String prvKey;
    @Value("${vler.truststore.password}") String truststorePassword;
    @Value("${vler.truststore.location}") String truststoreLocation;
    @Value("${vler.url}") String vlerUrl;

    public RestVlerClient(
            @Value("${vler.url}") String baseUrl, @Autowired RestTemplate restTemplate) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.restTemplate = restTemplate;
    }


    @SneakyThrows
    private String authHeader(String baseUrl, String url, String dateString) {
        String endpoint = "/" + url.substring(url.indexOf(baseUrl) + baseUrl.length());
        String reqStr = "GET\n" + dateString + "\napplication/json\n" + endpoint;
        System.out.println("\n********\nHere ye be the reqStr:\n----------------------\n" + reqStr + "\n********\n");
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        sha256_HMAC.init(new SecretKeySpec(prvKey.getBytes(), "HmacSHA256"));
        byte[] sha = sha256_HMAC.doFinal(reqStr.getBytes());
        String encSha = Base64.getEncoder().encodeToString(sha);
        return "DAAS " + pubKey + ":" + encSha;
    }

    @SneakyThrows
    public RestTemplate restTemplate(){
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }


/*  Implement this if the above isnt secure enough.
    @SneakyThrows
    private RestTemplate secureRestTemplate() {
        try (InputStream truststoreInputStream = new FileInputStream(new File(vlerTruststore))) {
            KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(truststoreInputStream, truststorePassword.toCharArray());
            SSLContext sslContext = null;
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(ts);
            sslContext = SSLContext.getInstance("TLSv1.1");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            SSLConnectionSocketFactory socketFactory =
                    new SSLConnectionSocketFactory(
                            new SSLContextBuilder()
                                    .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                    .loadKeyMaterial(ts, truststorePassword.toCharArray())
                                    .build(),
                            new String[]{"TLSv1.1"}, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            HttpClient httpsClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpsClient));
        }
    }*/


    @SneakyThrows
    private static <T extends VlerResponse> T handleVlerExceptions(
            String message, Callable<T> callable) {
        T response;
        try {
            response = callable.call();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFound(message, e);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new BadRequest(message, e);
        } catch (HttpStatusCodeException e) {
            throw new SearchFailed(message, e);
        } catch (Exception e) {
            throw new VlerException(message, e);
        }
        if (response == null) {
            throw new VlerException(message + ", no VLER response");
        }
        if (response.error() != null && isNotBlank(response.error().message())) {
            throw new VlerException(message + ", " + response.error().message());
        }
        return response;
    }

    @Override
    public AddressResponse endpointByAddress(String address) {
        return handleVlerExceptions(
                address,
                () -> {
                    String url =
                            UriComponentsBuilder.fromHttpUrl(baseUrl + "/direct/addresses")
                                    .queryParam("cn", address)
                                    .build()
                                    .toUriString();
                    System.out.println("URL: >>> " + url);
                    String dateString =
                            DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss z").format(ZonedDateTime.now());
                    HttpHeaders headers = new HttpHeaders();
                        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", authHeader(baseUrl, url, dateString));
                        headers.add("Date", dateString);

                    HttpEntity<?> requestEntity = new HttpEntity<>(headers);
                    System.out.println("RequestEntity: " + requestEntity);

                    ResponseEntity<AddressResponse> entity = restTemplate().exchange(url, HttpMethod.GET, requestEntity, AddressResponse.class);
                    System.out.println(">>> Do I like you or not? " + entity);
                    return entity.getBody();
                });
    }
}
