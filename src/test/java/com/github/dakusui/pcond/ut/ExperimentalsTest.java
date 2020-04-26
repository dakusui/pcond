package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.functions.Experimentals;
import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.functions.preds.BaseFuncUtils;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.provider.PreconditionViolationException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.Preconditions.require;
import static com.github.dakusui.pcond.functions.Experimentals.*;
import static com.github.dakusui.pcond.functions.Functions.*;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.internals.InternalUtils.wrapIfNecessary;
import static com.github.dakusui.pcond.ut.ExperimentalsTest.Utils.areEqual;
import static com.github.dakusui.pcond.ut.ExperimentalsTest.Utils.stringEndsWith;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.assertThat;

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
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 5),
          allOf(
              CoreMatchers.containsString("context:[hello, o]"),
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
          transform(stream().andThen(nest(asList("1", "2", "o")))).check(noneMatch(
              toContextPredicate(transform(Functions.length()).check(gt(3))))));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 5),
          allOf(
              CoreMatchers.containsString("context:[hello, 1]"),
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("true")
          ));
      assertThat(
          lineAt(e.getMessage(), 6),
          CoreMatchers.containsString("hello"));
      assertThat(
          lineAt(e.getMessage(), 7),
          allOf(
              CoreMatchers.containsString("length"),
              CoreMatchers.containsString("5")
          ));
      assertThat(
          lineAt(e.getMessage(), 8),
          allOf(
              CoreMatchers.containsString("5"),
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
      e.printStackTrace();
      assertThat(
          lineAt(e.getMessage(), 5),
          allOf(
              CoreMatchers.containsString("context:[null, 1]"),
              CoreMatchers.containsString("contextPredicate"),
              CoreMatchers.containsString("length >[3]"),
              CoreMatchers.containsString(",0"),
              CoreMatchers.containsString("NullPointerException")
          ));
      assertThat(
          lineAt(e.getMessage(), 6),
          CoreMatchers.containsString("null"));
      assertThat(
          lineAt(e.getMessage(), 7),
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
          CoreMatchers.containsString("hello"));
      assertThat(
          lineAt(e.getMessage(), 2),
          CoreMatchers.containsString("streamOf"));
      assertThat(
          lineAt(e.getMessage(), 3),
          CoreMatchers.containsString("toContextStream"));
      assertThat(
          lineAt(e.getMessage(), 4),
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
              CoreMatchers.containsString("=>")));
      assertThat(
          lineAt(e.getMessage(), 2),
          CoreMatchers.containsString("stream"));
      assertThat(
          lineAt(e.getMessage(), 3),
          allOf(
              CoreMatchers.containsString("nest"),
              CoreMatchers.containsString("\"1\",\"2\",\"o\"")));
      assertThat(
          lineAt(e.getMessage(), 4),
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
        transform(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).check(anyMatch(new Predicate<Experimentals.Context>() {
          @Override
          public boolean test(Experimentals.Context context) {
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
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(new Predicate<Experimentals.Context>() {
          @Override
          public boolean test(Experimentals.Context context) {
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
        transform(stream().andThen(nest(asList("1", "2")))).check(allMatch(new Predicate<Experimentals.Context>() {
          @Override
          public boolean test(Experimentals.Context context) {
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
        transform(stream().andThen(nest(asList("1", "2")))).check(anyMatch(new Predicate<Experimentals.Context>() {
          @Override
          public boolean test(Experimentals.Context context) {
            return context.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void usageExample() {
    BaseFuncUtils.Factory<String, String, List<Object>> functionFactory = pathToUriFunctionFactory();
    Function<String, String> pathToUriOnLocalHost = functionFactory.create(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost);
    System.out.println(pathToUriOnLocalHost.apply("path/to/resource"));
    System.out.println(pathToUriOnLocalHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnRemoteHost = functionFactory.create(asList("https", "example.com", 8443));
    System.out.println(pathToUriOnRemoteHost);
    System.out.println(pathToUriOnRemoteHost.apply("path/to/resource"));
    System.out.println(pathToUriOnRemoteHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnLocalHost_2 = functionFactory.create(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnLocalHost_2.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnLocalHost_2));

    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnRemoteHost.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnRemoteHost));
  }

  private static BaseFuncUtils.Factory<String, String, List<Object>> pathToUriFunctionFactory() {
    return Printables.functionFactory(
        (List<Object> args) -> "buildUri" + args,
        (List<Object> args) -> (String path) -> String.format("%s://%s:%s/%s", args.get(0), args.get(1), args.get(2), path));
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
