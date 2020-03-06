package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.ComparablesExample;
import com.github.dakusui.crest.utils.TestBase;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ExamplesTest extends TestBase {
  @Test
  public void bankAccount() {
    Class<?> exampleClass = BankAccount.class;
    verifyExample(exampleClass);
  }

  @Test
  public void simpleExample() {
    Class<?> exampleClass = SimpleExamples.class;
    verifyExample(exampleClass);
  }

  @Test
  public void inThincrest() {
    Class<?> exampleClass = InThincrest.class;
    verifyExample(exampleClass);
  }

  @Test
  public void comparableExample$Byte() {
    verifyExample(ComparablesExample.ByteExample.class);
  }

  @Test
  public void comparableExample$Char() {
    verifyExample(ComparablesExample.CharExample.class);
  }

  @Test
  public void comparableExample$Short() {
    verifyExample(ComparablesExample.ShortExample.class);
  }

  @Test
  public void comparableExample$Integer() {
    verifyExample(ComparablesExample.IntegerExample.class);
  }

  @Test
  public void comparableExample$Long() {
    verifyExample(ComparablesExample.LongExample.class);
  }

  @Test
  public void comparableExample$Float() {
    verifyExample(ComparablesExample.FloatExample.class);
  }

  @Test
  public void comparableExample$Double() {
    verifyExample(ComparablesExample.DoubleExample.class);
  }

  private void verifyExample(Class<?> javaTestClass) {
    List<String> expectation = new LinkedList<>();
    List<String> actualResult = new LinkedList<>();

    JUnitCore jUnitCore = new JUnitCore();
    TestClass testClass = new TestClass(javaTestClass);
    for (FrameworkMethod m : testClass.getAnnotatedMethods(Test.class).stream().sorted(Comparator.comparing(FrameworkMethod::getName)).collect(Collectors.toList())) {
      Request request = Request.method(testClass.getJavaClass(), m.getName());
      Result result = jUnitCore.run(request);
      expectation.add(String.format("%s: %d: %s", formatResult(shouldPass(m.getName())), 1, m.getName()));
      actualResult.add(String.format("%s: %d: %s", formatResult(result.wasSuccessful()), result.getRunCount(), m.getName()));
    }

    assertEquals(
        String.join("\n", expectation),
        String.join("\n", actualResult)
    );
  }

  private static String formatResult(boolean wasSuccessful) {
    return wasSuccessful ? "PASS" : "FAIL";
  }

  private static boolean shouldPass(String methodName) {
    if (methodName.endsWith("thenPass"))
      return true;
    if (methodName.endsWith("thenFail"))
      return false;
    throw new IllegalArgumentException(methodName);
  }
}
