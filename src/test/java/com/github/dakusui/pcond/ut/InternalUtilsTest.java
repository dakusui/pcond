package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.internals.InternalException;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.ut.testdata.FailingConstructor;
import com.github.dakusui.pcond.ut.testdata.IntentionalException;
import com.github.dakusui.pcond.ut.testdata.NoParameterConstructorAbsent;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Predicate;

import static java.util.Arrays.asList;
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

  public static class WrapperClassOf {
    @Test
    public void testInteger() {
      assertEquals(Integer.class, InternalUtils.wrapperClassOf(int.class));
    }
    @Test
    public void testLong() {
      assertEquals(Long.class, InternalUtils.wrapperClassOf(long.class));
    }

    @Test
    public void testBoolean() {
      assertEquals(Boolean.class, InternalUtils.wrapperClassOf(boolean.class));
    }

    @Test
    public void testByte() {
      assertEquals(Byte.class, InternalUtils.wrapperClassOf(byte.class));
    }

    @Test
    public void testCharacter() {
      assertEquals(Character.class, InternalUtils.wrapperClassOf(char.class));
    }

    @Test
    public void testFloat() {
      assertEquals(Float.class, InternalUtils.wrapperClassOf(float.class));
    }

    @Test
    public void testDouble() {
      assertEquals(Double.class, InternalUtils.wrapperClassOf(double.class));
    }

    @Test
    public void testShort() {
      assertEquals(Short.class, InternalUtils.wrapperClassOf(short.class));
    }

    @Test
    public void testVoid() {
      assertEquals(Void.class, InternalUtils.wrapperClassOf(void.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOther() {
      InternalUtils.wrapperClassOf(Object.class);
    }
  }

  public static class ToEvaluableIfNecessary {

    @Test
    public void givenNonEvaluable$whenToEvaluableIfNecessary$thenConverted() {
      Predicate<Object> predicate = Predicate.isEqual("Hello");
      Evaluable<Object> ev = InternalUtils.toEvaluableIfNecessary(predicate);
      assertNotNull(ev);
    }
  }

}
