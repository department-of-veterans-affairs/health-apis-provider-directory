package gov.va.api.health.providerdirectory.api.bundle;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import gov.va.api.health.providerdirectory.api.Fhir;
import gov.va.api.health.providerdirectory.api.datatypes.Signature;
import gov.va.api.health.providerdirectory.api.elements.Meta;
import gov.va.api.health.providerdirectory.api.resources.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Schema(description = "http://hl7.org/fhir/DSTU2/bundle.html")
public abstract class AbstractBundle<N extends AbstractEntry<?>> implements Resource {
  @NotBlank protected String resourceType;

  @Pattern(regexp = Fhir.ID)
  protected String id;

  @Valid protected Meta meta;

  @Pattern(regexp = Fhir.URI)
  protected String implicitRules;

  @Pattern(regexp = Fhir.CODE)
  protected String language;

  @NotNull
  protected BundleType type;

  @Min(0)
  protected Integer total;

  @Valid protected List<BundleLink> link;
  @Valid protected List<N> entry;
  @Valid protected Signature signature;

  @SuppressWarnings("unused")
  public enum BundleType {
    document,
    message,
    transaction,
    @JsonProperty("transaction-response")
    transaction_response,
    batch,
    @JsonProperty("batch-response")
    batch_response,
    history,
    searchset,
    collection
  }
}
