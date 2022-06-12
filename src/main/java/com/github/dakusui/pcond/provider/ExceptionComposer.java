package com.github.dakusui.pcond.provider;

import java.util.function.Function;

import static com.github.dakusui.pcond.provider.impls.AssertionProviderImpl.createException;

public interface ExceptionComposer {

  <T extends RuntimeException> T testSkippedException(String message);

  default <T extends RuntimeException> T testSkippedException(Explanation explanation) {
    return testSkippedException(explanation.toString());
  }

  <T extends Error> T testFailedException(String message);

  default <T extends Error> T testFailedException(Explanation explanation) {
    return testFailedException(explanation.toString());
  }

  @SuppressWarnings("unchecked")
  default <E extends RuntimeException>  E preconditionViolationException(String message) {
    return (E) new PreconditionViolationException(message);
  }

  @SuppressWarnings("unchecked")
  default <E extends Exception> E postconditionViolationException(String message) {
    return (E) new PostconditionViolationException(message);
  }

  static ExceptionComposer createExceptionComposerForJUnit4(final ReportComposer reportComposer) {
    return new ExceptionComposer() {
      private ReportComposer reportComposer() {
        return reportComposer;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message) {
        throw (T) createException(
            "org.junit.AssumptionViolatedException",
            reportComposer().explanationFromMessage(message),
            (c, exp) -> c.getConstructor(String.class).newInstance(exp.message()));
      }

      @Override
      public <T extends Error> T testFailedException(String message) {
        throw testFailedException(reportComposer().explanationFromMessage(message));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation) {
        throw (T) createException(
            "org.junit.ComparisonFailure",
            explanation,
            (c, exp) -> c.getConstructor(String.class, String.class, String.class).newInstance(exp.message(), exp.expected(), exp.actual()));
      }
    };
  }

  static ExceptionComposer createExceptionComposerForOpentest4J(AssertionProvider assertionProvider) {
    return new ExceptionComposer() {
      private ReportComposer reportComposer() {
        return assertionProvider.reportComposer();
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message) {
        throw (T) createException("org.opentest4j.TestSkippedException", reportComposer().explanationFromMessage(message), (c, exp) ->
            c.getConstructor(String.class).newInstance(exp.message()));
      }

      @Override
      public <T extends Error> T testFailedException(String message) {
        throw testFailedException(reportComposer().explanationFromMessage(message));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation) {
        throw (T) createException("org.opentest4j.AssertionFailedError", explanation, (c, exp) ->
            c.getConstructor(String.class, Object.class, Object.class).newInstance(exp.message(), exp.expected(), exp.actual()));
      }
    };
  }
}
