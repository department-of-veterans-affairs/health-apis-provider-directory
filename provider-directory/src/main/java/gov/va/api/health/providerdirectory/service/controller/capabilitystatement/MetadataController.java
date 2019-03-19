package gov.va.api.health.providerdirectory.service.controller.capabilitystatement;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import gov.va.api.health.providerdirectory.api.datatypes.CodeableConcept;
import gov.va.api.health.providerdirectory.api.datatypes.Coding;
import gov.va.api.health.providerdirectory.api.datatypes.ContactDetail;
import gov.va.api.health.providerdirectory.api.datatypes.ContactPoint;
import gov.va.api.health.providerdirectory.api.datatypes.ContactPoint.ContactPointSystem;
import gov.va.api.health.providerdirectory.api.elements.Extension;
import gov.va.api.health.providerdirectory.api.elements.Reference;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.AcceptUnknown;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.Kind;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.ResourceInteraction;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.ResourceInteractionCode;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.Rest;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.RestMode;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.RestResource;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.RestSecurity;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.SearchParamType;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.Software;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
  value = {"/api/metadata"},
  produces = {"application/json", "application/json+fhir", "application/fhir+json"}
)
@AllArgsConstructor(onConstructor = @__({@Autowired}))
class MetadataController {

  private static final String LOCATION_HTML =
      "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-location.html";

  private static final String ORGANIZATION_HTML =
      "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-organization.html";

  private static final String ENDPOINT_HTML =
      "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-endpoint.html";

  private static final String PRACTITIONER_HTML =
      "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitioner.html";

  private static final String PRACTITIONERROLE_HTML =
      "http://www.fhir.org/guides/argonaut/pd/StructureDefinition-argo-practitionerrole.html";

  private final CapabilityStatementProperties properties;

  private List<ContactDetail> contact() {
    return singletonList(
        ContactDetail.builder()
            .name(properties.getContact().getName())
            .telecom(
                singletonList(
                    ContactPoint.builder()
                        .system(ContactPointSystem.email)
                        .value(properties.getContact().getEmail())
                        .build()))
            .build());
  }

  private Collection<SearchParam> endpointSearchParams() {
    return asList(SearchParam.IDENTIFIER, SearchParam.ORGANIZATION, SearchParam.NAME);
  }

  private Collection<SearchParam> locationSearchParams() {
    return asList(SearchParam.ADDRESS, SearchParam.IDENTIFIER, SearchParam.NAME);
  }

  private Collection<SearchParam> organizationSearchParams() {
    return asList(SearchParam.IDENTIFIER, SearchParam.ADDRESS, SearchParam.NAME);
  }

  private Collection<SearchParam> practitionerRoleSearchParams() {
    return asList(
        SearchParam.IDENTIFIER, SearchParam.FAMILY, SearchParam.GIVEN, SearchParam.SPECIALTY);
  }

  private Collection<SearchParam> practitionerSearchParams() {
    return asList(SearchParam.FAMILY, SearchParam.GIVEN, SearchParam.IDENTIFIER);
  }

  @GetMapping
  CapabilityStatement read() {
    return CapabilityStatement.builder()
        .resourceType("CapabilityStatement")
        .id(properties.getId())
        .version(properties.getVersion())
        .name(properties.getName())
        .publisher(properties.getPublisher())
        .contact(contact())
        .date(properties.getPublicationDate())
        .description(properties.getDescription())
        .kind(Kind.capability)
        .software(software())
        .fhirVersion(properties.getFhirVersion())
        .status(properties.getStatus())
        .acceptUnknown(AcceptUnknown.no)
        .format(asList("application/json+fhir", "application/json", "application/fhir+json"))
        .rest(rest())
        .build();
  }

  private List<RestResource> resources() {
    return Stream.of(
            support("Location").documentation(LOCATION_HTML).search(locationSearchParams()).build(),
            support("Endpoint").documentation(ENDPOINT_HTML).search(endpointSearchParams()).build(),
            support("Organization")
                .documentation(ORGANIZATION_HTML)
                .search(organizationSearchParams())
                .build(),
            support("Practitioner")
                .documentation(PRACTITIONER_HTML)
                .search(practitionerSearchParams())
                .build(),
            support("PractitionerRole")
                .documentation(PRACTITIONERROLE_HTML)
                .search(practitionerRoleSearchParams())
                .build())
        .map(SupportedResource::asResource)
        .collect(Collectors.toList());
  }

  private List<Rest> rest() {
    return singletonList(
        Rest.builder()
            .mode(RestMode.server)
            .security(restSecurity())
            .resource(resources())
            .build());
  }

  private RestSecurity restSecurity() {
    return RestSecurity.builder()
        .extension(
            singletonList(
                Extension.builder()
                    .url("http://fhir-registry.smarthealthit.org/StructureDefinition/oauth-uris")
                    .extension(
                        asList(
                            Extension.builder()
                                .url("token")
                                .valueUri(properties.getSecurity().getTokenEndpoint())
                                .build(),
                            Extension.builder()
                                .url("authorize")
                                .valueUri(properties.getSecurity().getAuthorizeEndpoint())
                                .build()))
                    .build()))
        .cors(true)
        .service(singletonList(smartOnFhirCodeableConcept()))
        .description(properties.getSecurity().getDescription())
        .build();
  }

  private CodeableConcept smartOnFhirCodeableConcept() {
    return CodeableConcept.builder()
        .coding(
            singletonList(
                Coding.builder()
                    .system("http://hl7.org/fhir/restful-security-service")
                    .code("SMART-on-FHIR")
                    .display("SMART-on-FHIR")
                    .build()))
        .build();
  }

  private Software software() {
    return Software.builder().name(properties.getSoftwareName()).build();
  }

  private SupportedResource.SupportedResourceBuilder support(String type) {
    return SupportedResource.builder().properties(properties).type(type);
  }

  @Getter
  @AllArgsConstructor
  enum SearchParam {
    IDENTIFIER("identifier", SearchParamType.string),
    SPECIALTY("specialty", SearchParamType.string),
    ORGANIZATION("organization", SearchParamType.string),
    NAME("name", SearchParamType.string),
    GIVEN("given", SearchParamType.string),
    FAMILY("family", SearchParamType.string),
    ADDRESS("address", SearchParamType.string);

    private final String param;

    private final SearchParamType type;
  }

  @Value
  @Builder
  private static class SupportedResource {

    String type;

    String documentation;

    @Singular("searchBy")
    Set<SearchParam> search;

    CapabilityStatementProperties properties;

    RestResource asResource() {
      return RestResource.builder()
          .type(type)
          .profile(Reference.builder().reference(documentation).build())
          .interaction(interactions())
          .searchParam(searchParams())
          .build();
    }

    private List<ResourceInteraction> interactions() {
      if (search.isEmpty()) {
        return singletonList(readable());
      }
      return asList(searchable(), readable());
    }

    private ResourceInteraction readable() {
      return ResourceInteraction.builder()
          .code(ResourceInteractionCode.read)
          .documentation(properties.getResourceDocumentation())
          .build();
    }

    private List<CapabilityStatement.SearchParam> searchParams() {
      if (search.isEmpty()) {
        return null;
      }
      return search
          .stream()
          .map(
              s -> CapabilityStatement.SearchParam.builder().name(s.param()).type(s.type()).build())
          .collect(Collectors.toList());
    }

    private ResourceInteraction searchable() {
      return ResourceInteraction.builder()
          .code(ResourceInteractionCode.search_type)
          .documentation(properties.getResourceDocumentation())
          .build();
    }
  }
}
