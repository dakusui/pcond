package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

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
                  .thenVerifyWith(
                      matcherFor(Child.class)
                          .transformBy(function("lambda:Child::childMethod--by Printables.function() method", Child::childMethod))
                          // 'not(...)' is added to make the matcher fail.
                          .thenVerifyWith(not(isEqualTo("returnedStringFromChildMethod")))
                  )),
          TestException::new);
    } catch (TestException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          e.getMessage(),
          CoreMatchers.allOf(
              CoreMatchers.containsString("lambda:Parent::parentMethod1--by name() method"),
              CoreMatchers.containsString("lambda:Child::childMethod--by Printables.function() method")));
      throw e;
    }
  }

  @Test
  public void givenList_whenPassingMatcherForListOf_thenPasses() {
    validate(
        asList("Hello", "World"),
        and(matcherForListOf(String.class).transformBy(elementAt(0)).thenVerifyWith(isEqualTo("Hello")),
            matcherForListOf(String.class).transformBy(elementAt(1)).thenVerifyWith(isEqualTo("World"))
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
            matcherForArrayOf(String.class).transformBy(v -> v[0]).thenVerifyWith(equalTo("Hello")),
            matcherForArrayOf(String.class).transformBy(v -> v[1]).thenVerifyWith(equalTo("World"))
        ));
  }

  @Test
  public void givenString_whenPassingStringMatcher_thenPasses() {
    validate("Hello, world",
        matcherForString().transformBy(v -> v).thenVerifyWith(equalTo("Hello, world")));
  }

  @Test(expected = IllegalStateException.class)
  public void givenString_whenTransformingFunctionMissing_thenIllegalStateException() {
    try {
      validate("Hello, world",
          matcherForString().into(String.class).thenVerifyWith(equalTo("Hello, world")));
    } catch (IllegalStateException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(e.getMessage(),
          CoreMatchers.allOf(
              CoreMatchers.containsString("function"),
              CoreMatchers.containsString("transformBy")
          ));
      throw e;
    }
  }

  @Test(expected = IllegalStateException.class)
  public void givenString_whenCheckingPredicateMissing_thenIllegalStateException() {
    try {
      validate("Hello, world",
          matcherForString().transformBy(v -> v).build());
    } catch (IllegalStateException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(e.getMessage(),
          CoreMatchers.allOf(
              CoreMatchers.containsString("predicate"),
              CoreMatchers.containsString("thenVerifyWith")
          ));
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
