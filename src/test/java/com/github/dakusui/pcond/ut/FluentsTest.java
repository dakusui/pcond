package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Fluents;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.Validations.validate;
import static com.github.dakusui.pcond.Fluents.*;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.function;
import static java.util.Arrays.asList;

public class FluentsTest extends TestBase {
  @Test
  public void whenPassingValidation_thenPasses$1() {
    assertThat(
        new Parent(),
        when((Parent) value())
            .transformToObject(Parent::parentMethod1)
            .then()
            .with(isEqualTo("returnValueFromParentMethod")).build());
  }

  class ChildTransformer<OIN> extends AbstractObjectTransformer<ChildTransformer<OIN>, OIN, Child> {

    /**
     * @param parent
     * @param function
     */
    public <IN> ChildTransformer(Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Child> function) {
      super(parent, function);
    }

    public StringTransformer<OIN> childMethod() {
      return this.transformToString(Printables.function("childMethod", Child::childMethod));
    }
  }
  @Test
  public void whenPassingValidation_thenPasses$2() {
    assertThat(
        new Parent(),
        when((Parent) value())
            .<Child, ChildTransformer<Parent>>transform(Printables.function("parentMethod2", Parent::parentMethod2), ChildTransformer::new)
            .childMethod()
            .then()
            .with(isEqualTo("returnValueFromParentMethod")).build());
  }

  @Test
  public void test4() {
    assertThat(
        "hello",
        not(equalsIgnoreCase("HELLO"))
    );
  }

  @Test
  public void expectationFlipping() {
    assertThat(
        Stream.of("hello"),
        noneMatch(equalsIgnoreCase("HELLO"))
    );
  }

    @Test(expected = TestException.class)
  public void whenValidationWithIntentionallyFailingPredicate_thenExceptionThrown$2() {
    try {
      validate(
          new Parent(),
          allOf(
              when((Parent) value())
                  .thenAsObject(function("lambda:Parent::parentMethod1--by name() method", Parent::parentMethod1))
                  .with(isEqualTo("returnValueFromParentMethod"))
                  .verify(),
              when((Parent) value())
                  .thenAsObject(function("parentMethod2", Parent::parentMethod2))
                  .with(
                      when((Child) value())
                          .transformToString(function("lambda:Child::childMethod--by Printables.function()", Child::childMethod))
                          .then()
                          //         'not(...)' is added to make the matcher fail.
                          .equalsIgnoreCase("hello")
                          .with(not(isEqualTo("returnedStringFromChildMethod"))).verify())
                  .verify()),
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

  @Test
  public void example() {
    assertThat(
        asList("Hello", "world"),
        whenListOf((String) value())
            .elementAt(0)
            .thenAsString()
            .findSubstrings("hello", "world")
            .contains("hello")
            .verify()
    );
  }

  /*
  @Test(expected = ComparisonFailure.class)
  public void example() {
    try {
      assertThat(
          new Parent(),
          allOf(
              matcherFor(Parent.class)
                  .transformBy(function("lambda:Parent::parentMethod1--by name() method", Parent::parentMethod1))
                  .verifyWith(isEqualTo("returnValueFromParentMethod")),
              matcherFor(Parent.class).name("parentMethod2")
                  .transformBy(function("Parent::parentMethod2", Parent::parentMethod2))
                  .into(Child.class)
                  .verifyWith(
                      matcherFor(Child.class)
                          .transformBy(function("lambda:Child::childMethod--by Printables.function() method", Child::childMethod))
                          // 'not(...)' is added to make the matcher fail.
                          .verifyWith(not(isEqualTo("returnedStringFromChildMethod")))
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
            .verifyWith(equalTo("hello")));

  }

  @Test
  public void givenList_whenPassingMatcherForListOf_thenPasses() {
    validate(
        asList("Hello", "World"),
        and(matcherForListOf(String.class).transformBy(elementAt(0)).verifyWith(isEqualTo("Hello")),
            matcherForListOf(String.class).transformBy(l -> l.get(1).toLowerCase()).verifyWith(isEqualTo("world"))
        ));
  }

  @Test
  public void givenCollection_whenPassingMatcherForListOf_thenPasses() {
    validate(
        unmodifiableCollection(asList("Hello", "World")),
        matcherForCollectionOf(String.class).transformBy(size()).verifyWith(isEqualTo(2))
    );
  }

  @Test
  public void givenArray_whenPassingMatcher_thenPasses() {
    validate(new String[] { "Hello", "World" },
        allOf(
            matcherForArrayOf(String.class).transformBy(v -> v[0]).into(String.class).verifyWith(equalTo("Hello")),
            matcherForArrayOf(String.class).transformBy(v -> v[1]).into(String.class).verifyWith(equalTo("World"))
        ));
  }

  @Test
  public void givenString_whenPassingStringMatcher_thenPasses() {
    validate("Hello, world",
        matcherForString().transformBy(v -> v).verifyWith(equalTo("Hello, world")));
  }
*/

  @Test
  public void matcherForStringWorksFine() {
    assertThat("Hello, world", Fluents.fluent().string().substring(2).toUpperCase().then().with(containsString("Hello")).build());
  }

  @Test
  public void matcherForStringWorksFine2() {
    assertThat("Hello, world", fluent().string().substring(2).toUpperCase().then().with(containsString("Hello")).verify());
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
