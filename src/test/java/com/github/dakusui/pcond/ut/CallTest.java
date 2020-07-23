package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.internals.MethodAccessException;
import com.github.dakusui.pcond.internals.MethodInvocationException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Function;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class CallTest extends TestBase {
  @Test
  public void classMethodCaBeCalled() {
    Function<Double, Double> cos = call(classMethod(Math.class, "cos", 0));
    System.out.println(cos.apply(Math.PI / 2));
    assertThat(1.0, CoreMatchers.equalTo(cos.apply(Math.PI / 2)));
  }

  @Test
  public void instanceMethodCanBeCalled() {
    Function<String, Integer> length = call(instanceMethod(parameter(), "length"));
    System.out.println(length.apply("hello"));

    assertThat(5, CoreMatchers.equalTo(length.apply("hello")));
  }

  @Test
  public void functionCanBeCreatedFromInstanceMethod() {
    String var = "hello, world";

    Function<String, Integer> length = call(instanceMethod(parameter(), "length"));

    assertThat(length.apply(var), is(var.length()));
  }

  @Test
  public void test4() {
    String var = "hello, world";

    Function<String, String> chained = Functions.<String, String>chain("substring", 7).andThen(chain("toUpperCase"));

    assertThat(chained.apply(var), is("WORLD"));
  }

  @Test(expected = InternalException.class)
  public void methodNotFound() {
    try {
      requireArgument("hello", chainp("undefined", "H"));
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              containsString("undefined[H]"),
              containsString("was not found"),
              containsString("default"),
              containsString("preferNarrower"),
              containsString("preferExact")
          ));
      throw e;
    }
  }

  @Test(expected = InternalException.class)
  public void methodIncompatible() {
    try {
      chainp("startsWith", parameter()).test(123);
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              containsString("startsWith[123]"),
              containsString("was not found")
          ));
      throw e;
    }
  }

  @Test
  public void nullCanMatch() {
    requireArgument(new AcceptsNull(), chainp("method", "hello", null));
  }

  @Test(expected = InternalException.class)
  public void nullReturningFunctionAsPredicate() {
    requireArgument(new ReturnsNull(), chainp("method", "hello"));
  }

  @Test(expected = InternalException.class)
  public void methodAmbiguous() {
    try {
      System.out.println(chainp("method", "hello", "world").test(new Ambiguous()));
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              containsString("method[hello, world]"),
              containsString("were found more than one")
          ));
      throw e;
    }
  }

  @Test(expected = InternalException.class)
  public void methodAmbiguous_primitiveBoxed() {
    try {
      requireArgument(new Ambiguous2(), chainp("method", 1));
    } catch (InternalException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              containsString("method[1]"),
              containsString("were found more than one")
          ));
      throw e;
    }
  }

  @Test
  public void narrowerMethodIsChosen() {
    assertThat(
        chainp("narrowerMethod", "Hello").test(new Narrower()),
        is(true));
  }

  @Test(expected = InternalException.class)
  public void cannotCallPrimitiveParameterMethodWithNull() {
    chainp("method", new Object[] { null }).test(new Primitive());
  }

  @Test(expected = MethodInvocationException.class)
  public void exceptionThrowingMethod() {
    try {
      chainp("throwException").test(new ExceptionThrowing());
    } catch (MethodInvocationException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage(),
          allOf(
              containsString("Method invocation"),
              containsString("throwException"),
              containsString("was failed")));
      throw e;
    }
  }

  @SuppressWarnings("unused")
  public static class Ambiguous {
    public boolean method(String s, Object o) {
      return true;
    }

    public boolean method(Object o, String s) {
      return true;
    }
  }

  @SuppressWarnings("unused")
  public static class Ambiguous2 {
    public boolean method(int i) {
      return true;
    }

    public boolean method(Integer i) {
      return true;
    }
  }

  @SuppressWarnings("unused")
  public static class AcceptsNull {
    public boolean method(String v, String w) {
      return true;
    }
  }

  @SuppressWarnings("unused")
  public static class ReturnsNull {
    public Object method(String v) {
      return null;
    }
  }

  @SuppressWarnings("unused")
  public static class Narrower {
    public boolean narrowerMethod(String s) {
      return true;
    }

    public boolean narrowerMethod(Object o) {
      return false;
    }
  }

  @SuppressWarnings("unused")
  public static class Primitive {
    public boolean method(int i) {
      return false;
    }
  }

  @SuppressWarnings("unused")
  public static class ExceptionThrowing {
    public boolean throwException() {
      throw new TestException();
    }

    public static class TestException extends RuntimeException {
    }
  }
}