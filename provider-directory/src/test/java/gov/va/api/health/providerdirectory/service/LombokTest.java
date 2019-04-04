package gov.va.api.health.providerdirectory.service;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public final class LombokTest {
  private static <T> void exerciseLombok(Class<T> pojoClass) {
    newInstance(pojoClass).equals(newInstance(pojoClass));
    newInstance(pojoClass).hashCode();
    newInstance(pojoClass).toString();
  }

  @SneakyThrows
  private static <T> T newInstance(Class<T> aClass) {
    Constructor<T> cons = aClass.getDeclaredConstructor();
    ReflectionUtils.makeAccessible(cons);
    return cons.newInstance();
  }

  @Test
  @SneakyThrows
  public void exerciseLombok() {
    for (Class<?> pojoClass :
        Arrays.asList(
            PpmsProviderSpecialtiesResponse.class,
            PpmsProviderSpecialtiesResponse.Value.class,
            ProviderContacts.class,
            ProviderContacts.Value.class,
            ProviderResponse.Value.class,
            ProviderResponse.class)) {
      exerciseLombok(pojoClass);
    }
  }
}
