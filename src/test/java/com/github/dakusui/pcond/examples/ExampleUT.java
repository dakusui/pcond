package com.github.dakusui.pcond.examples;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import static com.github.dakusui.pcond.TestAssertions.assertThat;
import static com.github.dakusui.pcond.functions.Predicates.*;

@RunWith(Enclosed.class)
public class ExampleUT {
  public static class Inner {
    @Test
    public void shouldPass_testFirstNameOf() {
      String firstName = NameUtil.firstNameOf("Risa Kitajima");
      assertThat(firstName, and(not(containsString(" ")), startsWith("R")));
    }

    @Deprecated
    @Test
    public void shouldFail_testFirstNameOf() {
      String firstName = NameUtil.firstNameOf("Yoshihiko Naito");
      assertThat(firstName, and(not(containsString(" ")), startsWith("N")));
    }

    @Test
    public void shouldError_testFirstNameOf() {
      throw new RuntimeException("ERR");
    }

    @Test
    public void shouldOOM_testFirstNameOf() {
      throw new OutOfMemoryError("ERR");
    }
  }


  public static void main(String... args) throws Exception {
    JUnitCore core = new JUnitCore();
    core.addListener(new RunListener() {
      @Override
      public void testRunStarted(Description description) {
        System.out.println("testRunStarted");
        printDescription(description);
      }

      @Override
      public void testSuiteStarted(Description description) {
        System.out.println("testSuiteStarted");
        printDescription(description);
      }

      @Override
      public void testStarted(Description description) {
        System.out.println("testStarted");
        printDescription(description);
      }

      @Override
      public void testFinished(Description description) {
        System.out.println("finished");
        printDescription(description);
      }

      @Override
      public void testFailure(Failure failure) {
        System.out.println("failure");
        printDescription(failure.getDescription());
      }

      @Override
      public void testAssumptionFailure(Failure failure) {
        System.out.println("assumptionFailure");
        printDescription(failure.getDescription());
      }

      @Override
      public void testIgnored(Description description) throws Exception {
        System.out.println("testIgnored");
        printDescription(description);
      }

      private void printDescription(Description description) {
        System.out.println("  hashCode=<" + System.identityHashCode(description) + ">");
        System.out.println("  methodName=<" + description.getMethodName() + ">");
        System.out.println("  displayName=<" + description.getDisplayName() + ">");
        System.out.println("  annotation=<" + description.getAnnotations() + ">");
        System.out.println("  className=<" + description.getClassName() + ">");
        System.out.println("  isEmpty=<" + description.isEmpty() + ">");
        System.out.println("  children=<" + description.getChildren() + ">");
        System.out.println("  testClass=<" + description.getTestClass() + ">");
      }
    });
    core.run(ExampleUT.class).getFailures().forEach((Failure each) -> {
      System.err.println("message=<" + each.getMessage() + ">");
      System.err.println("----");
      System.err.println(each.getException().getMessage());
    });
  }
}
