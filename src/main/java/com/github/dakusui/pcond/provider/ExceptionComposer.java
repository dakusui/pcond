package com.github.dakusui.pcond.provider;

import java.util.function.Function;

import static com.github.dakusui.pcond.provider.impls.AssertionProviderImpl.createException;

public interface ExceptionComposer {
  interface Base {
    default Throwable exceptionForNonNullViolation(String message) {
      return new NullPointerException(message);
    }

    default Throwable exceptionForIllegalState(String message) {
      return new IllegalStateException(message);
    }

    Throwable exceptionForGeneralViolation(String message);
  }

  interface ForPrecondition extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new PreconditionViolationException(message);
    }

    <T extends Throwable> T exceptionForIllegalArgument(String message);
  }

  interface ForInvariantCondition extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new AssertionError(message);
    }
  }

  interface ForPostCondition extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new PostconditionViolationException(message);
    }
  }

  interface ForValidation extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new ValidationException(message);
    }
  }

  ForPrecondition forPrecondition();

  ForInvariantCondition forInvariantCondition();

  ForPostCondition forPostCondition();

  ForValidation forValidation();

  <T extends RuntimeException> T testSkippedException(String message);

  default <T extends RuntimeException> T testSkippedException(Explanation explanation) {
    return testSkippedException(explanation.toString());
  }

  <T extends Error> T testFailedException(String message);

  default <T extends Error> T testFailedException(Explanation explanation) {
    return testFailedException(explanation.toString());
  }

  @SuppressWarnings("unchecked")
  default <E extends RuntimeException> E preconditionViolationException(String message) {
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

      @Override
      public ForPrecondition forPrecondition() {
        return new ForPrecondition() {
          @Override
          public <T extends Throwable> T exceptionForIllegalArgument(String message) {
            return (T) new IllegalArgumentException(message);
          }
        };
      }

      @Override
      public ForInvariantCondition forInvariantCondition() {
        return new ForInvariantCondition() {
        };
      }

      @Override
      public ForPostCondition forPostCondition() {
        return new ForPostCondition() {
        };
      }

      @Override
      public ForValidation forValidation() {
        return new ForValidation() {
        };
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

      @Override
      public ForPrecondition forPrecondition() {
        return null;
      }

      @Override
      public ForInvariantCondition forInvariantCondition() {
        return null;
      }

      @Override
      public ForPostCondition forPostCondition() {
        return null;
      }

      @Override
      public ForValidation forValidation() {
        return null;
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
