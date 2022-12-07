package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.pcond.forms.Predicates;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Predicate;

import static com.github.dakusui.thincrest.TestAssertions.assertThat;

public class PropertyBasedTest {

  @Test
  public void exerciseTest() {
    exercise(composeValue(), composePredicate());
  }

  public void exercise(String value, Predicate<String> predicate) {
    assertThat(value, predicate);
  }

  Predicate<String> composePredicate() {
    return Predicates.isEqualTo("HELLO");
  }

  String composeValue() {
    return "Hello";
  }

  interface TestCase {
    Predicate<String> composePredicate();

    String composeValue();

    Optional<Predicate<Throwable>> expectedException();
  }
}
