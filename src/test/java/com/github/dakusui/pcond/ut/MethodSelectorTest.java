package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.core.refl.MethodSelector;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static java.util.Arrays.asList;

public class MethodSelectorTest {
  @Test(expected = IllegalArgumentException.class)
  public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method[] methods = new Method[] {
        Methods.class.getMethod("method", String.class),
        Methods.class.getMethod("method", String.class, String.class)
    };

    List<Method> results = new MethodSelector.PreferNarrower()
        .select(asList(methods), new Object[] { "hello" });
    System.out.println(results);

    System.out.println(results.get(0).invoke(new Methods(), "hello"));
  }

  @Test
  public void test2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method[] methods = new Method[] {
        Methods.class.getMethod("method", String.class),
        Methods.class.getMethod("method", String.class)
    };

    List<Method> results = new MethodSelector.PreferNarrower()
        .select(asList(methods), new Object[] { "hello" });
    System.out.println(results);

    System.out.println(results.get(0).invoke(new Methods(), "hello"));
  }

  @Test
  public void test3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method[] methods = new Method[] {
        Methods.class.getMethod("method", String.class),
        Methods.class.getMethod("method", String.class)
    };

    List<Method> results = new MethodSelector.Default()
        .select(asList(methods), new Object[] { null });
    System.out.println(results);

    System.out.println(results.get(0).invoke(new Methods(), new Object[] { null }));
  }

  public static class Methods {
    public String method(String a) {
      return String.format("method(String: %s)", a);
    }

    public String method(String s, String t) {
      return String.format("method(String:%s,String:%s)", s, t);
    }
  }
}
