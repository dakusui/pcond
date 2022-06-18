package com.github.dakusui.pcond.ut.types;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.fluent.Fluents.when;
import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static java.util.Arrays.asList;

public class ListTest extends TestBase {
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

  @Test
  public void listTransformerTest() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asObject().asListOf((String) value()).then().contains("world"));
  }

  @Test(expected = ComparisonFailure.class)
  public void listTransformerTestFailure() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asObject().asListOf((String) value()).then().contains("World"));
  }

  @Test(expected = ComparisonFailure.class)
  public void listVerifierTest_isEmpty_fail() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asListOf((String) value()).then().isEmpty());
  }

  @Test
  public void listVerifierTest_isEmpty_pass() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asListOf((String) value()).then().isEmpty().negate());
  }

  @Test
  public void listVerifierTest_findElementsInOrder() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asListOf((String) value()).then().findElementsInOrder("Hello", "world"));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void listVerifierTest_findElementsInOrderBy() {
    List<String> value = asList("Hello", "world");
    TestAssertions.assertThat(value, when().asListOf((String) value()).then().findElementsInOrderBy(Predicates.isEqualTo("Hello"), Predicates.isEqualTo("world")));
  }
}
