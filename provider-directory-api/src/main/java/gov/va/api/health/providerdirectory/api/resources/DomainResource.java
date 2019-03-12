package gov.va.api.health.providerdirectory.api.resources;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import gov.va.api.health.providerdirectory.api.datatypes.SimpleResource;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import gov.va.api.health.providerdirectory.api.elements.Narrative;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "https://www.hl7.org/fhir/STU3/domainresource.html")
public interface DomainResource extends Resource {
  List<SimpleResource> contained();

  List<Extension> extension();

  List<Extension> modifierExtension();

  Narrative text();
}
