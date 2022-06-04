package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.Fluents.*;
import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.utils.TestForms.objectHashCode;
import static java.util.Arrays.asList;

public class FluentsTest extends TestBase {
  @Test
  public void whenPassingValidation_thenPasses$1() {
    assertThat(
        new Parent(),
        when().as((Parent) value())
            .exercise(Parent::parentMethod1)
            .then()
            .verifyWith(isEqualTo("returnValueFromParentMethod")).build());
  }


  @Test(expected = ComparisonFailure.class)
  public void test4() {
    assertThat(
        "hello",
        not(equalsIgnoreCase("HELLO"))
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void expectationFlipping() {
    assertThat(
        Stream.of("hello"),
        noneMatch(equalsIgnoreCase("HELLO"))
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void example() {
    assertThat(
        asList("Hello", "world"),
        when().asListOf((String) value())
            .elementAt(0)
            .then().asString()
            .findSubstrings("hello", "world")
            .contains("hello"));
  }

  @Test(expected = ComparisonFailure.class)
  public void example2() {
    assertThat(
        "stringHelloworlD!",
        explainableStringIsEqualTo("Hello")
    );
  }

  @Test(expected = ComparisonFailure.class)
  public void example3() {
    try {
      assertThat(
          new Parent(),
          allOf(
              whenValueOfClass(Parent.class).<Parent>asObject()
                  .exercise(function("lambda:Parent::parentMethod1", Parent::parentMethod1))
                  .then()
                  .asString()
                  .isEqualTo("returnValueFromParentMethod"),
              whenValueOfClass(Parent.class).<Parent>asObject()
                  .exercise(function("Parent::parentMethod2", Parent::parentMethod2))
                  .exercise(function("lambda:Child::childMethod", Child::childMethod))
                  .then()
                  .asString()
                  // 'not(...)' is added to make the matcher fail.
                  .testPredicate(not(isEqualTo("returnedStringFromChildMethod")))));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void example4() {
    try {
      assertThat(
          (Supplier<Parent>) Parent::new,
          whenValueOfClass(Supplier.class)
              .asObject()
              .exercise(Supplier::get)
              .then().verifyWith(allOf(
                  $().as((Parent) value())
                      .exercise(function("lambda:Parent::parentMethod1", Parent::parentMethod1))
                      .then().asString()
                      .isEqualTo("returnValueFromParentMethod"),
                  $().asValueOfClass(Parent.class)
                      .exercise(function("Parent::parentMethod2", Parent::parentMethod2))
                      .exercise(function("lambda:Child::childMethod", Child::childMethod))
                      .then().asString()
                      // 'not(...)' is added to make the matcher fail.
                      .testPredicate(not(isEqualTo("returnedStringFromChildMethod"))))));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  static class Parent {
    public String parentMethod1() {
      return "returnValueFromParentMethod";
    }

    public Child parentMethod2() {
      return new Child();
    }
  }

  static class Child {
    public String childMethod() {
      return "returnedStringFromChildMethod";
    }
  }

  @Test
  public void teeTest() {
    String hello = "hello";
    assertThat(
        hello,
        when().asObject().thenWith(allOf(
            $().as((String) value())
                .exercise(objectHashCode())
                .then().isInstanceOf(Integer.class)))
    );
  }

  @Test
  public void teeTest2() {
    String hello = "hello";
    assertThat(
        hello,
        when().asObject().then().verifyWith(allOf(
            $().as((String) value())
                .exercise(objectHashCode())
                .then().isInstanceOf(Integer.class))));
  }
}
