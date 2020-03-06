package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.internals.InternalUtils;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class InternalUtilsTest {
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
  public void testFormatObject$InnerClassObject() {
    assertThat(
        InternalUtils.formatObject(new InnerClass()),
        startsWith("InnerClass@"));
  }
}
