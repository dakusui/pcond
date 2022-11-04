package com.github.dakusui.pcond_2.ut.experimentals;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.forms.Experimentals;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.ut.IntentionalError;
import com.github.dakusui.pcond.validator.exceptions.PreconditionViolationException;
import com.github.dakusui.shared.ExperimentalsUtils;
import com.github.dakusui.shared.TargetMethodHolder;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Experimentals.*;
import static com.github.dakusui.pcond.forms.Functions.stream;
import static com.github.dakusui.pcond.forms.Functions.streamOf;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static com.github.dakusui.shared.ExperimentalsUtils.areEqual;
import static com.github.dakusui.shared.ExperimentalsUtils.stringEndsWith;
import static com.github.dakusui.valid8j.Requires.require;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;

public class DbCExperimentalsTest {
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
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o"))))
            .check(anyMatch(toContextPredicate(stringEndsWith()))));
  }

  @Test
  public void hello_a() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(anyMatch(toContextPredicate(stringEndsWith(), 0, 1))));
  }

  @Test
  public void hello_b() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(toContextPredicate(stringEndsWith(), 1, 0))));
  }

  @Test(expected = PreconditionViolationException.class)
  public void hello_b_e() {
    try {
      require(
          asList("Hi", "hello", "world"),
          transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(toContextPredicate(stringEndsWith(), 0, 1))));
    } catch (PreconditionViolationException e) {
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
      assertThat(
          lineAt(e.getMessage(), 4),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("stringEndsWith(String)(String)[0, 1]"),
              CoreMatchers.containsString("true")
          ));
      throw e;
    }
  }


  @Test(expected = NullPointerException.class)
  public void givenStreamContainingNull_whenRequireConditionResultingInNPE_thenInternalExceptionWithCorrectMessageAndNpeAsNestedException() {
    try {
      require(
          asList(null, "Hi", "hello", "world", null),
          transform(stream().andThen(nest(asList("1", "2", "o"))))
              .check(noneMatch(
                  toContextPredicate(transform(Functions.length()).check(gt(3))))));
    } catch (InternalException e) {
      e.printStackTrace(System.out);
      assertThat(
          lineAt(e.getMessage(), 3),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
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

  @Test(expected = PreconditionViolationException.class)
  public void hello_b_e2() {
    try {
      require(
          asList("Hi", "hello", "world", null),
          transform(stream().andThen(nest(asList("1", "2", "o"))))
              .check(
                  noneMatch(
                      toContextPredicate(transform(Functions.length()).check(gt(3))))
              ));
    } catch (PreconditionViolationException e) {
      e.printStackTrace(System.out);
      assertThat(
          lineAt(e.getMessage(), 4),
          CoreMatchers.allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("true")
          ));
      assertThat(
          lineAt(e.getMessage(), 5),
          CoreMatchers.containsString("transform"));
      assertThat(
          lineAt(e.getMessage(), 5),
          CoreMatchers.allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("5")
          ));
      assertThat(
          lineAt(e.getMessage(), 6),
          CoreMatchers.allOf(
              CoreMatchers.containsString("5"),
              CoreMatchers.containsString("check")
          ));
      assertThat(
          lineAt(e.getMessage(), 6),
          CoreMatchers.allOf(
              CoreMatchers.containsString(">[3]"),
              CoreMatchers.containsString("true")
          ));
      throw e;
    }
  }

  @Test(expected = IntentionalError.class)
  public void hello_b_e4() {
    require(
        asList(null, "Hi", "hello", "world", null),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(
            toContextPredicate(transform((Function<String, Integer>) s -> {
              throw new IntentionalError();
            }).check(gt(3))))));
  }

  @Test
  public void hello_c() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(toContextStream()).andThen(nest(asList("1", "2", "o")))).check(anyMatch(toContextPredicate(stringEndsWith(), 0, 1))));
  }

  @Test
  public void hello_d_1() {
    require(
        "hello",
        transform(streamOf().andThen(nest(asList("Hello", "HELLO", "hello")))).check(anyMatch(toContextPredicate(areEqual()))));
  }

  @Test(expected = PreconditionViolationException.class)
  public void givenStreamOfSingleString$hello$_whenRequireNullIsFound_thenPreconditionViolationWithCorrectMessageIsThrown() {
    try {
      require(
          "hello",
          transform(streamOf().andThen(toContextStream())).check(anyMatch(toContextPredicate(isNull()))));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      int i = 0;
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("transform")));
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.containsString("streamOf"));
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.containsString("toContextStream"));
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("anyMatch"),
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void givenStreamOfSingleString$hello$_whenRequireNonNullIsFound_thenPassing() {
    require(
        "hello",
        transform(toContext()).check(toContextPredicate(isNotNull())));
  }

  @Test(expected = PreconditionViolationException.class)
  public void givenString$hello$_whenTransformToContextAndCheckContextValueIsNull_thenPreconditionViolationWithCorrectMessageThrown() {
    try {
      require(
          "hello",
          transform(toContext()).check(toContextPredicate(isNull())));
    } catch (PreconditionViolationException e) {
      e.printStackTrace(System.out);
      int i = 0;
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("toContext"),
              CoreMatchers.containsString("context:[hello]")
          ));

      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("context:[hello]"),
              CoreMatchers.containsString("check")
          ));
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

  @Test(expected = PreconditionViolationException.class)
  public void given$hello$_$world$_whenRequireNestedStreamImpossibleConditions_thenPreconditionViolationExceptionWithCorrectMessage() {
    try {
      require(
          asList("hello", "world"),
          transform(stream().andThen(nest(asList("1", "2", "o")))).check(allMatch(toContextPredicate(stringEndsWith()))));
    } catch (PreconditionViolationException e) {
      e.printStackTrace(System.out);
      int i = 0;
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("\"hello\",\"world\""),
              CoreMatchers.containsString("transform")));
      assertThat(
          lineAt(e.getMessage(), i),
          CoreMatchers.containsString("stream"));
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("nest"),
              CoreMatchers.containsString("\"1\",\"2\",\"o\"")));
      assertThat(
          lineAt(e.getMessage(), ++i),
          CoreMatchers.allOf(
              CoreMatchers.containsString("allMatch"),
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("stringEndsWith"),
              CoreMatchers.containsString("String"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void hello2() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(alwaysTrue()));
  }

  @Test
  public void hello3() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello3_a() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(alwaysTrue()));
  }

  @Test
  public void hello3_b() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello4() {
    require(
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
    require(
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

  @Test(expected = PreconditionViolationException.class)
  public void hello5() {
    require(
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
    require(
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
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("msg-1", "msg-2")))).check(anyMatch(toContextPredicate(equalTo("msg-2"), 1))));
  }

  @Test(expected = PreconditionViolationException.class)
  public void nestedLoop_fail() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("msg-1", "msg-2")))).check(anyMatch(toContextPredicate(equalTo("msg-3"), 1))));
  }

}
