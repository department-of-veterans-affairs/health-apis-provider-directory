package gov.va.api.health.providerdirectory.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class AddressResponse {
  @JsonProperty("contacts")
  private List<Contacts> contacts;

  /** Lazy getter. */
  public List<Contacts> contacts() {
    if (contacts == null) {
      contacts = new ArrayList<>();
    }
    return contacts;
  }

  @Data
  @Builder
  @JsonIgnoreProperties(ignoreUnknown = true)
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Contacts {

    @JsonProperty("displayname")
    private String displayName;

    @JsonProperty("mail")
    private String emailAddress;

    @JsonProperty("uid")
    private String uid;

    @JsonProperty("givenname")
    private String givenName;

    @JsonProperty("sn")
    private String surname;

    @JsonProperty("physicaldeliveryofficename")
    private String officeCityState;

    @JsonProperty("o")
    private String companyName;

    @JsonProperty("departmentnumber")
    private String departmentNumber;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("telephonenumber")
    private String telephoneNumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("cn")
    private String commonName;

    @JsonProperty("facility")
    private String facility;
  }
}
