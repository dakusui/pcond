package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.forms.Matchers;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.Validations.validate;
import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Functions.size;
import static com.github.dakusui.pcond.forms.Matchers.*;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.function;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

public class MatchersTest extends TestBase {
  @Test
  public void whenPassingValidation_thenPasses$1() {
    validate(
        new Parent(),
        matcherFor(Parent.class)
            .transformBy(Parent::parentMethod1)
            .thenVerifyWith(isEqualTo("returnValueFromParentMethod")),
        TestException::new);
  }

  @Test(expected = TestException.class)
  public void whenValidationWithIntentionallyFailingPredicate_thenExceptionThrown$2() {
    try {
      validate(
          new Parent(),
          allOf(
              matcherFor(Parent.class)
                  .name("lambda:Parent::parentMethod1--by name() method")
                  .transformBy(Parent::parentMethod1)
                  .thenVerifyWith(isEqualTo("returnValueFromParentMethod")),
              matcherFor(Parent.class).name("parentMethod2")
                  .transformBy(Parent::parentMethod2)
                  .into(Child.class)
                  .thenVerifyWith(
                      matcherFor(Child.class)
                          .transformBy(function("lambda:Child::childMethod--by Printables.function()", Child::childMethod))
                          // 'not(...)' is added to make the matcher fail.
                          .thenVerifyWith(not(isEqualTo("returnedStringFromChildMethod")))
                  )),
          TestException::new);
    } catch (TestException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          e.getMessage(),
          CoreMatchers.allOf(
              CoreMatchers.containsString("lambda:Parent::parentMethod1--by name()"),
              CoreMatchers.containsString("lambda:Child::childMethod--by Printables.function()")));
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void example() {
    try {
      assertThat(
          new Parent(),
          allOf(
              matcherFor(Parent.class)
                  .transformBy(function("lambda:Parent::parentMethod1--by name() method", Parent::parentMethod1))
                  .thenVerifyWith(isEqualTo("returnValueFromParentMethod")),
              matcherFor(Parent.class).name("parentMethod2")
                  .transformBy(function("Parent::parentMethod2", Parent::parentMethod2))
                  .into(Child.class)
                  .thenVerifyWith(
                      matcherFor(Child.class)
                          .transformBy(function("lambda:Child::childMethod--by Printables.function() method", Child::childMethod))
                          // 'not(...)' is added to make the matcher fail.
                          .thenVerifyWith(not(isEqualTo("returnedStringFromChildMethod")))
                  )));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void givenRawLambdas$whenFailingAssertionPerformed$thenComparisonFailureThrown() {
    assertThat(
        new Parent(),
        matcherFor(Parent.class)
            .transformBy(Parent::parentMethod2).into(Child.class)
            .andThen(Child::childMethod)
            .thenVerifyWith(equalTo("hello")));
  }

  @Test
  public void givenList_whenPassingMatcherForListOf_thenPasses() {
    validate(
        asList("Hello", "World"),
        and(matcherForListOf(String.class).transformBy(elementAt(0)).thenVerifyWith(isEqualTo("Hello")),
            matcherForListOf(String.class).transformBy(l -> l.get(1).toLowerCase()).thenVerifyWith(isEqualTo("world"))
        ));
  }

  @Test
  public void givenCollection_whenPassingMatcherForListOf_thenPasses() {
    validate(
        unmodifiableCollection(asList("Hello", "World")),
        matcherForCollectionOf(String.class).transformBy(size()).thenVerifyWith(isEqualTo(2))
    );
  }

  @Test
  public void givenArray_whenPassingMatcher_thenPasses() {
    validate(new String[] { "Hello", "World" },
        allOf(
            matcherForArrayOf(String.class).transformBy(v -> v[0]).into(String.class).thenVerifyWith(equalTo("Hello")),
            matcherForArrayOf(String.class).transformBy(v -> v[1]).into(String.class).thenVerifyWith(equalTo("World"))
        ));
  }

  @Test
  public void givenString_whenPassingStringMatcher_thenPasses() {
    validate("Hello, world",
        matcherForString().transformBy(v -> v).thenVerifyWith(equalTo("Hello, world")));
  }


  @Test
  public void example2() {

  }

  @Test//(expected = ComparisonFailure.class)
  public void findSubstringsTest() {
    String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur";
    try {
      assertThat(text, Matchers.findSubstrings("Gallia", "quarum", "Belgium", "nostra"));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test//(expected = ComparisonFailure.class)
  public void findRegexesTest() {
    String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur";
    try {
      assertThat(text, Matchers.findRegexes("Gall.a", "quar.m", "Belgium", "nostr(um|a)"));
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

  static class TestException extends RuntimeException {
    TestException(String message) {
      super(message);
    }
  }
}
