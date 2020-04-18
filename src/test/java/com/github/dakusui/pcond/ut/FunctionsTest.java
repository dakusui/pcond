package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.MultiParameterFunction;
import com.github.dakusui.pcond.functions.currying.CurryingUtils;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.github.dakusui.pcond.functions.Functions.stream;
import static com.github.dakusui.pcond.functions.Functions.stringify;
import static com.github.dakusui.pcond.internals.InternalUtils.getMethod;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class FunctionsTest {
  public static class ElementAtTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          200,
          Functions.elementAt(1).apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "at[123]",
          Functions.elementAt(123).toString());
    }

    @Test
    public void whenEqualityIsChecked$thenSameIsSameAndDifferentIsDifferent() {
      Function<List<?>, ?> target = Functions.elementAt(100);
      assertThat(
          target,
          allOf(
              is(Functions.elementAt(100)),
              is(target),
              not(is(new Object())),
              not(is(Functions.elementAt(101)))));
    }

    @Test
    public void whenHashCode$thenSameIsSameAndDifferentIsDifferent() {
      int target = Functions.elementAt(100).hashCode();
      assertThat(
          target,
          allOf(
              is(Functions.elementAt(100).hashCode()),
              not(is(Functions.elementAt(101).hashCode()))));
    }
  }

  public static class SizeTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          (Integer) 3,
          Functions.size().apply(asList(100, 200, 300))
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "size",
          Functions.size().toString()
      );
    }
  }

  public static class StreamTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          asList(100, 200, 300),
          stream().apply(asList(100, 200, 300)).collect(toList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "stream",
          stream().toString()
      );
    }
  }

  public static class StringifyTest extends TestBase {
    @Test
    public void whenApplied$thenLooksGood() {
      assertEquals(
          "[]",
          stringify().apply(Collections.emptyList())
      );
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(
          "stringify",
          stringify().toString()
      );
    }
  }

  public static class MultiParameterFunctionTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void lookUpWithInvalidArgument_duplicatedOrder() {
      try {
        greeting(0, 0);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        assertThat(
            e.getMessage(),
            allOf(
                containsString("Duplicated elements are found"),
                containsString("[0, 0]")));
        throw e;
      }
    }

    @Test(expected = IllegalArgumentException.class)
    public void lookUpWithInvalidArgument_insufficientNumberOfParamOrder() {
      try {
        greeting(0);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        assertThat(
            e.getMessage(),
            allOf(
                containsString("Inconsistent number of parameters are"),
                containsString("Expected:2, Actual: 1")));
        throw e;
      }
    }

    @Test
    public void runMultiParameterFunction$thenExpectedValueReturned() {
      MultiParameterFunction<String> func = greeting(0, 1);
      String ret = func.apply(asList("Hello", "John"));
      System.out.println(ret);
      assertEquals("Hello, John", ret);
    }

    @Test
    public void toStringMultiParameterFunction$thenExpectedValueReturned() {
      MultiParameterFunction<String> func = greeting(0, 1);
      assertEquals("com.github.dakusui.pcond.ut.FunctionsTest$MultiParameterFunctionTest$TargetMethodHolder.greeting(String,String)", func.toString());
    }

    @Test
    public void testMultiParameterFunction$withReversedOrder$thenExpectedValueReturned() {
      MultiParameterFunction<String> func = greeting(1, 0);
      String ret = func.apply(asList("John", "Hello"));
      System.out.println(ret);
      assertEquals("Hello, John", ret);
    }

    @Test
    public void toStringMultiParameterFunction$withReversedOrder$thenExpectedValueReturned() {
      MultiParameterFunction<String> func = greeting(1, 0);
      assertEquals("com.github.dakusui.pcond.ut.FunctionsTest$MultiParameterFunctionTest$TargetMethodHolder.greeting(String,String)(1,0)", func.toString());
    }

    @Test
    public void testHashCodeWithIdenticalObjects() {
      MultiParameterFunction<String> func1 = greeting(0, 1);
      MultiParameterFunction<String> func2 = greeting(0, 1);
      assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithIdenticalObjects() {
      MultiParameterFunction<String> func1 = greeting(0, 1);
      MultiParameterFunction<String> func2 = greeting(0, 1);
      assertEquals(func1, func2);
      assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithIdenticalObjectsCreatedSeparately() {
      MultiParameterFunction<String> func1 = greeting(0, 1);
      MultiParameterFunction<String> func2 = greeting2(0, 1);
      assertEquals(func1, func2);
      assertEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentObjects() {
      MultiParameterFunction<String> func1 = greeting(0, 1);
      MultiParameterFunction<String> func2 = greeting(1, 0);
      assertNotEquals(func1, func2);
      assertNotEquals(func1.hashCode(), func2.hashCode());
    }

    @Test
    public void testEqualsWithDifferentTypeObjects() {
      MultiParameterFunction<String> func1 = greeting(0, 1);
      assertNotEquals(func1, new Object());
    }

    @Test
    public void runVoidReturningMultiParameterFunction$thenNullReturned() {
      MultiParameterFunction<String> func = voidMethod();
      String ret = func.apply(asList("Hello", "John"));
      System.out.println(ret);
      assertNull(ret);
    }

    private static MultiParameterFunction<String> greeting(int... order) {
      return CurryingUtils.Reflections.lookupFunctionForStaticMethod(order, TargetMethodHolder.class, "greeting", String.class, String.class);
    }

    private static MultiParameterFunction<String> greeting2(int... order) {
      Method m = getMethod(TargetMethodHolder.class, "greeting", String.class, String.class);
      List<Integer> paramOrder = Arrays.stream(order).boxed().collect(toList());
      List<Object> args = asList(m, paramOrder);
      return CurryingUtils.Reflections.createMultiParameterFunctionForStaticMethod(args);
    }

    private static MultiParameterFunction<String> voidMethod() {
      return Functions.functionForStaticMethod(TargetMethodHolder.class, "voidMethod", String.class, String.class);
    }

    public static class TargetMethodHolder {
      @SuppressWarnings("unused") // Called through reflection.
      public static String greeting(String hello, String name) {
        return String.format("%s, %s", hello, name);
      }

      @SuppressWarnings("unused") // Called through reflection.
      public static void voidMethod(String hello, String name) {
        System.out.println(String.format("%s, %s", hello, name));
      }
    }
  }
}
