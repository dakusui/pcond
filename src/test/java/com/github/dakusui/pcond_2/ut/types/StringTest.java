package com.github.dakusui.pcond_2.ut.types;

import com.github.dakusui.thincrest.TestAssertions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.dakusui.pcond.fluent.FluentsInternal.value;
import static com.github.dakusui.thincrest.ut.FluentsInternalTest.Utils.when;
import static java.util.Collections.singletonList;

public class StringTest {
  @Test(expected = ComparisonFailure.class)
  public void testTransformToString() {
    Object obj = new Object() {
      @Override
      public String toString() {
        return "HELLO, WORLD";
      }
    };

    try {
      TestAssertions.assertThat(obj, when().asObject().transformToString(Object::toString).then().isEqualTo("Hello, world"));
    } catch (ComparisonFailure e) {
      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.allOf(CoreMatchers.containsString("Hello, world"), CoreMatchers.containsString("HELLO, WORLD")));
      throw e;
    }
  }


  @Test
  public void testSubstring1() {
    String s = "Hello, world";
    TestAssertions.assertThat(s, when().asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring2() {
    String s = "Hello, world";
    TestAssertions.assertThat(s, when().asObject().asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring3a() {
    String s = "Hello, world";
    TestAssertions.assertThat(s, when().asValueOf((String) value()).asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring3b() {
    String s = "Hello, world";
    TestAssertions.assertThat(s, when().asObject().asValueOf((String) value()).asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring4() {
    String s = "Hello, world";
    List<String> out = new LinkedList<>();
    TestAssertions.assertThat(s, when().asValueOf((String) value()).peek(out::add).asString().substring(2).then().isEqualTo("llo, world"));
    MatcherAssert.assertThat(out, CoreMatchers.equalTo(singletonList("Hello, world")));
  }

  @Test()
  public void testToUpperCase() {
    String s = "Hello, world";
    TestAssertions.assertThat(s, when().asString().toUpperCase().then().isEqualTo("HELLO, WORLD"));
  }

  @Test()
  public void testToLowerCase() {
    String s = "Hello, world";
    TestAssertions.assertThat(s, when().asString().toLowerCase().then().isEqualTo("hello, world"));
  }

}
