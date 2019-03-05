package gov.va.api.health.providerdirectory.api.elements;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "http://hl7.org/fhir/STU3/element.html")
public interface Element {
  List<Extension> extension();

  String id();
}
