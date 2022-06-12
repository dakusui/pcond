package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.provider.impls.AssertionProviderImpl;

import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.provider.impls.AssertionProviderImpl.createException;
import static java.lang.String.format;

/**
 * An interface of a policy for behaviours on 'contract violations'.
 */
public interface AssertionProvider {
  /**
   * A constant field that holds the default provider instance.
   */
  AssertionProvider INSTANCE = createAssertionProvider(System.getProperties());

  ExceptionComposer exceptionComposer();

  MessageComposer messageComposer();

  ReportComposer reportComposer();


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
    return new AssertionProviderImpl(properties);
  }

  /**
   * Checks a value if it is {@code null} or not.
   * If it is not a {@code null}, this method returns the given value itself.
   *
   * @param value The given value.
   * @param <T>   The type of the value.
   * @return The {@code value}.
   */
  default <T> T requireNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(NullPointerException::new));
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(IllegalArgumentException::new));
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T requireState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(IllegalStateException::new));
  }
  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception returned by {@link ExceptionComposer#preconditionViolationException()}
   * is thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check if `value` satisfies.
   * @param <T>   The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  default <T> T require(T value, Predicate<? super T> cond) {
    return require(value, cond, this.exceptionComposer().preconditionViolationException());
  }

  default <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) throws E {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionFactory));
  }
  default <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) throws E {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForValidation, ExceptionFactory.from(exceptionFactory));
  }

  default <T> T ensureNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(NullPointerException::new));
  }
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(IllegalStateException::new));
  }
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E {
    return ensure(value, cond, this.exceptionComposer().<E>postconditionViolationException());
  }
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(exceptionComposer));
  }
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, AssertionError::new);
  }
  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, AssertionError::new);
  }
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, AssertionError::new);
  }
  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::<Error>testFailedException);
  }

  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::<RuntimeException>testSkippedException);
  }

  <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E;

  default <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<E> exceptionFactory) throws E {
    return checkValue(value, cond, messageComposer, msg -> exceptionFactory.apply(reportComposer().explanationFromMessage(msg)));
  }

  interface Configuration {
    static Configuration create(Properties properties) {
      return new Configuration() {
      };
    }

    default int summarizedStringLength() {
      return 40;
    }

    default ExceptionComposer createExceptionComposerFromProperties(Properties properties, AssertionProvider assertionProvider) {
      if (isJunit4(properties))
        return new ExceptionComposer() {
          @Override
          public ReportComposer reportComposer() {
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
          public <T extends Error> T testFailedException(Explanation explanation) {
            throw (T) createException(
                "org.junit.ComparisonFailure",
                explanation,
                (c, exp) -> c.getConstructor(String.class, String.class, String.class).newInstance(exp.message(), exp.expected(), exp.actual()));
          }
        };
      return new ExceptionComposer() {
        @Override
        public ReportComposer reportComposer() {
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

    default boolean isJunit4(Properties properties) {
      return true;
    }

    default ReportComposer createReportComposer() {
      return new ReportComposer() {
      };
    }

    default MessageComposer createMessageComposer() {
      return new MessageComposer() {
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
