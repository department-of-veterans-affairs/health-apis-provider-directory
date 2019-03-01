package gov.va.api.health.providerdirectory.api.validation;

import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractSetFieldCounter<A extends Annotation>
    implements ConstraintValidator<A, Object> {

  protected A annotation;

  protected abstract String[] fields();

  /** Finds the getter method of the property provided in order to access the value. */
  private Method findGetter(Class<?> type, String name) {
    Method getter = null;
    PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(type, name);
    if (pd != null) {
      getter = pd.getReadMethod();
    }
    if (getter == null) {
      getter = BeanUtils.findMethodWithMinimalParameters(type, name);
    }
    if (getter == null) {
      throw new IllegalArgumentException(
          "Cannot find Java bean property or fluent getter: " + type.getName() + "." + name);
    }
    return getter;
  }

  @Override
  public void initialize(A annotation) {
    this.annotation = annotation;
  }

  protected abstract boolean isNumberOfFieldsSetValid(int numberOfFieldsSet);

  @Override
  public boolean isValid(Object value, ConstraintValidatorContext context) {
    int numberOfFieldsSet = 0;
    for (String fieldName : fields()) {
      if (valueOf(value, fieldName) != null) {
        numberOfFieldsSet++;
      }
    }
    return isNumberOfFieldsSetValid(numberOfFieldsSet);
  }

  /** Gets the value of the property provided. */
  @SneakyThrows
  private Object valueOf(Object o, String field) {
    return findGetter(o.getClass(), field).invoke(o);
  }
}
