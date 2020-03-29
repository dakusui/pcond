package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.ExtraFunctions;
import com.github.dakusui.pcond.functions.currying.CurriedFunction;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.ut.testdata.FailingConstructor;
import com.github.dakusui.pcond.ut.testdata.IntentionalException;
import com.github.dakusui.pcond.ut.testdata.NoParameterConstructorAbsent;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class InternalUtilsTest {
  public static class FormatObject {
    static class InnerClass {
    }

    @Test
    public void testFormatObject$collection3() {
      assertEquals(
          InternalUtils.formatObject(asList("a", "b", "c")),
          "(\"a\",\"b\",\"c\")");
    }

    @Test
    public void testFormatObject$collection4() {
      assertEquals(
          InternalUtils.formatObject(asList("a", "b", "c", "d")),
          "(\"a\",\"b\",\"c\"...;4)");
    }

    @Test
    public void testFormatObject$array4() {
      assertEquals(
          InternalUtils.formatObject(new String[]{"a", "b", "c", "d"}),
          "(\"a\",\"b\",\"c\"...;4)");
    }

    @Test
    public void testFormatObject$longString() {
      assertEquals(
          InternalUtils.formatObject("HelloWorldHelloWorldHelloWorldHelloWorldHelloWorld"),
          "\"HelloWorldHe...World\"");
    }

    @Test
    public void testFormatObject$boundaryLengthString() {
      assertEquals(
          InternalUtils.formatObject("HelloHelloHelloHello"),
          "\"HelloHelloHelloHello\"");
    }

    @Test
    public void testFormatObject$InnerClassObject() {
      assertThat(
          InternalUtils.formatObject(new InnerClass()),
          startsWith("InnerClass@"));
    }

    @Test
    public void testCreateInstanceFromClassName() {
      String created = InternalUtils.createInstanceFromClassName(String.class, "java.lang.String");
      assertEquals("", created);
    }

    @Test(expected = InternalException.class)
    public void testCreateInstanceFromClassName$thenNotFound() {
      String requestedClassName = "java.lang.String_";
      try {
        InternalUtils.createInstanceFromClassName(String.class, requestedClassName);
      } catch (InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("requested class was not found"),
                containsString(requestedClassName)
            ));
        throw e;
      }
    }

    @Test(expected = InternalException.class)
    public void testCreateInstanceFromClassName$thenNotInstance() {
      String requestedClassName = "java.lang.Object";
      try {
        InternalUtils.createInstanceFromClassName(String.class, requestedClassName);
      } catch (InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("not an instance"),
                containsString(String.class.getCanonicalName()),
                containsString(requestedClassName)
            ));
        throw e;
      }
    }

    @Test(expected = InternalException.class)
    public void testCreateInstanceFromClassName$thenNoConstructor() {
      String requestedClassName = NoParameterConstructorAbsent.class.getCanonicalName();
      try {
        InternalUtils.createInstanceFromClassName(Object.class, requestedClassName);
      } catch (InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("Public constructor"),
                containsString(requestedClassName),
                containsString("not found")
            ));
        throw e;
      }
    }

    @Test(expected = IntentionalException.class)
    public void testCreateInstanceFromClassName$thenConstructorFails() throws Throwable {
      String requestedClassName = FailingConstructor.class.getCanonicalName();
      try {
        InternalUtils.createInstanceFromClassName(Object.class, requestedClassName);
      } catch (InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("Public constructor"),
                containsString(requestedClassName),
                containsString("but threw an exception")
            ));
        throw e.getCause();
      }
    }
  }

  public static class AssertFailsWith extends TestBase.ForAssertionEnabledVM {
    @Test
    public void givenTrue$whenAssertionFailsWith$thenFalse() {
      assertFalse(InternalUtils.assertFailsWith(true));
    }

    /**
     * This is the only test case that fails when {@code -da} is given to the JVM.
     * You can give {@code assumeTrue(InternalUtils.assertFailsWith(false))}, before the {@code assertTrue}, but
     * it will hurt mutation test coverage.
     * I decided to let it fail, when {@code -da} is set, since this library should usually be built with {@code -ea}
     * option.
     */
    @Test
    public void givenFalse$whenAssertionFailsWith$thenTrue() {
      assertTrue(InternalUtils.assertFailsWith(false));
    }
  }

  public static class CurryingTest {
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

    @Test(expected = AssertionError.class)
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

    @Test(expected = AssertionError.class)
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

    public static String example(int i, int j) {
      return String.format("%s+%s=%s", i, j, i + j);
    }

    public static String example(int i) {
      return String.format("value=%s", i);
    }

    public static boolean endsWith(String s, String suffix) {
      return s.endsWith(suffix);
    }
  }
}
