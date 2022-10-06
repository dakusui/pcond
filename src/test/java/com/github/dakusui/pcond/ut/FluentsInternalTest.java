package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.core.fluent.Fluent;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.fluent.FluentsInternal.*;
import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.utils.TestForms.objectHashCode;
import static java.util.Arrays.asList;

public class FluentsInternalTest extends TestBase {
  @Test
  public void whenPassingValidation_thenPasses$1() {
    assertThat(
        new Parent(),
        Utils.when().as((Parent) value())
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
        Utils.when().asListOf((String) value())
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
              Utils.whenValueOfClass(Parent.class).<Parent>asObject()
                  .exercise(function("lambda:Parent::parentMethod1", Parent::parentMethod1))
                  .then()
                  .asString()
                  .isEqualTo("returnValueFromParentMethod"),
              valueOfClass(Parent.class).asObject()
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
          Utils.whenValueOfClass(Supplier.class)
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

  @Test(expected = ComparisonFailure.class)
  public void multiValueAssertionTest_allOf() {
    assertThat(
        Utils.list(123, Utils.list("Hello", "world")),
        allOf(
            Utils.when().at(0).asInteger()
                .then().equalTo(122),
            Utils.when().at(1).asListOfClass(String.class).thenVerifyWithAllOf(asList(
                $().at(0).asString()
                    .then().isEqualTo("hello"),
                $().at(1).asString()
                    .then().isEqualTo("world")))));
  }

  @Test(expected = ComparisonFailure.class)
  public void multiValueAssertionTest_anyOf() {
    assertThat(
        Utils.list(123, Utils.list("Hello", "world")),
        allOf(
            Utils.when().at(0).asInteger()
                .then().equalTo(122),
            Utils.when().at(1).asListOfClass(String.class).thenVerifyWithAnyOf(asList(
                $().at(0).asString()
                    .then().isEqualTo("hello"),
                $().at(1).asString()
                    .then().isEqualTo("world")))));
  }

  @Test
  public void teeTest() {
    String hello = "hello";
    assertThat(
        hello,
        Utils.when().asObject().thenVerifyWith(allOf(
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
        Utils.when().asObject().then().verifyWith(allOf(
            $().as((String) value())
                .exercise(objectHashCode())
                .then()
                .isInstanceOf(Integer.class))));
  }

  public enum Utils {
    ;

    public static <T> Fluent<T> when() {
      return whenValueOf(value());
    }

    /**
     * Use the value returned from `value()` as the argument for this
     * method as a place-holder for the sake of readability.
     *
     * @param value A value place-holder.
     * @param <T>   The type of the object to be verified.
     * @return A new ObjectTransformer for type `T`.
     */
    public static <T> Fluent<T> whenValueOf(@SuppressWarnings("unused") T value) {
      return fluent("WHEN");
    }

    public static <T> Fluent<T> whenValueOfClass(@SuppressWarnings("unused") Class<T> klass) {
      return whenValueOf(value());
    }

    public static List<?> list(Object... args) {
      return asList(args);
    }
  }
}
