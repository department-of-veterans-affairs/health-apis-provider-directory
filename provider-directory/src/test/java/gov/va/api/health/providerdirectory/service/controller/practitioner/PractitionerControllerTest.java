package gov.va.api.health.providerdirectory.service.controller.practitioner;



@SuppressWarnings("WeakerAccess")
public class PractitionerControllerTest {
  /*   RestTemplate restTemplate;
      @Mock PractitionerController.Transformer tx;
      PractitionerController controller;
      @Mock Bundler bundler;
      @Value("${ppms.url}") String baseUrl;

      @Before
      public void _init() {
          MockitoAnnotations.initMocks(this);
          controller = new PractitionerController(baseUrl, restTemplate,tx, bundler);
      }

      private void assertSearch(Supplier<Bundle> invocation, MultiValueMap<String, String> params) {
          ProviderResponse providerResponse = ProviderResponse.builder().build();
          ProviderContacts providerContacts = ProviderContacts.builder().build();
          ProviderWrapper root = ProviderWrapper.builder().providerContacts(providerContacts).providerResponse(providerResponse).build();
      }


      @Test
      public void searchByName() {
          assertSearch(
                  () -> controller.searchByName("me", 1, 1),
                  Parameters.builder().add("identifier", "me").add("page", 1).add("_count", 1).build());
      }

      @Test
      public void searchByIdentifier() {
          assertSearch(
                  () -> controller.searchByIdentifier("me", 1, 1),
                  Parameters.builder().add("identifier", "me").add("page", 1).add("_count", 1).build());
      }

      @Test
      public void searchByFamilyAndGiven() {
          assertSearch(
                  () -> controller.searchByFamilyAndGiven("myFamily","myGiven", 1, 1),
                  Parameters.builder().add("identifier", "me").add("page", 1).add("_count", 10).build());
      }
  */

}
