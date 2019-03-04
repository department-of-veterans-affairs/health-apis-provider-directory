package gov.va.api.health.providerdirectory.api;

import gov.va.api.health.providerdirectory.api.resources.Location;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

public interface OrganizationApi {
    @Operation(
            summary = "Location Read",
            description =
                    "location link",
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
    Location LocationRead(
            @Parameter(in = ParameterIn.PATH, name = "id", required = true) String id);


}
