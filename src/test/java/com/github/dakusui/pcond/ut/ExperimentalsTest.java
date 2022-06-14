package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.pcond.forms.Experimentals;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.provider.PreconditionViolationException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.Requires.require;
import static com.github.dakusui.pcond.forms.Experimentals.*;
import static com.github.dakusui.pcond.forms.Functions.*;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.predicate;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.pcond.ut.ExperimentalsTest.Utils.areEqual;
import static com.github.dakusui.pcond.ut.ExperimentalsTest.Utils.stringEndsWith;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class ExperimentalsTest extends TestBase {
  /**
   * Building a nested loop with the {@code pcond} library.
   * <p>
   * You can build a check using a multi-parameter static method which returns a boolean value.
   * In this example, {@link TargetMethodHolder#stringEndsWith(String, String)} is the method.
   * It is turned into a curried function in {@link Utils#stringEndsWith()} and then passed to {@link Experimentals#toContextPredicate(CurriedFunction, int...)}.
   * The method {@code Experimentals#test(CurriedFunction, int...)} converts a curried function whose final returned value is a boolean into a predicate of a {@link Context}.
   * A {@code Context} may have one or more values at once and those values are indexed.
   */
  @Test
  public void hello() {
    require(
        asList("hello", "world"),
        transform(stream().andThen(nest(asList("1", "2", "o")))).check(anyMatch(toContextPredicate(stringEndsWith()))));
  }

  @Test(expected = ComparisonFailure.class)
  public void helloError() {
    TestAssertions.assertThat(
        singletonList("hello"),
        transform(stream().andThen(nest(singletonList("o"))))
            .check(noneMatch(toContextPredicate(stringEndsWith()))));
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
com.github.dakusui.pcond.provider.PreconditionViolationException: value:["Hi","hello","world"] violated precondition:value stream->nest["1","2","o"] noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])]
["Hi","hello","world"]          -> =>                                                                  ->     false
                                     stream                                                            ->   ReferencePipeline$Head@1888ff2c
ReferencePipeline$Head@1888ff2c ->   nest["1","2","o"]                                                 ->   ReferencePipeline$7@6adca536
ReferencePipeline$7@6adca536    ->   noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])] ->   false
context:[hello, o]              ->     contextPredicate(stringEndsWith(String)(String)[0, 1])          -> true
	at com.github.dakusui.pcond.provider.AssertionProviderBase.lambda$exceptionComposerForPrecondition$0(AssertionProviderBase.java:83)
       */
      /* AFTER
com.github.dakusui.pcond.provider.PreconditionViolationException: value:["Hi","hello","world"] violated precondition:value stream->nest["1","2","o"] noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])]
["Hi","hello","world"]       -> =>                                                                  ->     false
                                  stream->nest["1","2","o"]                                         ->   ReferencePipeline$7@6c3708b3
ReferencePipeline$7@6c3708b3 ->   noneMatch[contextPredicate(stringEndsWith(String)(String)[0, 1])] ->   false
context:[hello, o]           ->     contextPredicate(stringEndsWith(String)(String)[0, 1])          -> true
	at com.github.dakusui.pcond.provider.AssertionProviderBase.lambda$exceptionComposerForPrecondition$0(AssertionProviderBase.java:83)
       */
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 6),
          allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("stringEndsWith(String)(String)[0, 1]"),
              CoreMatchers.containsString("true")
          ));
      throw e;
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
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 6),
          allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("true")
          ));
      assertThat(
          lineAt(e.getMessage(), 7),
          CoreMatchers.containsString("transform"));
      assertThat(
          lineAt(e.getMessage(), 8),
          allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("5")
          ));
      assertThat(
          lineAt(e.getMessage(), 9),
          allOf(
              CoreMatchers.containsString("5"),
              CoreMatchers.containsString("check")
          ));
      assertThat(
          lineAt(e.getMessage(), 10),
          allOf(
              CoreMatchers.containsString(">[3]"),
              CoreMatchers.containsString("true")
          ));
      throw e;
    }
  }

  @Test(expected = NullPointerException.class)
  public void hello_b_e3() {
    try {
      require(
          asList(null, "Hi", "hello", "world", null),
          transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(
              toContextPredicate(transform(Functions.length()).check(gt(3))))));
    } catch (InternalException e) {
      e.printStackTrace(System.out);
      assertThat(
          lineAt(e.getMessage(), 6),
          allOf(
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("NullPointerException")
          ));
      assertThat(
          lineAt(e.getMessage(), 7),
          CoreMatchers.containsString("transform"));
      assertThat(
          lineAt(e.getMessage(), 8),
          allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("NullPointerException")
          ));
      throw wrapIfNecessary(e.getCause());
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
  public void toContextPredicateTest() {
    assertFalse(toContextPredicate(isNotNull()).test(Context.from(null)));
    assertTrue(toContextPredicate(isNotNull()).test(Context.from(new Object())));
  }

  public static class IntentionalError extends Error {
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
  public void hello_d_2e() {
    try {
      require(
          "hello",
          transform(streamOf().andThen(toContextStream())).check(anyMatch(toContextPredicate(isNull()))));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 1),
          allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("transform")));
      assertThat(
          lineAt(e.getMessage(), 2),
          CoreMatchers.containsString("streamOf"));
      assertThat(
          lineAt(e.getMessage(), 3),
          CoreMatchers.containsString("toContextStream"));
      assertThat(
          lineAt(e.getMessage(), 5),
          allOf(
              CoreMatchers.containsString("anyMatch"),
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("isNull"),
              CoreMatchers.containsString("0"),
              CoreMatchers.containsString("false")));
      throw e;
    }
  }

  @Test
  public void hello_d_2() {
    require(
        "hello",
        transform(toContext()).check(toContextPredicate(isNotNull())));
  }

  @Test(expected = PreconditionViolationException.class)
  public void hello_d_3e() {
    try {
      require(
          "hello",
          transform(toContext()).check(toContextPredicate(isNull())));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 2),
          allOf(
              CoreMatchers.containsString("hello"),
              CoreMatchers.containsString("toContext"),
              CoreMatchers.containsString("context:[hello]")
          ));

      assertThat(
          lineAt(e.getMessage(), 3),
          allOf(
              CoreMatchers.containsString("context:[hello]"),
              CoreMatchers.containsString("check")
          ));
      assertThat(
          lineAt(e.getMessage(), 4),
          allOf(
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
  public void hello_e() {
    try {
      require(
          asList("hello", "world"),
          transform(stream().andThen(nest(asList("1", "2", "o")))).check(allMatch(toContextPredicate(stringEndsWith()))));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 1),
          allOf(
              CoreMatchers.containsString("\"hello\",\"world\""),
              CoreMatchers.containsString("transform")));
      assertThat(
          lineAt(e.getMessage(), 2),
          CoreMatchers.containsString("stream"));
      assertThat(
          lineAt(e.getMessage(), 3),
          allOf(
              CoreMatchers.containsString("nest"),
              CoreMatchers.containsString("\"1\",\"2\",\"o\"")));
      assertThat(
          lineAt(e.getMessage(), 5),
          allOf(
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

  @Test
  public void parameterizedPredicateTest() {
    Predicate<String> p = Experimentals.<String>parameterizedPredicate("containsStringIgnoreCase")
        .factory(args -> v -> v.toUpperCase().contains(args.get(0).toString().toUpperCase()))
        .create("hello");
    assertTrue(p.test("hello!"));
    assertTrue(p.test("Hello!"));
    assertFalse(p.test("World!"));
    assertEquals("containsStringIgnoreCase[hello]", p.toString());
  }

  @Test
  public void parameterizedPredicate_() {

    Predicate<String> p = Experimentals.<String>parameterizedPredicate("containsStringIgnoreCase")
        .factory((args) -> predicate(() -> "toUpperCase().contains(" + args.get(0) + ")", (String v) -> v.toUpperCase().contains(args.get(0).toString().toUpperCase())))
        .create("hello");
    System.out.println("p:<" + p + ">");
    assertTrue(p.test("hello!"));
    assertTrue(p.test("Hello!"));
    assertFalse(p.test("World!"));
    assertEquals("containsStringIgnoreCase[hello]", p.toString());

  }

  @Test
  public void parameterizedFunctionTest() {
    Function<Object[], Object> f = Experimentals.<Object[], Object>parameterizedFunction("arrayElementAt")
        .factory(args -> v -> v[(int) args.get(0)])
        .create(1);
    assertEquals("HELLO1", f.apply(new Object[] { 0, "HELLO1" }));
    assertEquals("HELLO2", f.apply(new Object[] { "hello", "HELLO2" }));
    assertEquals("arrayElementAt[1]", f.toString());
  }

  @Test
  public void usageExample() {
    Function<List<Object>, Function<String, String>> functionFactory = pathToUriFunctionFactory();
    Function<String, String> pathToUriOnLocalHost = functionFactory.apply(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost);
    System.out.println(pathToUriOnLocalHost.apply("path/to/resource"));
    System.out.println(pathToUriOnLocalHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnRemoteHost = functionFactory.apply(asList("https", "example.com", 8443));
    System.out.println(pathToUriOnRemoteHost);
    System.out.println(pathToUriOnRemoteHost.apply("path/to/resource"));
    System.out.println(pathToUriOnRemoteHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnLocalHost_2 = functionFactory.apply(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnLocalHost_2.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnLocalHost_2));

    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnRemoteHost.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnRemoteHost));
  }

  private static Function<List<Object>, Function<String, String>>
  pathToUriFunctionFactory() {
    return v -> PrintableFunctionFactory.create(
        (List<Object> args) -> () -> "buildUri" + args, (List<Object> args) -> (String path) -> String.format("%s://%s:%s/%s", args.get(0), args.get(1), args.get(2), path), v, ExperimentalsTest.class
    );
  }


  public enum Utils {
    ;

    public static CurriedFunction<Object, Object> stringEndsWith() {
      return curry(TargetMethodHolder.class, "stringEndsWith", String.class, String.class);
    }

    public static CurriedFunction<Object, Object> areEqual() {
      return curry(TargetMethodHolder.class, "areEqual", Object.class, Object.class);
    }
  }

  public enum TargetMethodHolder {
    ;

    @SuppressWarnings("unused") // Called through reflection
    public static boolean stringEndsWith(String s, String suffix) {
      return s.endsWith(suffix);
    }

    @SuppressWarnings("unused") // Called through reflection
    public static boolean areEqual(Object object, Object another) {
      return Objects.equals(object, another);
    }
  }
}
