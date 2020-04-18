package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.ExtraFunctions;
import com.github.dakusui.pcond.functions.PrintableFunction;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.functions.currying.CurriedFunction;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.provider.PreconditionViolationException;
import com.github.dakusui.pcond.ut.testdata.IntentionalException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.Preconditions.require;
import static com.github.dakusui.pcond.functions.ExtraFunctions.*;
import static com.github.dakusui.pcond.functions.Functions.curry;
import static com.github.dakusui.pcond.functions.Functions.stream;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.junit.Assert.*;

public class CurryingTest extends TestBase {
  @Test
  public void givenCurriedFunction$whenApplyExpectedTimes$thenExpectedResultReturned() {
    CurriedFunction<Object, Object> curried = Utils.example();
    curried = curried.applyNext(1);
    String actual = curried.applyLast(2);
    assertEquals("1+2=3", actual);
  }

  @Test
  public void givenCurriedFunction$whenToStringOnOngoing$thenExpectedResultReturned() {
    CurriedFunction<Object, Object> curried = Utils.example();
    curried = curried.applyNext(1);
    String actual = curried.toString();
    assertEquals("example(int:1)(int)", actual);
  }

  @Test(expected = NoSuchElementException.class)
  public void givenCurriedFunction$whenApplyNextMoreThanExpected$thenNoSuchElementIsThrown() {
    CurriedFunction<Object, Object> curried = Utils.example();
    curried = curried.applyNext(1);
    curried.applyNext(2);
  }

  @Test(expected = IllegalStateException.class)
  public void givenCurriedFunction$whenApplyLastBeforeLast$thenIllegalStateIsThrown() {
    CurriedFunction<Object, Object> curried = Utils.example();
    Object actual = curried.applyLast(1);
    System.out.println(actual);
  }

  @Test(expected = IllegalArgumentException.class)
  public void givenCurriedFunction$whenApplyWithInvalidArg$thenThrown() {
    CurriedFunction<Object, Object> curried = Utils.example();
    try {
      curried = curried.applyNext("InvalidArgString").applyLast("Detail:InvalidArgString");
      System.out.println(curried);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              CoreMatchers.containsString("Given argument"),
              CoreMatchers.containsString("InvalidArgString"),
              CoreMatchers.containsString(String.class.getName()),
              CoreMatchers.containsString("int")
          ));
      throw e;
    }
  }

  @Test(expected = IntentionalException.class)
  public void givenExceptionThrowingFunction$whenApplythenThrown() throws Throwable {
    CurriedFunction<Object, Object> curried = Utils.exceptionThrowingMethod();
    try {
      String actual = curried.applyNext("Hello").applyLast("World");
      System.out.println(actual);
    } catch (InternalException e) {
      assertThat(
          e.getMessage(),
          allOf(
              CoreMatchers.containsString(TestMethodHolder.class.getName()),
              CoreMatchers.containsString("exceptionThrowingMethod(String,String)")
          ));
      throw e.getCause();
    }
  }

  @SuppressWarnings("UnnecessaryCallToStringValueOf")
  @Test
  public void test3() {
    CurriedFunction<Object, Object> curried = Utils.example();
    System.out.println(Objects.toString(curried.applyNext((short) 2).applyLast(3)));
  }

  @Test
  public void givenStringToCurriedFuncWithIntParam$whenIsValidArg$thenFalse() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertFalse(curried.isValidArg("Hello"));
  }

  @Test
  public void given_intToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg(1));
  }

  @Test
  public void given_shortToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg((short) 1));
  }

  @Test
  public void given_byteToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg((byte) 1));
  }


  @Test
  public void given_longToCurriedFuncWithIntParam$whenIsValidArg$thenFalse() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertFalse(curried.isValidArg(1L));
  }

  @SuppressWarnings("UnnecessaryBoxing")
  @Test
  public void given_IntegerToCurriedFuncWithIntParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertTrue(curried.isValidArg(new Integer(0)));
  }

  @Test
  public void given_nullToCurriedFuncWithIntParam$whenIsValidArg$thenFalse() {
    CurriedFunction<Object, Object> curried = Utils.example();
    assertFalse(curried.isValidArg(null));
  }

  @Test
  public void given_nullToCurriedFuncWithStringParam$whenIsValidArg$thenTrue() {
    CurriedFunction<Object, Object> curried = Utils.endsWith();
    assertTrue(curried.isValidArg(null));
  }

  @Test(expected = InternalException.class)
  public void test4_b() {
    try {
      curry(TestMethodHolder.class, "undefined", int.class, int.class);
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(), allOf(
              CoreMatchers.containsString("undefined(int,int)"),
              CoreMatchers.containsString("was not found"),
              CoreMatchers.containsString(CurryingTest.class.getName())
          ));
      throw e;
    }
  }

  @Test(expected = InternalException.class)
  public void undefinedMethod() {
    try {
      curry(TestMethodHolder.class, "undefined", int.class, int.class);
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(), allOf(
              CoreMatchers.containsString("undefined(int,int)"),
              CoreMatchers.containsString("was not found"),
              CoreMatchers.containsString(CurryingTest.class.getName())
          ));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonStaticMethod() {
    try {
      curry(TestMethodHolder.class, "nonStatic", int.class, int.class);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(), allOf(
              CoreMatchers.containsString("nonStatic(int,int)"),
              CoreMatchers.containsString("is not static"),
              CoreMatchers.containsString(CurryingTest.class.getName())
          ));
      throw e;
    }
  }

  @Test
  public void hello() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2", "o")))).then(anyMatch(test(Utils.endsWith(), 0, 1))));
  }

  @Test
  public void hello_c() {
    require(
        asList("hello", "world"),
        when(stream().andThen(toContextStream()).andThen(nest(asList("1", "2", "o")))).then(anyMatch(test(Utils.endsWith(), 0, 1))));
  }

  @Test(expected = PreconditionViolationException.class)
  public void hello_a() {
    try {
      require(
          asList("hello", "world"),
          when(stream().andThen(nest(asList("1", "2", "o")))).then(allMatch(test(Utils.endsWith()))));
    } catch (PreconditionViolationException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void hello_b() {
    require(
        asList("hello", "world"),
        when(stream().andThen(nest(asList("1", "2", "o")))).then(noneMatch(test(Utils.endsWith(), 1, 0))));
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
  public void usageExample() {
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


  public static class Utils {
    public static CurriedFunction<Object, Object> example() {
      return curry(TestMethodHolder.class, "example", int.class, int.class);
    }

    public static CurriedFunction<Object, Object> endsWith() {
      return curry(TestMethodHolder.class, "endsWith", String.class, String.class);
    }

    public static CurriedFunction<Object, Object> exceptionThrowingMethod() {
      return curry(TestMethodHolder.class, "exceptionThrowingMethod", String.class, String.class);
    }
  }

  public static class TestMethodHolder {
    @SuppressWarnings("unused") // Called through reflection
    public static String example(int i, int j) {
      return String.format("%s+%s=%s", i, j, i + j);
    }

    @SuppressWarnings("unused") // Called through reflection
    public static boolean endsWith(String s, String suffix) {
      return s.endsWith(suffix);
    }

    @SuppressWarnings("unused") // Called through reflection
    public boolean nonStatic(int i, int j) {
      return false;
    }

    @SuppressWarnings("unused") // Called through reflection
    public static boolean exceptionThrowingMethod(String message, String detail) {
      throw new IntentionalException(message + ":" + detail);
    }
  }
}
