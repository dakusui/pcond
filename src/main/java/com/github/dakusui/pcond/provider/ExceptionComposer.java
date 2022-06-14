package com.github.dakusui.pcond.provider;

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

    Throwable exceptionForIllegalArgument(String message);
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

  interface ForAssertion {
    default Throwable exceptionPreconditionViolation(String message) {
      return new AssertionError(message);
    }

    default Throwable exceptionInvariantConditionViolation(String message) {
      return new AssertionError(message);
    }

    default Throwable exceptionPostconditionViolation(String message) {
      return new AssertionError(message);
    }
  }

  ForPrecondition forRequire();

  ForInvariantCondition forInvariantCondition();

  ForPostCondition forEnsure();

  ForValidation forValidation();

  ForAssertion forAssert();

  <T extends RuntimeException> T testSkippedException(String message);

  default <T extends RuntimeException> T testSkippedException(Explanation explanation) {
    return testSkippedException(explanation.toString());
  }

  <T extends Error> T testFailedException(Explanation explanation);

  static ExceptionComposer createExceptionComposerForJUnit4(final ForPrecondition forPrecondition, final ForInvariantCondition forInvariantCondition, final ForPostCondition forPostCondition, final ForValidation forValidation, final ForAssertion forAssertion, final ReportComposer reportComposer) {
    return new ExceptionComposer() {

      private ReportComposer reportComposer() {
        return reportComposer;
      }

      @Override
      public ForPrecondition forRequire() {
        return forPrecondition;
      }

      @Override
      public ForInvariantCondition forInvariantCondition() {
        return forInvariantCondition;
      }

      @Override
      public ForPostCondition forEnsure() {
        return forPostCondition;
      }

      @Override
      public ForValidation forValidation() {
        return forValidation;
      }

      @Override
      public ForAssertion forAssert() {
        return forAssertion;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message) {
        throw (T) createException(
            "org.junit.AssumptionViolatedException",
            reportComposer().explanationFromMessage(message),
            (c, exp) -> c.getConstructor(String.class).newInstance(exp.message()));
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

  static ExceptionComposer createExceptionComposerForOpentest4J(ForPrecondition forPrecondition, ForInvariantCondition forInvariantCondition, final ForPostCondition forPostCondition, ForValidation forValidation, ForAssertion forAssertion, final ReportComposer reportComposer) {
    return new ExceptionComposer() {
      private ReportComposer reportComposer() {
        return reportComposer;
      }

      @Override
      public ForPrecondition forRequire() {
        return forPrecondition;
      }

      @Override
      public ForInvariantCondition forInvariantCondition() {
        return forInvariantCondition;
      }

      @Override
      public ForPostCondition forEnsure() {
        return forPostCondition;
      }

      @Override
      public ForValidation forValidation() {
        return forValidation;
      }

      @Override
      public ForAssertion forAssert() {
        return forAssertion;
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message) {
        throw (T) createException("org.opentest4j.TestSkippedException", reportComposer().explanationFromMessage(message), (c, exp) ->
            c.getConstructor(String.class).newInstance(exp.message()));
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
