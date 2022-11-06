package com.github.dakusui.pcond.experimentals;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.forms.Experimentals;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.ut.IntentionalError;
import com.github.dakusui.shared.ExperimentalsUtils;
import com.github.dakusui.shared.TargetMethodHolder;
import com.github.dakusui.shared.TestUtils;
import com.github.dakusui.shared.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Experimentals.*;
import static com.github.dakusui.pcond.forms.Functions.stream;
import static com.github.dakusui.pcond.forms.Functions.streamOf;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.shared.utils.TestUtils.lineAt;
import static com.github.dakusui.shared.ExperimentalsUtils.areEqual;
import static com.github.dakusui.shared.ExperimentalsUtils.stringEndsWith;
import static com.github.dakusui.shared.TestUtils.validate;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

public class DbCExperimentalsTest extends TestBase {
  /**
   * Building a nested loop with the {@code pcond} library.
   *
   * You can build a check using a multi-parameter static method which returns a boolean value.
   * In this example, {@link TargetMethodHolder#stringEndsWith(String, String)} is the method.
   * It is turned into a curried function in {@link ExperimentalsUtils#stringEndsWith()} and then passed to {@link Experimentals#toContextPredicate(CurriedFunction, int...)}.
   * The method {@code Experimentals#test(CurriedFunction, int...)} converts a curried function whose final returned value is a boolean into a predicate of a {@link Context}.
   * A {@code Context} may have one or more values at once and those values are indexed.
   */
  @Test
  public void hello() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o"))))
            .check(anyMatch(toContextPredicate(stringEndsWith()))));
  }

  @Test
  public void hello_a() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(anyMatch(toContextPredicate(stringEndsWith(), 0, 1))));
  }

  @Test
  public void hello_b() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(toContextPredicate(stringEndsWith(), 1, 0))));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void hello_b_e() {
    try {
      validate(
          asList("Hi", "hello", "world"),
          transform(stream()
              .andThen(nest(asList("1", "2", "o"))))
              .check(noneMatch(toContextPredicate(stringEndsWith(), 0, 1))));                // (1)
    } catch (TestUtils.IllegalValueException e) {
      /* BEFORE
com.github.dakusui.pcond.provider.exceptions.PreconditionViolationException: value:["Hi","hello","world"] violated precondition:value stream->nest["1","2","o"] noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])]
["Hi","hello","world"]          -> =>                                                                  ->     false
                                     stream                                                            ->   ReferencePipeline$Head@1888ff2c
ReferencePipeline$Head@1888ff2c ->   nest["1","2","o"]                                                 ->   ReferencePipeline$7@6adca536
ReferencePipeline$7@6adca536    ->   noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])] ->   false
context:[hello, o]              ->     contextPredicate(stringEndsWith(String)(String)[0, 1])          -> true
	at com.github.dakusui.pcond.provider.AssertionProviderBase.lambda$exceptionComposerForPrecondition$0(AssertionProviderBase.java:83)
       */
      /* AFTER
com.github.dakusui.pcond.provider.exceptions.PreconditionViolationException: value:["Hi","hello","world"] violated precondition:value stream->nest["1","2","o"] noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])]
["Hi","hello","world"]       -> =>                                                                  ->     false
                                  stream->nest["1","2","o"]                                         ->   ReferencePipeline$7@6c3708b3
ReferencePipeline$7@6c3708b3 ->   noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])] ->   false
context:[hello, o]           ->     contextPredicate(stringEndsWith(String)(String)[0, 1])          -> true
	at com.github.dakusui.pcond.provider.AssertionProviderBase.lambda$exceptionComposerForPrecondition$0(AssertionProviderBase.java:83)
       */
      e.printStackTrace(System.out);
      // expected (1)
      assertThat(
          lineAt(e.getMessage(), 5),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
              //              CoreMatchers.containsString("stringEndsWith(String)(String)[0, 1]"),
              CoreMatchers.containsString("false")
          ));
      // actual (1)
      assertThat(
          lineAt(e.getMessage(), 6),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
              //              CoreMatchers.containsString("stringEndsWith(String)(String)[0, 1]"),
              CoreMatchers.containsString("true")
          ));
      throw e;
    }
  }


  @Test(expected = NullPointerException.class)
  public void givenStreamContainingNull_whenRequireConditionResultingInNPE_thenInternalExceptionWithCorrectMessageAndNpeAsNestedException() {
    try {
      validate(
          asList(null, "Hi", "hello", "world", null),
          transform(stream().andThen(nest(asList("1", "2", "o"))))
              .check(noneMatch(
                  toContextPredicate(transform(Functions.length()).check(gt(3))))));
    } catch (InternalException e) {
      e.printStackTrace(System.out);
      assertThat(
          lineAt(e.getMessage(), 3),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contex..."),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("NullPointerException")
          ));
      assertThat(
          lineAt(e.getMessage(), 5),
          CoreMatchers.containsString("transform"));
      assertThat(
          lineAt(e.getMessage(), 5),
          CoreMatchers.allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("NullPointerException")
          ));
      throw wrapIfNecessary(e.getCause());
    }
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void hello_b_e2() {
    try {
      validate(
          asList("Hi", "hello", "world", null),
          transform(stream().andThen(nest(asList("1", "2", "o"))))
              .check(
                  noneMatch(
                      toContextPredicate(transform(Functions.length()).check(gt(3))))));
      //              |                  |         |                         |
      //              |                  |         |                         |
      //             (1)                (2)       (3)                       (4)

    } catch (TestUtils.IllegalValueException e) {
      e.printStackTrace(System.out);
      int i = 3;
      // expected (1)
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contex..."),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("true")
          ));
      // actual (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contex..."),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("false")
          ));
      ++i;
      ++i;
      // (2)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.containsString("transform"));
      // (3)
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("5")
          ));
      // expected (4)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("5"),
              CoreMatchers.containsString("check")
          ));
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString(">[3]"),
              CoreMatchers.containsString("false")
          ));
      // actual (4)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("5"),
              CoreMatchers.containsString("check")
          ));
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString(">[3]"),
              CoreMatchers.containsString("true")
          ));
      throw e;
    }
  }

  @Test(expected = IntentionalError.class)
  public void hello_b_e4() {
    validate(
        asList(null, "Hi", "hello", "world", null),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(
            toContextPredicate(transform((Function<String, Integer>) s -> {
              throw new IntentionalError();
            }).check(gt(3))))));
  }

  @Test
  public void hello_c() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(toContextStream()).andThen(nest(asList("1", "2", "o")))).check(anyMatch(toContextPredicate(stringEndsWith(), 0, 1))));
  }

  @Test
  public void hello_d_1() {
    validate(
        "hello",
        transform(streamOf().andThen(nest(asList("Hello", "HELLO", "hello")))).check(anyMatch(toContextPredicate(areEqual()))));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void givenStreamOfSingleString$hello$_whenRequireNullIsFound_thenPreconditionViolationWithCorrectMessageIsThrown() {
    try {
      validate(
          "hello",
          transform(streamOf()                                        // (1)
              .andThen(toContextStream()))                            // (2)
              .check(anyMatch(toContextPredicate(isNull()))));        // (3)
    } catch (TestUtils.IllegalValueException e) {
      e.printStackTrace();
      int i = 0;
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("transform")));
      // (1)
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.containsString("streamOf"));
      // (2)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.containsString("toContextStream"));
      // expected (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("anyMatch"),
              CoreMatchers.containsString("context..."),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("true")));
      // expected (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("anyMatch"),
              CoreMatchers.containsString("context..."),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void givenStreamOfSingleString$hello$_whenRequireNonNullIsFound_thenPassing() {
    validate(
        "hello",
        transform(toContext()).check(toContextPredicate(isNotNull())));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void givenString$hello$_whenTransformToContextAndCheckContextValueIsNull_thenPreconditionViolationWithCorrectMessageThrown() {
    try {
      validate(
          "hello",
          transform(toContext())                              // (1)
              .check(toContextPredicate(isNull())));          // (2) -1,2
    } catch (TestUtils.IllegalValueException e) {
      e.printStackTrace(System.out);
      int i = 0;
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("toContext"),
              CoreMatchers.containsString("context:[hello]")
          ));
      // expected (2) -1
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("context:[hello]"),
              CoreMatchers.containsString("check")
          ));
      // expected (2) -2
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("->"),
              CoreMatchers.containsString("true")
          ));
      // actual (2) -1
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("context:[hello]"),
              CoreMatchers.containsString("check")
          ));
      // actual (2) -2
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("->"),
              CoreMatchers.containsString("false")
          ));
      throw e;
    }
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void given$hello$_$world$_whenRequireNestedStreamImpossibleConditions_thenPreconditionViolationExceptionWithCorrectMessage() {
    try {
      validate(
          asList("hello", "world"),
          transform(stream()                                                        // (1)
              .andThen(nest(asList("1", "2", "o"))))                                // (2)
              .check(allMatch(toContextPredicate(stringEndsWith()))));              // (3)
    } catch (TestUtils.IllegalValueException e) {
      e.printStackTrace(System.out);
      int i = 0;
      // (1)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("\"hello\",\"world\""),
              CoreMatchers.containsString("transform")));
      // (1)
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.containsString("stream"));
      // (2)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("nest"),
              CoreMatchers.containsString("\"1\",\"2\",\"o\"")));
      // expected (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("allMatch"),
              CoreMatchers.containsString("context..."),
              CoreMatchers.containsString("String"),
              CoreMatchers.containsString("true")));
      // actual (3)
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("allMatch"),
              CoreMatchers.containsString("context..."),
              CoreMatchers.containsString("String"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void hello2() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(alwaysTrue()));
  }

  @Test
  public void hello3() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello3_a() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(alwaysTrue()));
  }

  @Test
  public void hello3_b() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello4() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(anyMatch(new Predicate<Context>() {
          @Override
          public boolean test(Context context) {
            return context.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void hello4_a() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(new Predicate<Context>() {
          @Override
          public boolean test(Context context) {
            return context.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void hello5() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(allMatch(new Predicate<Context>() {
          @Override
          public boolean test(Context context) {
            return context.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void hello6() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(new Predicate<Context>() {
          @Override
          public boolean test(Context context) {
            return context.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void nestedLoop_success() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("msg-1", "msg-2")))).check(anyMatch(toContextPredicate(equalTo("msg-2"), 1))));
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void nestedLoop_fail() {
    validate(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("msg-1", "msg-2")))).check(anyMatch(toContextPredicate(equalTo("msg-3"), 1))));
  }
}
