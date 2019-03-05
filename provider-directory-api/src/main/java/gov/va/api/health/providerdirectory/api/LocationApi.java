package gov.va.api.health.providerdirectory.api;

import gov.va.api.health.providerdirectory.api.datatypes.Identifier;
import gov.va.api.health.providerdirectory.api.resources.Location;
import gov.va.api.health.providerdirectory.api.resources.OperationOutcome;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface LocationApi {
    @Operation(
            summary = "Location Read",
            description =
                    "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-location.html",
            tags = {"Location"}
    )
    @GET
    @Path("Location/{id}")
    @ApiResponse(
            responseCode = "200",
            description = "Record found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = Location.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Not found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Bad request",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    Location locationRead(
            @Parameter(in = ParameterIn.PATH, name = "id", required = true) String id);

    @Operation(
            summary = "Location Search By Identifier",
            description =
                    "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-location.html",
            tags = {"Location"}
    )
    @GET
    @Path("Location")
    @ApiResponse(
            responseCode = "200",
            description = "Record found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = Location.Bundle.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Not found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Bad request",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    Location.Bundle locationSearchByIdentifier(
            @Parameter(in = ParameterIn.QUERY, required = true, name = "identifier") Identifier id,
            @Parameter(in = ParameterIn.QUERY, required = true, name = "code") String code,
            @Parameter(in = ParameterIn.QUERY, name = "page") @DefaultValue("1") int page,
            @Parameter(in = ParameterIn.QUERY, name = "_count") @DefaultValue("15") int count);

    @Operation(
            summary = "Location Search By Name",
            description =
                    "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-location.html",
            tags = {"Location"}
    )
    @GET
    @Path("Location")
    @ApiResponse(
            responseCode = "200",
            description = "Record found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = Location.Bundle.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Not found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Bad request",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    Location.Bundle locationSearchByName(
            @Parameter(in = ParameterIn.QUERY, required = true, name = "name") String name,
            @Parameter(in = ParameterIn.QUERY, name = "page") @DefaultValue("1") int page,
            @Parameter(in = ParameterIn.QUERY, name = "_count") @DefaultValue("15") int count);
    @Operation(
            summary = "Location Search By Address",
            description =
                    "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-location.html",
            tags = {"Location"}
    )
    @GET
    @Path("Location")
    @ApiResponse(
            responseCode = "200",
            description = "Record found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = Location.Bundle.class)
            )
    )
    @ApiResponse(
            responseCode = "400",
            description = "Not found",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Bad request",
            content =
            @Content(
                    mediaType = "application/json+fhir",
                    schema = @Schema(implementation = OperationOutcome.class)
            )
    )
    Location.Bundle locationSearchByaddress(
            @Parameter(in = ParameterIn.QUERY, required = true, name = "address") String address,
            @Parameter(in = ParameterIn.QUERY, name = "page") @DefaultValue("1") int page,
            @Parameter(in = ParameterIn.QUERY, name = "_count") @DefaultValue("15") int count);
}