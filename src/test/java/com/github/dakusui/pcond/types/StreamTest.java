package com.github.dakusui.pcond.types;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.shared.IllegalValueException;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static com.github.dakusui.pcond.forms.Predicates.containsString;
import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class StreamTest extends TestBase {
  @Test
  public void streamTest() {
    Stream<String> value = Stream.of("Hello", "world");
    validate(value, when().asStreamOf((String) value()).then().anyMatch(Predicates.isEqualTo("world")));
  }

  @Test(expected = IllegalValueException.class)
  public void streamTestFailure() {
    Stream<String> value = Stream.of("Hello", "world");
    try {
      validate(value, when().asStreamOf((String) value()).then().anyMatch(Predicates.isEqualTo("World")));
    } catch (IllegalValueException e) {
      e.printStackTrace();
      //MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("anyMatch[isEqualTo[\"World\"]]->true"));
      //MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("anyMatch[isEqualTo[\"World\"]]->false"));
      validate(e.getMessage(), containsString("anyMatch[isEqualTo[\"World\"]]->true"));
      validate(e.getMessage(), containsString("anyMatch[isEqualTo[\"World\"]]->false"));
      throw e;
    }
  }

  @Test
  public void streamTransformerTest() {
    Stream<String> value = Stream.of("Hello", "world");
    validate(value, when().asObject().asStreamOf((String) value()).then().anyMatch(Predicates.isEqualTo("world")));
  }

  @Test(expected = IllegalValueException.class)
  public void streamTransformerTestFailure() {
    Stream<String> value = Stream.of("Hello", "world");
    validate(value, when().asObject().asStreamOf((String) value()).then().anyMatch(Predicates.isEqualTo("World")));
  }
}
