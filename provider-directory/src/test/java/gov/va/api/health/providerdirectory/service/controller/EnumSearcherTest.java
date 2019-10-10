package gov.va.api.health.providerdirectory.service.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.SneakyThrows;
import org.junit.Test;

public class EnumSearcherTest {
  @Test
  public void searcherFindsAnnotationIfHyphens() {
    EnumSearcher<?> e = EnumSearcher.of(sample.class);
    assertThat(e.find("hello-world")).isEqualTo(sample.hello_world);
  }

  @Test
  @SneakyThrows
  public void searcherFindsAnnotationIfUnderscores() {
    EnumSearcher<?> e = EnumSearcher.of(sample.class);
    assertThat(e.find("HELLO")).isEqualTo(sample._HELLO);
    assertThat(e.find("_world")).isEqualTo(sample._world);
  }

  enum sample {
    @JsonProperty("HELLO")
    _HELLO,
    @JsonProperty("world")
    _world,
    @JsonProperty("hello-world")
    hello_world
  }
}
