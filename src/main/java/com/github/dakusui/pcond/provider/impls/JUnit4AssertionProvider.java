package com.github.dakusui.pcond.provider.impls;

import java.util.Properties;

public class JUnit4AssertionProvider extends BaseAssertionProvider {
  public JUnit4AssertionProvider(Properties properties) {
    super(properties);
  }

  @SuppressWarnings("unchecked")

  @Override
  public ExceptionComposer exceptionComposer() {
    return new ExceptionComposer() {
      @Override
      public <T extends RuntimeException> T testSkippedException(String message) {
        throw (T) createException("org.junit.AssumptionViolatedException", reportComposer().explanationFromMessage(message), (c, exp) ->
            c.getConstructor(String.class).newInstance(exp.message()));
      }

      @Override
      public <T extends Error> T testFailedException(String message) {
        throw testFailedException(reportComposer().explanationFromMessage(message));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation) {
        throw (T) createException("org.junit.ComparisonFailure", explanation, (c, exp) ->
            c.getConstructor(String.class, String.class, String.class).newInstance(exp.message(), exp.expected(), exp.actual()));
      }
    };
  }
}
