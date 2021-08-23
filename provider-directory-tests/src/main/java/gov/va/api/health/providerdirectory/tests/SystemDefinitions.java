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
    // String url = "https://sandbox-api.va.gov";
    String url = "https://blue.lab.lighthouse.va.gov";
    return SystemDefinition.builder()
        .internal(serviceDefinition("internal", url, 443, "/provider-directory/v0/"))
        .publicIds(labIds())
        .build();
  }

  private static Ids labIds() {
    return Ids.builder()
        .location("I2-2FPCKUIXVR7RJLLG34XVWGZERM000000")
        .organization("I2-AKOTGEFSVKFJOPUKHIVJAH5VQU000000")
        .practitioner("I2-HRJI2MVST2IQSPR7U5SACWIWZA000000")
        .practitionerRole("I2-6KYHN4VYERE5OHKPXWAPAU5BO4000000")
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
        .organization("I2-U2YS4YSMVOGA4TNVOJB3RXVIQRQR7OXTDMFNC24L4YSKJKXSTCZA0000")
        .practitioner("I2-A5Q24JYL4AQKD664ASIIGBVYQUXVWZRBWWGVFCS7IBU27TJIZBFQ0000")
        .practitionerRole("I2-QVW4BN5ETDZ2F4OQZQ7H3RAUYU000000")
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
        throw new UnsupportedOperationException("LOCAL is unsupported");
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

    @NonNull String organization;

    @NonNull String practitioner;

    @NonNull String practitionerRole;
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
