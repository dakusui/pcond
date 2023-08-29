package com.github.dakusui.pcond.metamor;

import com.github.dakusui.pcond.validator.ExceptionComposer;
import com.github.dakusui.pcond.validator.ReportComposer;

public enum Utils {
  ;

  public static void initializeThincrestForMetamorUnit() {
    configureTestAssertionExceptionComposer(ExceptionComposer.ForTestAssertion.Opentest4J.class);
    configureReportComposer(MetamorphicReportComposer.class);
  }

  private static void configureReportComposer(Class<? extends ReportComposer> reportComposerClass) {
    System.getProperties().setProperty("com.github.dakusui.pcond.reportComposer", reportComposerClass.getName());
  }

  private static void configureTestAssertionExceptionComposer(Class<? extends ExceptionComposer.ForTestAssertion> exceptionComposerClass) {
    System.getProperties().setProperty("com.github.dakusui.pcond.exceptionComposerForAssertThat", exceptionComposerClass.getName());
  }

  public static void requireState(boolean c, String message) {
    if (!c)
      throw new IllegalStateException(message);
  }
  public static void requireArgument(boolean c, String message) {
    if (!c)
      throw new IllegalArgumentException(message);
  }
}
