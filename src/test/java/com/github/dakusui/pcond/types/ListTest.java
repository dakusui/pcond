package com.github.dakusui.pcond.types;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.shared.IllegalValueException;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;
import static java.util.Arrays.asList;

public class ListTest extends TestBase {
  @Test
  public void listTest() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asListOf((String) value()).then().contains("world"));
  }

  @Test(expected = IllegalValueException.class)
  public void listTestFailure() {
    List<String> value = asList("Hello", "world");
    try {
      validate(value, when().asListOf((String) value()).then().contains("World"));
    } catch (IllegalValueException e) {
      e.printStackTrace();
      //      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("contains[\"World\"]->true"));
      //      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("contains[\"World\"]->false"));
      validate(e.getMessage(), containsString("contains[\"World\"]->true"));
      validate(e.getMessage(), containsString("contains[\"World\"]->false"));
      throw e;
    }
  }

  @Test
  public void listTransformerTest() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asObject().asListOf((String) value()).then().contains("world"));
  }

  @Test(expected = IllegalValueException.class)
  public void listTransformerTestFailure() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asObject().asListOf((String) value()).then().contains("World"));
  }

  @Test(expected = IllegalValueException.class)
  public void listCheckerTest_isEmpty_fail() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asListOf((String) value()).then().isEmpty());
  }

  @Test
  public void listCheckerTest_isEmpty_pass() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asListOf((String) value()).then().isNotEmpty());
  }

  @Test
  public void listCheckerTest_findElementsInOrder() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asListOf((String) value()).then().findElementsInOrder("Hello", "world"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void listCheckerTest_findElementsInOrderBy() {
    List<String> value = asList("Hello", "world");
    validate(value, when().asListOf((String) value()).then().findElementsInOrderBy(Predicates.isEqualTo("Hello"), Predicates.isEqualTo("world")));
  }
}
