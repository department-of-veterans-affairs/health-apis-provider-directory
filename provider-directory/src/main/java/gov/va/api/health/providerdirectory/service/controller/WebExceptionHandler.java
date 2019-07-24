package gov.va.api.health.providerdirectory.service.controller;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.stu3.api.elements.Narrative;
import gov.va.api.health.stu3.api.resources.OperationOutcome;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Exceptions that escape the rest controllers will be processed by this handler. It will convert
 * exception into different HTTP status codes and produce an error response payload.
 */
@Slf4j
@RestControllerAdvice
@RequestMapping(produces = {"application/json"})
public class WebExceptionHandler {
  @ExceptionHandler({
    BindException.class,
    HttpClientErrorException.BadRequest.class,
    PpmsClient.BadRequest.class,
    UnsatisfiedServletRequestParameterException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public OperationOutcome handleBadRequest(Exception e, HttpServletRequest request) {
    return responseFor("structure", e, request);
  }

  @ExceptionHandler({HttpClientErrorException.NotFound.class, PpmsClient.NotFound.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public OperationOutcome handleNotFound(Exception e, HttpServletRequest request) {
    return responseFor("not-found", e, request);
  }

  @ExceptionHandler({PpmsClient.PpmsException.class, PpmsClient.SearchFailed.class})
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public OperationOutcome handleServiceUnavailable(Exception e, HttpServletRequest request) {
    return responseFor("service-unavailable", e, request);
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public OperationOutcome handleSnafu(Exception e, HttpServletRequest request) {
    return responseFor("exception", e, request);
  }

  /**
   * For constraint violation exceptions, we want to add a little more information in the exception
   * to present what exactly is wrong. We will distill which properties are wrong and why, but we
   * will not leak any values.
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public OperationOutcome handleValidationException(
      ConstraintViolationException e, HttpServletRequest request) {
    List<String> problems =
        e.getConstraintViolations()
            .stream()
            .map(v -> v.getPropertyPath() + " " + v.getMessage())
            .collect(Collectors.toList());

    return responseFor("structure", e, request, problems);
  }

  private OperationOutcome responseFor(String code, Exception e, HttpServletRequest request) {
    return responseFor(code, e, request, emptyList());
  }

  private OperationOutcome responseFor(
      String code, Exception e, HttpServletRequest request, List<String> problems) {
    StringBuilder diagnostics = new StringBuilder();
    diagnostics
        .append("Error: ")
        .append(e.getClass().getSimpleName())
        .append(" Timestamp:")
        .append(Instant.now())
        .append(", ")
        .append(e.getMessage());
    problems.forEach(p -> diagnostics.append('\n').append(p));

    OperationOutcome response =
        OperationOutcome.builder()
            .id(UUID.randomUUID().toString())
            .resourceType("OperationOutcome")
            .text(
                Narrative.builder()
                    .status(Narrative.NarrativeStatus.additional)
                    .div("<div>Failure: " + request.getRequestURI() + "</div>")
                    .build())
            .issue(
                singletonList(
                    OperationOutcome.Issue.builder()
                        .severity(OperationOutcome.Issue.IssueSeverity.fatal)
                        .code(code)
                        .diagnostics(diagnostics.toString())
                        .build()))
            .build();
    log.error("Response {}", response, e);
    return response;
  }
}
