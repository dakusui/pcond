package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class InternalUtilsTest {
  public static class FormtObject {
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
          InternalUtils.formatObject(new String[] { "a", "b", "c", "d" }),
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

  }

  public static class IsAssertionEnabled extends TestBase.ForAssertionEnabledVM {
    @Test
    public void testIsAssertionEnabled() {
      assertTrue(InternalUtils.isAssertionEnabled());
    }
  }
}
