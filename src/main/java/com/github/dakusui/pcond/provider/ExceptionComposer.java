package com.github.dakusui.pcond.provider;

import java.lang.reflect.InvocationTargetException;

import static com.github.dakusui.pcond.provider.ExceptionComposer.Utils.createException;

public interface ExceptionComposer {
  ForPrecondition forRequire();

  ForPostCondition forEnsure();

  ForValidate defaultForValidate();

  ForAssertion forAssert();

  ForTestAssertion forAssertThat();

  class Impl implements ExceptionComposer {
    final private ForPrecondition  forRequire;
    final private ForPostCondition forEnsure;
    final private ForValidate      defaultForValidate;
    final private ForAssertion     forAssert;
    final private ForTestAssertion forAssertThat;

    public Impl(ForPrecondition forRequire, ForPostCondition forEnsure, ForValidate defaultForValidate, ForAssertion forAssert, ForTestAssertion forAssertThat) {
      this.forRequire = forRequire;
      this.forEnsure = forEnsure;
      this.defaultForValidate = defaultForValidate;
      this.forAssert = forAssert;
      this.forAssertThat = forAssertThat;
    }

    @Override
    public ForPrecondition forRequire() {
      return this.forRequire;
    }

    @Override
    public ForPostCondition forEnsure() {
      return this.forEnsure;
    }

    @Override
    public ForValidate defaultForValidate() {
      return this.defaultForValidate;
    }

    @Override
    public ForAssertion forAssert() {
      return this.forAssert;
    }

    @Override
    public ForTestAssertion forAssertThat() {
      return this.forAssertThat;
    }
  }

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

    class Default implements ForPrecondition {
      @Override
      public Throwable exceptionForIllegalArgument(String message) {
        return new IllegalArgumentException(message);
      }
    }
  }

  interface ForPostCondition extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new PostconditionViolationException(message);
    }

    class Default implements ForPostCondition {
    }
  }

  interface ForValidate extends Base {
    @Override
    default Throwable exceptionForGeneralViolation(String message) {
      return new ValidationException(message);
    }

    default Throwable exceptionForIllegalArgument(String message) {
      return new IllegalArgumentException(message);
    }

    class Default implements ForValidate {
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

    class Default implements ForAssertion {
    }
  }

  interface ForTestAssertion {
    <T extends RuntimeException> T testSkippedException(String message, ReportComposer reportComposer);

    default <T extends RuntimeException> T testSkippedException(Explanation explanation, ReportComposer reportComposer) {
      return testSkippedException(explanation.toString(), reportComposer);
    }

    <T extends Error> T testFailedException(Explanation explanation, ReportComposer reportComposer);

    class JUnit4 implements ForTestAssertion {
      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message, ReportComposer reportComposer) {
        throw (T) createException(
            "org.junit.AssumptionViolatedException",
            reportComposer.explanationFromMessage(message),
            (c, exp) -> c.getConstructor(String.class).newInstance(exp.message()));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation, ReportComposer reportComposer) {
        throw (T) createException(
            "org.junit.ComparisonFailure",
            explanation,
            (c, exp) -> c.getConstructor(String.class, String.class, String.class).newInstance(exp.message(), exp.expected(), exp.actual()));
      }
    }

    class Opentest4J implements ForTestAssertion {
      @SuppressWarnings("unchecked")
      @Override
      public <T extends RuntimeException> T testSkippedException(String message, ReportComposer reportComposer) {
        throw (T) createException("org.opentest4j.TestSkippedException", reportComposer.explanationFromMessage(message), (c, exp) ->
            c.getConstructor(String.class).newInstance(exp.message()));
      }

      @SuppressWarnings("unchecked")
      @Override
      public <T extends Error> T testFailedException(Explanation explanation, ReportComposer reportComposer) {
        throw (T) createException("org.opentest4j.AssertionFailedError", explanation, (c, exp) ->
            c.getConstructor(String.class, Object.class, Object.class).newInstance(exp.message(), exp.expected(), exp.actual()));
      }
    }
  }

  enum Utils {
    ;

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T createException(String className, Explanation explanation, ReflectiveExceptionFactory<T> reflectiveExceptionFactory) {
      try {
        return reflectiveExceptionFactory.apply((Class<T>) Class.forName(className), explanation);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + className + "' (NOT FOUND)", e);
      }
    }

    @FunctionalInterface
    public
    interface ReflectiveExceptionFactory<T extends Throwable> {
      T create(Class<T> c, Explanation explanation) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

      default T apply(Class<T> c, Explanation explanation) {
        try {
          return create(c, explanation);
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
          throw new RuntimeException("FAILED TO INSTANTIATE EXCEPTION: '" + c.getCanonicalName() + "'", e);
        }
      }
    }
  }

}
