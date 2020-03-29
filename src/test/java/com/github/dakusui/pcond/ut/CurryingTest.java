package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.ExtraFunctions;
import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.functions.currying.CurriedFunction;
import com.github.dakusui.pcond.provider.PreconditionViolationException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.Preconditions.require;
import static com.github.dakusui.pcond.functions.ExtraFunctions.nest;
import static com.github.dakusui.pcond.functions.ExtraFunctions.test;
import static com.github.dakusui.pcond.functions.Functions.curry;
import static com.github.dakusui.pcond.functions.Functions.stream;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CurryingTest extends TestBase {
  @SuppressWarnings("unchecked")
  @Test
  public void test1() {
    CurriedFunction<Object, Object> curried = curry(CurryingTest.class, "example", int.class, int.class);
    System.out.println(curried);
    Function<Object, Object> partiallyApplied = (Function<Object, Object>) curried.apply(1);
    Object actual = partiallyApplied.apply(2);
    System.out.println(curried);
    System.out.println(partiallyApplied);
    assertEquals("1+2=3", actual.toString());
  }

  @Test
  public void hello() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2", "o")))).then(anyMatch(test(endsWith(), 0, 1))));
  }

  @Test(expected = PreconditionViolationException.class)
  public void hello_a() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2", "o")))).then(allMatch(test(endsWith()))));
  }

  @Test
  public void hello_b() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2", "o")))).then(noneMatch(test(endsWith(), 1, 0))));
  }

  @Test
  public void hello2() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2")))).then(alwaysTrue()));
  }

  @Test
  public void hello3() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2")))).then(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello3_a() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).then(alwaysTrue()));
  }

  @Test
  public void hello3_b() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).then(anyMatch(alwaysTrue())));
  }

  @Test
  public void hello4() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2"))).andThen(nest(asList("A", "B")))).then(anyMatch(new Predicate<ExtraFunctions.Context>() {
          @Override
          public boolean test(ExtraFunctions.Context context) {
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
        when(stream().andThen(nest(asList("1", "2")))).then(anyMatch(new Predicate<ExtraFunctions.Context>() {
          @Override
          public boolean test(ExtraFunctions.Context context) {
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
        when(stream().andThen(nest(asList("1", "2")))).then(allMatch(new Predicate<ExtraFunctions.Context>() {
          @Override
          public boolean test(ExtraFunctions.Context context) {
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
        when(stream().andThen(nest(asList("1", "2")))).then(anyMatch(new Predicate<ExtraFunctions.Context>() {
          @Override
          public boolean test(ExtraFunctions.Context context) {
            return context.valueAt(1).equals("1");
          }

          @Override
          public String toString() {
            return "context#valueAt(1) equals '1'";
          }
        })));
  }

  @Test
  public void example() {
    PrintableFunction.Factory<String, String, List<Object>> functionFactory = pathToUriFunctionFactory();
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

  private PrintableFunction.Factory<String, String, List<Object>> pathToUriFunctionFactory() {
    return Printables.functionFactory(
        (List<Object> args) -> "buildUri" + args,
        (List<Object> args) -> (String path) -> String.format("%s://%s:%s/%s", args.get(0), args.get(1), args.get(2), path));
  }


  public CurriedFunction<Object, Object> endsWith() {
    return curry(CurryingTest.class, "endsWith", String.class, String.class);
  }

  @Test
  public void test2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method m = CurryingTest.class.getMethod("example", int.class);
    System.out.println(m);
    Short s = 100;
    System.out.println(int.class.getSimpleName() + "value=" + m.invoke(null, s));
  }

  @SuppressWarnings("UnnecessaryCallToStringValueOf")
  @Test
  public void test3() {
    CurriedFunction<Object, Object> curried = curry(CurryingTest.class, "example", int.class, int.class);
    System.out.println(Objects.toString(curried.applyNext((short) 2).applyLast(3)));
  }

  @SuppressWarnings("unused") // Called through reflection
  public static String example(int i, int j) {
    return String.format("%s+%s=%s", i, j, i + j);
  }

  public static String example(int i) {
    return String.format("value=%s", i);
  }

  @SuppressWarnings("unused") // Called through reflection
  public static boolean endsWith(String s, String suffix) {
    return s.endsWith(suffix);
  }
}
