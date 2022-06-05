package com.github.dakusui.pcond.ut.types;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.forms.Predicates;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.Fluents.when;
import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static java.util.Arrays.asList;

public class ListTest {
  @Test
  public void listTest() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asListOf((String) value()).then().contains("world"));
  }

  @Test(expected = ComparisonFailure.class)
  public void listTestFailure() {
    List<String> value = asList("Hello", "world");
    try {
      TestAssertions.assertThat(value, when().asListOf((String) value()).then().contains("World"));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("contains[\"World\"]->true"));
      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("contains[\"World\"]->false"));
      throw e;
    }
  }
}
