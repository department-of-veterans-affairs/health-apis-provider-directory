package gov.va.api.health.providerdirectory.tests;

import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.SentinelProperties;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SystemDefinitions {
  private static SystemDefinition lab() {
    String url = "https://sandbox-api.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, "/services/provider-directory/v0/"))
        .publicIds(labIds())
        .build();
  }

  private static Ids labIds() {
    return Ids.builder()
        .location("I2-4KG3N5YUSPTWD3DAFMLMRL5V5U000000")
        .practitioner("I2-HRJI2MVST2IQSPR7U5SACWIWZA000000")
        .build();
  }

  private static SystemDefinition local() {
    String url = "http://localhost";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 8121, "/provider-directory/v0"))
        .publicIds(localIds())
        .build();
  }

  private static Ids localIds() {
    return Ids.builder()
        .location("I2-K7WNFKZA3JCXL3CLT6D2HP7RRU000000")
        .practitioner("I2-TVUBUQIWCJ6NIPURPDPGIYLLLU000000")
        .build();
  }

  private static SystemDefinition production() {
    String url = "https://api.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, "/services/provider-directory/v0/"))
        .publicIds(productionIds())
        .build();
  }

  private static Ids productionIds() {
    return Ids.builder()
        .location("I2-WEIZUDRRQFULJACUVBXZO7EFOU000000")
        .practitioner("I2-6NVSMKEGQKNB3KRDXBGE7NRIEY000000")
        .build();
  }

  private static SystemDefinition qa() {
    String url = "https://blue.qa.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, "/provider-directory/v0/"))
        .publicIds(productionIds())
        .build();
  }

  private static Service serviceDefinition(String name, String url, int port, String apiPath) {
    return Service.builder()
        .url(SentinelProperties.optionUrl(name, url))
        .port(port)
        .apiPath(SentinelProperties.optionApiPath(name, apiPath))
        .build();
  }

  private static SystemDefinition staging() {
    String url = "https://blue.staging.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, "/provider-directory/v0/"))
        .publicIds(productionIds())
        .build();
  }

  private static SystemDefinition stagingLab() {
    String url = "https://blue.staging-lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, "/provider-directory/v0/"))
        .publicIds(labIds())
        .build();
  }

  /** Return the applicable system definition for the current environment. */
  public static SystemDefinition systemDefinition() {
    switch (Environment.get()) {
      case PROD:
        return production();
      case LAB:
        return lab();
      case LOCAL:
        return local();
      case QA:
        return qa();
      case STAGING:
        return staging();
      case STAGING_LAB:
        return stagingLab();
      default:
        throw new IllegalArgumentException("Unknown sentinel environment: " + Environment.get());
    }
  }

  @Value
  @Builder
  static final class Ids {
    @NonNull String location;

    @NonNull String practitioner;
  }

  @Value
  @Builder
  static final class Service {
    @NonNull String url;

    @NonNull Integer port;

    @NonNull String apiPath;

    String urlWithApiPath() {
      StringBuilder builder = new StringBuilder(url());
      builder.append(":");
      builder.append(port());
      if (!apiPath().startsWith("/")) {
        builder.append('/');
      }
      builder.append(apiPath());
      if (!apiPath.endsWith("/")) {
        builder.append('/');
      }
      return builder.toString();
    }
  }

  @Value
  @Builder
  static final class SystemDefinition {
    @NonNull Service internal;

    @NonNull Ids publicIds;
  }
}
