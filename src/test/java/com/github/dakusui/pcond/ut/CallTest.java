package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Function;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.*;
import static com.github.dakusui.pcond.functions.Predicates.lessThan;
import static com.github.dakusui.pcond.functions.Predicates.transform;
import static org.junit.Assert.assertThat;

public class CallTest extends TestBase {
  @Test
  public void test() {
    Function<Double, Double> cos = call(classMethod(Math.class, "cos", 0));
    System.out.println(cos.apply(Math.PI / 2));
    assertThat(1.0, CoreMatchers.equalTo(cos.apply(Math.PI / 2)));
  }

  @Test
  public void test2() {
    Function<String, Integer> length = call(instanceMethod(parameter(), "length"));
    System.out.println(length.apply("hello"));

    assertThat(5, CoreMatchers.equalTo(length.apply("hello")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test3() {
    String var = "hello, world";

    Function<String, Integer> length = call(instanceMethod(parameter(), "length"));
    requireArgument(var, transform(length).castAndCheck(lessThan(5)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test4() {
    String var = "hello, world";

    requireArgument(var,
        transform(
            chain("substring", 7)
                .andThen(chain("toUpperCase")))
            .check(chainp("startsWith", "WILD")));
  }

  @Test(expected = InternalException.class)
  public void methodNotFound() {
    try {
      requireArgument("hello", chainp("undefined", "H"));
    } catch (InternalException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = InternalException.class)
  public void methodIncompatible() {
    try {
      requireArgument("hello", chainp("startsWith", 123));
    } catch (
        InternalException e) {
      e.printStackTrace();
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
      requireArgument(new Ambiguous(), chainp("method", "hello", "world"));
    } catch (InternalException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = InternalException.class)
  public void methodAmbiguous_primitiveBoxed() {
    try {
      requireArgument(new Ambiguous2(), chainp("method", 1));
    } catch (InternalException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void narrowerMethodIsChosen() {
    requireArgument(new Narrower(), chainp("method", "Hello"));
  }

  @Test(expected = InternalException.class)
  public void callPrimitiveParameterMethodWithNull() {
    requireArgument(new Primitive(), chainp("method", new Object[]{null}));
  }

  public static class Ambiguous {
    public boolean method(String s, Object o) {
      return true;
    }

    public boolean method(Object o, String s) {
      return true;
    }
  }

  public static class Ambiguous2 {
    public boolean method(int i) {
      return true;
    }

    public boolean method(Integer i) {
      return true;
    }
  }

  public static class AcceptsNull {
    public boolean method(String v, String w) {
      return true;
    }
  }

  public static class ReturnsNull {
    public Object method(String v) {
      return null;
    }
  }

  public static class Narrower {
    public boolean method(String s) {
      return true;
    }

    public boolean method(Object o) {
      return false;
    }
  }

  public static class Primitive {
    public boolean method(int i) {
      return false;
    }
  }
}