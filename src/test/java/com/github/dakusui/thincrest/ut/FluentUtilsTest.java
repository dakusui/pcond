package com.github.dakusui.thincrest.ut;

import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;
import static com.github.dakusui.pcond.fluent.FluentUtils.value;
import static com.github.dakusui.pcond.fluent.FluentUtils.valueOfClass;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.shared.FluentTestUtils.*;
import static com.github.dakusui.shared.utils.TestForms.objectHashCode;
import static com.github.dakusui.thincrest.TestAssertions.assertThat;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class FluentUtilsTest extends TestBase {
  @Test
  public void whenPassingValidation_thenPasses$1() {
    assertThat(
        new Parent(),
        when().<Parent>as((Parent) value())
            .exercise(Parent::parentMethod1)
            .then()
            .<Parent>as(value())
            .verify(isEqualTo("returnValueFromParentMethod")).toPredicate());
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
              whenValueOfClass(Parent.class).asObject()
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
                  .addPredicate(not(isEqualTo("returnedStringFromChildMethod")))));
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
              .thenAllOf(asList(
                  tx -> tx.asValueOfClass(Parent.class)
                      .exercise(function("lambda:Parent::parentMethod1", Parent::parentMethod1))
                      .then()
                      .asString()
                      .isEqualTo("returnValueFromParentMethod"),
                  tx -> tx.asValueOfClass(Parent.class)
                      .exercise(function("Parent::parentMethod2", Parent::parentMethod2))
                      .exercise(function("lambda:Child::childMethod", Child::childMethod))
                      .then()
                      .asString()
                      // 'not(...)' is added to make the matcher fail.
                      .addPredicate(not(isEqualTo("returnedStringFromChildMethod"))))));
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
        list(123, list("Hello", "world")),
        allOf(
            when().at(0).asInteger()
                .then().equalTo(122),
            when().at(1).asListOfClass(String.class)
                .thenAllOf(asList(
                    (ListTransformer<Object, String> b) -> b
                        .at(0)
                        .asString()
                        .then()
                        .isEqualTo("hello"),
                    (ListTransformer<Object, String> b) -> b.at(1).asString()
                        .then().isEqualTo("world")))));
  }

  @Test(expected = ComparisonFailure.class)
  public void multiValueAssertionTest_anyOf() {
    try {
      assertThat(
          list(123, list("Hello", "world")),
          allOf(
              when().at(0).asInteger()
                  .then().equalTo(122),
              when().at(1).asListOfClass(String.class)
                  .thenAnyOf(asList(
                      (ListTransformer<Object, String> b) -> b.at(0).asString()
                          .then().isEqualTo("hello"),
                      (ListTransformer<Object, String> b) -> b.at(1).asString()
                          .then().isEqualTo("world")))));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void teeTest() {
    String hello = "hello";
    assertThat(
        hello,
        when()
            .asObject()
            .thenAllOf(singletonList(
                b -> b.as((String) value())
                    .exercise(objectHashCode())
                    .then()
                    .isInstanceOf(Integer.class))));
  }

  @Test
  public void teeTest2() {
    String hello = "hello";
    assertThat(
        hello,
        when().asObject().thenAllOf(singletonList(
            tx -> tx.as((String) value())
                .exercise(objectHashCode())
                .then()
                .isInstanceOf(Integer.class))));
  }

}
