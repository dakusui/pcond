package com.github.dakusui.pcond.types;

import com.github.dakusui.shared.IllegalValueException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static com.github.dakusui.pcond.fluent.FluentsInternal.value;
import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;
import static java.util.Collections.singletonList;

public class StringTest {
  @Test(expected = IllegalValueException.class)
  public void testTransformToString() {
    Object obj = new Object() {
      @Override
      public String toString() {
        return "HELLO, WORLD";
      }
    };

    try {
      validate(obj, when().asObject().toString(Object::toString).then().isEqualTo("Hello, world"));
    } catch (IllegalValueException e) {
      MatcherAssert.assertThat(
          //e.getExpected(),
          e.getMessage(),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Hello, world"),
              CoreMatchers.containsString("HELLO, WORLD")));
      throw e;
    }
  }


  @Test
  public void testSubstring1() {
    String s = "Hello, world";
    validate(s, when().asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring2() {
    String s = "Hello, world";
    validate(s, when().asObject().asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring3a() {
    String s = "Hello, world";
    validate(s, when().asValueOf((String) value()).asString().substring(2).then().isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring3b() {
    String s = "Hello, world";
    validate(
        s,
        when().asObject().asValueOf((String) value())
            .asString().substring(2)
            .then()
            .isEqualTo("llo, world"));
  }

  @Test
  public void testSubstring4() {
    String s = "Hello, world";
    List<String> out = new LinkedList<>();
    validate(
        s,
        when().asValueOf((String) value())
            .peek(out::add)
            .asString()
            .substring(2)
            .then()
            .isEqualTo("llo, world"));
    MatcherAssert.assertThat(out, CoreMatchers.equalTo(singletonList("Hello, world")));
  }

  @Test()
  public void testToUpperCase() {
    String s = "Hello, world";
    validate(s, when().asString().toUpperCase().then().isEqualTo("HELLO, WORLD"));
  }

  @Test()
  public void testToLowerCase() {
    String s = "Hello, world";
    validate(s, when().asString().toLowerCase().then().isEqualTo("hello, world"));
  }
}
