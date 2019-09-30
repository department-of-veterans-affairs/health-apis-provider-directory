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
public final class AddressResponse implements VlerResponse {
  @JsonProperty("contacts")
  private List<Contacts> contacts;

  private Error error;

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
    /* Map to 'name'. */
    @JsonProperty("displayname") // Last, First
    private String displayname;

    /* Map to 'address'. */
    @JsonProperty("mail") // Email address
    private String mail;

    @JsonProperty("uid") // LH of email address
    private String uid;

    @JsonProperty("givenname") // First name
    private String givenname;

    @JsonProperty("sn") // Surname
    private String sn;

    @JsonProperty("physicaldeliveryofficename") // City, State
    private String physicaldeliveryofficename;

    @JsonProperty("o") // Company  name
    private String companyname;

    @JsonProperty("departmentnumber")
    private String departmentnumber;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("telephonenumber")
    private String telephonenumber;

    @JsonProperty("title")
    private String title;

    @JsonProperty("cn") // First Last
    private String cn;

    /* Map to 'managingOrganization'. */
    @JsonProperty("facility")
    private String facility;
  }
}
