package gov.va.api.health.providerdirectory.api;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import javax.ws.rs.Path;

@OpenAPIDefinition(
  info =
      @Info(
        title = "Argonaut Provider Directory",
        version = "v1",
        description =
            "FHIR (Fast Healthcare Interoperability Resources) specification defines a set of"
                + " \"Resources\" that represent granular clinical concepts."
                + " This service is compliant with the FHIR Argonaut Provider Directory "
                + "Implementation Guide."
      ),
  servers = {
    @Server(
      url = "https://dev-api.va.gov/services/argonaut/v0/",
      description = "Development server"
    )
  },
  externalDocs =
      @ExternalDocumentation(
        description = "Argonaut Provider Directory Implementation Guide",
        url = "http://www.fhir.org/guides/argonaut/pd/index.html"
      )
)
@Path("/")
public interface ProviderDirectoryService
    extends LocationApi,
        OrganizationApi,
        EndpointApi,
        PractitionerRoleApi,
        PractitionerApi,
        MetadataApi {

  class ArgonautServiceException extends RuntimeException {
    ArgonautServiceException(String message) {
      super(message);
    }
  }

  class SearchFailed extends ArgonautServiceException {
    @SuppressWarnings("WeakerAccess")
    public SearchFailed(String id, String reason) {
      super(id + " Reason: " + reason);
    }
  }

  class UnknownResource extends ArgonautServiceException {
    @SuppressWarnings("WeakerAccess")
    public UnknownResource(String id) {
      super(id);
    }
  }
}
