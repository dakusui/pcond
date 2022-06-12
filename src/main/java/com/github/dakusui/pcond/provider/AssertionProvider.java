package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.impls.JUnit4AssertionProvider;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.provider.impls.BaseAssertionProvider.createException;
import static java.lang.String.format;

/**
 * An interface of a policy for behaviours on 'contract violations'.
 */
public interface AssertionProvider {
  AssertionProviderBase.ExceptionComposer exceptionComposer();

  AssertionProviderBase.MessageComposer messageComposer();

  AssertionProviderBase.ReportComposer reportComposer();


  /**
   * A constant field that holds the default provider instance.
   */
  AssertionProvider INSTANCE = createAssertionProvider(System.getProperties());

  Configuration configuration();

  /**
   * Returns a provider instance created from a given `Properties` object.
   * This method reads the value for the FQCN of this class (`com.github.dakusui.pcond.provider.AssertionProvider`) and creates an instance of a class specified by the value.
   * If the value is not set, this value instantiates an object of `DefaultAssertionProvider` and returns it.
   *
   * @param properties A {@code Properties} object from which an {@code AssertionProvider} is created
   * @return Created provider instance.
   */
  static AssertionProvider createAssertionProvider(Properties properties) {
    String propertyKeyName = AssertionProvider.class.getCanonicalName();
    if (properties.containsKey(propertyKeyName)) {
      return InternalUtils.createInstanceFromClassName(AssertionProvider.class, properties.getProperty(propertyKeyName), System.getProperties());
    }
    return new JUnit4AssertionProvider(properties);
  }

  /**
   * Checks a value if it is {@code null} or not.
   * If it is not a {@code null}, this method returns the given value itself.
   *
   * @param value The given value.
   * @param <T>   The type of the value.
   * @return The {@code value}.
   */
  <T> T requireNonNull(T value);

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  <T> T requireArgument(T value, Predicate<? super T> cond);

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  <T> T requireState(T value, Predicate<? super T> cond);

  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception returned by {@link AssertionProviderBase.ExceptionComposer#preconditionViolationException()}
   * is thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check if `value` satisfies.
   * @param <T>   The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  <T> T require(T value, Predicate<? super T> cond);

  <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> T ensureNonNull(T value);

  <T> T ensureState(T value, Predicate<? super T> cond);

  <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E;

  <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> void checkInvariant(T value, Predicate<? super T> cond);

  <T> void checkPrecondition(T value, Predicate<? super T> cond);

  <T> void checkPostcondition(T value, Predicate<? super T> cond);

  <T> void assertThat(T value, Predicate<? super T> cond);

  <T> void assumeThat(T value, Predicate<? super T> cond);

  interface Configuration {
    static Configuration create(Properties properties) {
      return new Configuration() {
      };
    }

    default int summarizedStringLength() {
      return 40;
    }

    default AssertionProviderBase.ExceptionComposer createExceptionComposerFromProperties(Properties properties, AssertionProvider assertionProvider) {
      if (isJunit4(properties))
        return new AssertionProviderBase.ExceptionComposer() {
          @Override
          public AssertionProviderBase.ReportComposer reportComposer() {
            return assertionProvider.reportComposer();
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
          public <T extends Error> T testFailedException(AssertionProviderBase.Explanation explanation) {
            throw (T) createException(
                "org.junit.ComparisonFailure",
                explanation,
                (c, exp) -> c.getConstructor(String.class, String.class, String.class).newInstance(exp.message(), exp.expected(), exp.actual()));
          }
        };
      return new AssertionProviderBase.ExceptionComposer() {
        @Override
        public AssertionProviderBase.ReportComposer reportComposer() {
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
        public <T extends Error> T testFailedException(AssertionProviderBase.Explanation explanation) {
          throw (T) createException("org.opentest4j.AssertionFailedError", explanation, (c, exp) ->
              c.getConstructor(String.class, Object.class, Object.class).newInstance(exp.message(), exp.expected(), exp.actual()));
        }
      };
    }

    default boolean isJunit4(Properties properties) {
      return true;
    }

    default AssertionProviderBase.ReportComposer createReportComposer() {
      return new AssertionProviderBase.ReportComposer() {
      };
    }

    default AssertionProviderBase.MessageComposer createMessageComposer() {
      return new AssertionProviderBase.MessageComposer() {
        @Override
        public <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
          return format("value:<%s> violated precondition:value %s", formatObject(value), predicate);
        }

        @Override
        public <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
          return format("value:<%s> violated postcondition:value %s", formatObject(value), predicate);
        }

        @Override
        public <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate) {
          return "Value:" + formatObject(t) + " violated: " + predicate.toString();
        }

        @Override
        public <T> String composeMessageForValidation(T t, Predicate<? super T> predicate) {
          return "Value:" + formatObject(t) + " violated: " + predicate.toString();
        }
      };
    }
  }
}
