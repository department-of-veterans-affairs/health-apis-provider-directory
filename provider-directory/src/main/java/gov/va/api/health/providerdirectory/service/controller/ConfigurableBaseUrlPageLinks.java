package gov.va.api.health.providerdirectory.service.controller;

import gov.va.api.health.stu3.api.bundle.BundleLink;
import gov.va.api.health.stu3.api.bundle.BundleLink.LinkRelation;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/** This implementation uses a configurable base URL (provider-directory.url) for the links. */
@Service
public class ConfigurableBaseUrlPageLinks implements PageLinks {
  /**
   * The published URL for provider-directory, which is likely not the hostname of the machine
   * running this application.
   */
  private final String baseUrl;

  @Autowired
  public ConfigurableBaseUrlPageLinks(@Value("${provider-directory.url}") String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  public List<BundleLink> create(LinkConfig config) {
    LinkContext context = new LinkContext(config);
    List<BundleLink> links = new LinkedList<>();
    links.add(context.first());
    if (context.hasPrevious()) {
      links.add(context.previous());
    }
    links.add(context.self());
    if (context.hasNext()) {
      links.add(context.next());
    }
    links.add(context.last());
    return links;
  }

  @Override
  public String readLink(String resourcePath, String id) {
    return baseUrl + "/" + resourcePath + "/" + id;
  }

  /** This context wraps the link state to allow link creation to be clearly described. */
  @RequiredArgsConstructor
  private class LinkContext {
    private final LinkConfig config;

    BundleLink first() {
      return BundleLink.builder().relation(LinkRelation.first).url(toUrl(1)).build();
    }

    boolean hasNext() {
      return config.page() < lastPage();
    }

    boolean hasPrevious() {
      return config.page() > 1 && config.page() <= lastPage();
    }

    BundleLink last() {
      return BundleLink.builder().relation(LinkRelation.last).url(toUrl(lastPage())).build();
    }

    private int lastPage() {
      return (int) Math.ceil((double) config.totalRecords() / (double) config.recordsPerPage());
    }

    BundleLink next() {
      return BundleLink.builder().relation(LinkRelation.next).url(toUrl(config.page() + 1)).build();
    }

    BundleLink previous() {
      return BundleLink.builder().relation(LinkRelation.prev).url(toUrl(config.page() - 1)).build();
    }

    BundleLink self() {
      return BundleLink.builder().relation(LinkRelation.self).url(toUrl(config.page())).build();
    }

    private Stream<String> toKeyValueString(Entry<String, List<String>> entry) {
      return entry.getValue().stream().map((value) -> entry.getKey() + '=' + value);
    }

    private String toUrl(int page) {
      MultiValueMap<String, String> mutableParams = new LinkedMultiValueMap<>(config.queryParams());
      mutableParams.remove("page");
      mutableParams.remove("_count");
      StringBuilder msg = new StringBuilder(baseUrl).append('/');
      msg.append(config.path()).append('?');
      String params =
          mutableParams
              .entrySet()
              .stream()
              .sorted(Comparator.comparing(Entry::getKey))
              .flatMap(this::toKeyValueString)
              .collect(Collectors.joining("&"));
      if (!params.isEmpty()) {
        msg.append(params).append('&');
      }
      msg.append("page=").append(page).append("&_count=").append(config.recordsPerPage());
      return msg.toString();
    }
  }
}
