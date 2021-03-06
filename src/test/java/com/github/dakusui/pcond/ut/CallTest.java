package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.internals.MethodInvocationException;
import com.github.dakusui.pcond.internals.MethodNotFound;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.util.Objects;
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
    assertThat(cos.apply(Math.PI / 2), equalTo(1.0));
  }

  @Test
  public void instanceMethodCanBeCalled() {
    Function<String, Integer> length = call(instanceMethod(parameter(), "length"));
    System.out.println(length.apply("hello"));

    assertThat(length.apply("hello"), equalTo(5));
    assertThat(length.toString(), equalTo("p[0].length()"));
  }

  @Test
  public void instanceMethodCanBeCreatedOnSpecifiedObject() {
    Function<String, Integer> contains = call(instanceMethod("Hello, world", "contains", parameter()));
    System.out.println(contains);
    System.out.println(contains.apply("hello"));

    assertThat(contains.apply("Hello"), equalTo(true));
    assertThat(contains.apply("HELLO"), equalTo(false));
    assertThat(contains.toString(), equalTo("<Hello, world>.contains(p[0])"));
  }

  @Test
  public void functionCanBeCreatedFromInstanceMethod() {
    String var = "hello, world";

    Function<String, Integer> length = call(instanceMethod(parameter(), "length"));

    assertThat(length.apply(var), is(var.length()));
  }

  @Test
  public void functionsCanBeChained() {
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
              containsString("were found more than one"),
              containsString("Ambiguous.method(java.lang.String,java.lang.Object)"),
              containsString("Ambiguous.method(java.lang.Object,java.lang.String)")
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

  @Test
  public void covariantOverridingMethodCanBeInvokedCorrectly() {
    Object r = chain("method", "Hello").apply(new ExtendsBase());

    assertThat(
        Objects.toString(r),
        allOf(containsString("Hello"), containsString("extendsBase")));
  }

  @Test(expected = MethodNotFound.class)
  public void parameterUnmatchedWhileNameMatching() {
    try {
      chain("method", 123).apply(new Base());
    } catch (MethodNotFound e) {
      assertThat(e.getMessage(), allOf(
          containsString("method[123]"),
          containsString("not found"),
          containsString(Base.class.getCanonicalName())
      ));
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

  @SuppressWarnings("unused")
  public static class Base {
    public Object method(String s) {
      return "base: " + s;
    }
  }

  @SuppressWarnings("unused")
  public static class ExtendsBase extends Base {
    public String method(String s) {
      return "extendsBase: " + s;
    }
  }
}