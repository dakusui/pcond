package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.utils.InternalUtils;
import com.github.dakusui.crest.utils.TestBase;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class InternalUtilsTest extends TestBase {
  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenNotFound$thenExceptionThrown() {
    InternalUtils.findMethod(Object.class, "undefined", new Object[] {});
  }

  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenNotFoundBecauseNullNotMatched$thenExceptionThrown() {
    System.out.println(InternalUtils.findMethod(Object.class, "wait", new Object[] { null }));
  }

  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenNotFoundBecauseArgumentNotMatched$thenExceptionThrown() {
    System.out.println(InternalUtils.findMethod(Object.class, "wait", new Object[] { "hello" }));
  }

  @Test
  public void tryToFindMethod$whenMultipleMethodsFoundButNarrowestCanBeDetermined$thenWorksFine() {
    System.out.println(InternalUtils.findMethod(InternalUtilsTest.class, "dummy", new Object[] { "hello" }));
  }

  @Test(expected = RuntimeException.class)
  public void tryToFindMethod$whenMultipleMethodsFoundAndNarrowestCanNotBeDetermined$thenExceptionThrown() {
    try {
      System.out.println(InternalUtils.findMethod(InternalUtilsTest.class, "dummy2", new Object[] { 2 }));
    } catch (RuntimeException e) {
      e.printStackTrace();
      assertThat(e.getMessage(), containsString("more than one"));
      throw e;
    }
  }

  @Test
  public void tryToFindMethod$whenOverloadedMethod$thenLooksGood() {
    Method m = InternalUtils.findMethod(Object.class, "wait", new Object[] { 0L });
    String methodInfo = String.format("%s/%s", m.getName(), m.getParameterTypes().length);

    assertEquals(
        "wait/1",
        methodInfo
    );
  }

  public static class TestList extends LinkedList {
    @Override
    public String get(int i) {
      return "hello";
    }
  }

  @Test
  public void tryToFindMethod$whenOverridden$thenLooksGood() throws InvocationTargetException, IllegalAccessException {
    Method m = InternalUtils.findMethod(TestList.class, "get", new Object[] { 0 });

    assertEquals(
        "hello",
        m.invoke(new TestList(), 100)
    );
  }

  @Test
  public void givenStringContainingControlSequences$formatValue$thenCorrectlyFormatted() {
    assertEquals(
        "\" \\n\\t\\\"\"",
        InternalUtils.summarizeValue(" \n\t\"")
    );
  }

  @Test
  public void givenChar_$r_$formatValue$thenCorrectlyFormatted() {
    assertEquals(
        "\"\\r\"",
        InternalUtils.summarizeValue('\r')
    );
  }

  @Test
  public void givenNull$formatValue$thenCorrectlyFormatted() {
    assertEquals(
        "null",
        InternalUtils.summarizeValue((Object) null)
    );
  }

  /*
   * This method is used by 'tryToFindMethod$whenMultipleMethodsFoundAndNarrowestCanBeDetermined$thenWorksFine'
   * reflectively.
   */
  @SuppressWarnings("unused")
  public Object dummy(Object arg) {
    return "object";
  }

  /*
   * This method is used by 'tryToFindMethod$whenMultipleMethodsFoundAndNarrowestCanBeDetermined$thenWorksFine'
   * reflectively.
   */
  @SuppressWarnings("unused")
  public Object dummy(String arg) {
    return "string";
  }

  /*
   * This method is used by 'tryToFindMethod$whenMultipleMethodsFoundAndNarrowestCannotBeDetermined$thenExceptionThrown'
   * reflectively.
   */
  @SuppressWarnings("unused")
  public Object dummy2(int arg) {
    return "int";
  }

  /*
   * This method is used by 'tryToFindMethod$whenMultipleMethodsFoundAndNarrowestCannotBeDetermined$thenExceptionThrown'
   * reflectively.
   */
  @SuppressWarnings("unused")
  public Object dummy2(Integer arg) {
    return "Integer";
  }

}
