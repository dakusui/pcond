package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.internals.Exceptions;
import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.ut.testdata.FailingConstructor;
import com.github.dakusui.pcond.ut.testdata.IntentionalException;
import com.github.dakusui.pcond.ut.testdata.NoParameterConstructorAbsent;
import com.github.dakusui.pcond.utils.TestUtils;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

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

    @Test(expected = Exceptions.InternalException.class)
    public void testCreateInstanceFromClassName$thenNotFound() {
      String requestedClassName = "java.lang.String_";
      try {
        InternalUtils.createInstanceFromClassName(String.class, requestedClassName);
      } catch (Exceptions.InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("requested class was not found"),
                containsString(requestedClassName)
            ));
        throw e;
      }
    }

    @Test(expected = Exceptions.InternalException.class)
    public void testCreateInstanceFromClassName$thenNotInstance() {
      String requestedClassName = "java.lang.Object";
      try {
        InternalUtils.createInstanceFromClassName(String.class, requestedClassName);
      } catch (Exceptions.InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("not an instance"),
                containsString(String.class.getCanonicalName()),
                containsString(requestedClassName)
            ));
        throw e;
      }
    }

    @Test(expected = Exceptions.InternalException.class)
    public void testCreateInstanceFromClassName$thenNoConstructor() {
      String requestedClassName = NoParameterConstructorAbsent.class.getCanonicalName();
      try {
        InternalUtils.createInstanceFromClassName(Object.class, requestedClassName);
      } catch (Exceptions.InternalException e) {
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
      } catch (Exceptions.InternalException e) {
        assertThat(e.getMessage(),
            allOf(
                containsString("Public constructor"),
                containsString(requestedClassName),
                containsString("but threw an exception")
            ));
        throw e.getCause();
      }
    }

    @Test
    public void test () {

    }
  }

  public static class IsAssertionEnabled extends TestBase.ForAssertionEnabledVM {
    @Test
    public void testIsAssertionEnabled() {
      assertTrue(InternalUtils.isAssertionEnabled());
    }
  }
}
