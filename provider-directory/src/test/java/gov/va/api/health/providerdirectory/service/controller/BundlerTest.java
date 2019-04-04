package gov.va.api.health.providerdirectory.service.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.va.api.health.providerdirectory.api.bundle.AbstractBundle;
import gov.va.api.health.providerdirectory.api.bundle.AbstractBundle.BundleType;
import gov.va.api.health.providerdirectory.api.bundle.AbstractEntry;
import gov.va.api.health.providerdirectory.api.bundle.BundleLink;
import gov.va.api.health.providerdirectory.api.bundle.BundleLink.LinkRelation;
import gov.va.api.health.providerdirectory.api.elements.Meta;
import gov.va.api.health.providerdirectory.api.resources.Resource;
import gov.va.api.health.providerdirectory.service.controller.Bundler.BundleContext;
import gov.va.api.health.providerdirectory.service.controller.PageLinks.LinkConfig;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("WeakerAccess")
public class BundlerTest {

  private static final Function<FugaziCdwRoot, FugaziArgo> FUGAZIMUS_PRIME =
      x -> FugaziArgo.of(x.id());

  @Mock PageLinks links;
  Bundler bundler;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    bundler = new Bundler(links);
  }

  @Test
  public void bundlerBuildsGenericTypeBundle() {
    List<BundleLink> theLinks =
        Collections.singletonList(
            BundleLink.builder().relation(LinkRelation.self).url("http://whatever.com").build());
    when(this.links.create(Mockito.any())).thenReturn(theLinks);
    when(this.links.readLink(Mockito.any(), Mockito.any()))
        .thenReturn("http://one.com")
        .thenReturn("http://two.com")
        .thenReturn("http://three.com");

    LinkConfig linkConfig =
        LinkConfig.builder()
            .page(1)
            .recordsPerPage(3)
            .totalRecords(10)
            .queryParams(Parameters.forIdentity("1"))
            .path("api/Fugazi")
            .build();

    List<FugaziCdwRoot> xmlItems =
        Arrays.asList(FugaziCdwRoot.of(1), FugaziCdwRoot.of(2), FugaziCdwRoot.of(3));

    FugaziBundle expected = new FugaziBundle();
    expected.resourceType("Bundle");
    expected.type(BundleType.searchset);
    expected.total(10);
    expected.link(theLinks);
    expected.entry(
        Arrays.asList(
            FugaziEntry.of("http://one.com", FugaziArgo.of(1)),
            FugaziEntry.of("http://two.com", FugaziArgo.of(2)),
            FugaziEntry.of("http://three.com", FugaziArgo.of(3))));

    Object actual =
        bundler.bundle(
            BundleContext.of(
                linkConfig, xmlItems, FUGAZIMUS_PRIME, FugaziEntry::new, FugaziBundle::new));

    assertThat(actual).isEqualTo(expected);
  }

  @Value
  @Builder
  private static class FugaziArgo implements Resource {
    String id;
    Meta meta;
    String implicitRules;
    String language;

    private static FugaziArgo of(int id) {
      return FugaziArgo.builder().id("a" + id).build();
    }
  }

  private static class FugaziBundle extends AbstractBundle<FugaziEntry> {}

  @AllArgsConstructor(staticName = "of")
  @Getter
  private static class FugaziCdwRoot {
    int id;
  }

  private static class FugaziEntry extends AbstractEntry<FugaziArgo> {
    private static FugaziEntry of(String url, FugaziArgo a) {
      FugaziEntry entry = new FugaziEntry();
      entry.fullUrl(url);
      entry.resource(a);
      entry.search(Search.builder().mode(SearchMode.match).build());
      return entry;
    }
  }
}
