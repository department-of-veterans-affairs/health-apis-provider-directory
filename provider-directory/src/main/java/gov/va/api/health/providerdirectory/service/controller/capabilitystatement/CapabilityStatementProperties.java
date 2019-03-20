package gov.va.api.health.providerdirectory.service.controller.capabilitystatement;

import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("DefaultAnnotationParam")
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("capability")
@Data
@Accessors(fluent = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapabilityStatementProperties {
  private String id;
  private String version;
  private String name;
  private String publisher;
  private ContactProperties contact;
  private String publicationDate;
  private String description;
  private String softwareName;
  private String fhirVersion;
  private SecurityProperties security;
  private String resourceDocumentation;
  private Status status;

  @Data
  @Accessors(fluent = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class ContactProperties {
    private String name;
    private String email;
  }

  @Data
  @Accessors(fluent = false)
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class SecurityProperties {
    private String tokenEndpoint;
    private String authorizeEndpoint;
    private String description;
  }
}
