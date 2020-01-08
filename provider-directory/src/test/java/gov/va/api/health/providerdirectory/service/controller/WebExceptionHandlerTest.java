package gov.va.api.health.providerdirectory.service.controller;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.client.Exceptions;
import gov.va.api.health.providerdirectory.service.client.VlerClient;
import gov.va.api.health.providerdirectory.service.controller.endpoint.EndpointController;
import gov.va.api.health.providerdirectory.service.controller.endpoint.EndpointTransformer;
import java.lang.reflect.Method;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

public class WebExceptionHandlerTest {
  @Test
  @SneakyThrows
  public void badRequest() {
    VlerClient vlerClient = mock(VlerClient.class);
    when(vlerClient.endpointByAddress("123"))
        .thenThrow(mock(HttpClientErrorException.BadRequest.class));
    EndpointController controller =
        new EndpointController(new EndpointTransformer(), null, vlerClient);
    MockMvcBuilders.standaloneSetup(controller)
        .setHandlerExceptionResolvers(exceptionResolver())
        .setMessageConverters()
        .build()
        .perform(get("/stu3/Endpoint/123"))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("text.div", containsString("/Endpoint/123")))
        .andExpect(jsonPath("issue[0].diagnostics", containsString("BadRequest")));
  }

  @Test
  @SneakyThrows
  public void constraintViolation() {
    VlerClient vlerClient = mock(VlerClient.class);
    when(vlerClient.endpointByAddress("123"))
        .thenThrow(new ConstraintViolationException(emptySet()));
    EndpointController controller =
        new EndpointController(new EndpointTransformer(), null, vlerClient);
    MockMvcBuilders.standaloneSetup(controller)
        .setHandlerExceptionResolvers(exceptionResolver())
        .setMessageConverters()
        .build()
        .perform(get("/stu3/Endpoint/123"))
        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
        .andExpect(jsonPath("text.div", containsString("/Endpoint/123")))
        .andExpect(
            jsonPath("issue[0].diagnostics", containsString("ConstraintViolationException")));
  }

  private ExceptionHandlerExceptionResolver exceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver =
        new ExceptionHandlerExceptionResolver() {
          @Override
          protected ServletInvocableHandlerMethod getExceptionHandlerMethod(
              HandlerMethod handlerMethod, Exception ex) {
            Method method =
                new ExceptionHandlerMethodResolver(WebExceptionHandler.class).resolveMethod(ex);
            assertThat(method).isNotNull();
            return new ServletInvocableHandlerMethod(new WebExceptionHandler(), method);
          }
        };
    exceptionResolver
        .getMessageConverters()
        .add(new MappingJackson2HttpMessageConverter(JacksonConfig.createMapper()));
    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }

  @Test
  @SneakyThrows
  public void internalServerError() {
    VlerClient vlerClient = mock(VlerClient.class);
    when(vlerClient.endpointByAddress("123")).thenThrow(new RuntimeException());
    EndpointController controller =
        new EndpointController(new EndpointTransformer(), null, vlerClient);
    MockMvcBuilders.standaloneSetup(controller)
        .setHandlerExceptionResolvers(exceptionResolver())
        .setMessageConverters()
        .build()
        .perform(get("/stu3/Endpoint/123"))
        .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        .andExpect(jsonPath("text.div", containsString("/Endpoint/123")))
        .andExpect(jsonPath("issue[0].diagnostics", containsString("Exception")));
  }

  @Test
  @SneakyThrows
  public void notFound() {
    VlerClient vlerClient = mock(VlerClient.class);
    when(vlerClient.endpointByAddress("123"))
        .thenThrow(mock(HttpClientErrorException.NotFound.class));
    EndpointController controller =
        new EndpointController(new EndpointTransformer(), null, vlerClient);
    MockMvcBuilders.standaloneSetup(controller)
        .setHandlerExceptionResolvers(exceptionResolver())
        .setMessageConverters()
        .build()
        .perform(get("/stu3/Endpoint/123"))
        .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
        .andExpect(jsonPath("text.div", containsString("/Endpoint/123")))
        .andExpect(jsonPath("issue[0].diagnostics", containsString("NotFound")));
  }

  @Test
  @SneakyThrows
  public void searchFailed() {
    VlerClient vlerClient = mock(VlerClient.class);
    when(vlerClient.endpointByAddress("123")).thenThrow(new Exceptions.VlerException(null));
    EndpointController controller =
        new EndpointController(new EndpointTransformer(), null, vlerClient);
    MockMvcBuilders.standaloneSetup(controller)
        .setHandlerExceptionResolvers(exceptionResolver())
        .setMessageConverters()
        .build()
        .perform(get("/stu3/Endpoint/123"))
        .andExpect(status().is(HttpStatus.SERVICE_UNAVAILABLE.value()))
        .andExpect(jsonPath("text.div", containsString("/Endpoint/123")))
        .andExpect(jsonPath("issue[0].diagnostics", containsString("VlerException")));
  }
}
