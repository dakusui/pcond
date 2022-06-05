package com.github.dakusui.pcond.ut.types;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.forms.Predicates;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.pcond.Fluents.when;
import static com.github.dakusui.pcond.core.fluent.Fluent.value;

public class StreamTest {
  @Test
  public void streamTest() {
    Stream<String> value = Stream.of("Hello", "world");
    TestAssertions.assertThat(value, when().asStreamOf((String) value()).then().anyMatch(Predicates.isEqualTo("world")));
  }

  @Test(expected = ComparisonFailure.class)
  public void streamTestFailure() {
    Stream<String> value = Stream.of("Hello", "world");
    try {
      TestAssertions.assertThat(value, when().asStreamOf((String) value()).then().anyMatch(Predicates.isEqualTo("World")));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("anyMatch[isEqualTo[\"World\"]]->true"));
      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("anyMatch[isEqualTo[\"World\"]]->false"));
      throw e;
    }
  }

}
