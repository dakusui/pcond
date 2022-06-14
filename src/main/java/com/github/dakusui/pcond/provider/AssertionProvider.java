package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.provider.impls.AssertionProviderImpl;

import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

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
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionComposer().forPrecondition()::exceptionForNonNullViolation));
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
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionComposer().forPrecondition()::exceptionForIllegalArgument));
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
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionComposer().forPrecondition()::exceptionForIllegalState));
  }

  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception returned by {@link ExceptionComposer#forPrecondition()#preconditionViolationException(String)}
   * is thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check if `value` satisfies.
   * @param <T>   The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  default <T> T require(T value, Predicate<? super T> cond) {
    return require(value, cond, msg -> this.exceptionComposer().forPrecondition().exceptionForGeneralViolation(msg));
  }

  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception created by `exceptionFactory` is thrown.
   *
   * @param value            A value to be checked.
   * @param cond             A condition to check if `value` satisfies.
   * @param exceptionFactory A function to create an exception thrown when `cond`
   *                         is not satisfied by `value`.
   * @param <T>              The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  default <T> T require(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionFactory) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionFactory));
  }

  default <T> T validate(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionFactory) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForValidation, ExceptionFactory.from(exceptionFactory));
  }

  default <T> T ensureNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(exceptionComposer().forPostCondition()::exceptionForNonNullViolation));
  }

  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(exceptionComposer().forPostCondition()::exceptionForIllegalState));
  }

  default <T> T ensure(T value, Predicate<? super T> cond) {
    return ensure(value, cond, msg -> this.exceptionComposer().forPostCondition().exceptionForGeneralViolation(msg));
  }

  default <T> T ensure(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionComposer) {
    return checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(exceptionComposer));
  }

  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, ExceptionFactory.from(exceptionComposer().forInvariantCondition()::exceptionForGeneralViolation));
  }

  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPrecondition, ExceptionFactory.from(exceptionComposer().forPrecondition()::exceptionForGeneralViolation));
  }

  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForPostcondition, ExceptionFactory.from(exceptionComposer().forPostCondition()::exceptionForGeneralViolation));
  }

  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::testFailedException);
  }

  @SuppressWarnings("RedundantTypeArguments")
  // Necessary to suppress compilation failure.
  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::<RuntimeException>testSkippedException);
  }

  default <T> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, Throwable> exceptionFactory) {
    return checkValueAndThrowIfFails(value, cond, messageComposer, explanation -> exceptionFactory.apply(explanation.toString()));
  }

  default <T> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<Throwable> exceptionFactory) {
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
      final ExceptionComposer.ForPrecondition forPrecondition = new ExceptionComposer.ForPrecondition() {
        @Override
        public Throwable exceptionForIllegalArgument(String message) {
          return new IllegalArgumentException(message);
        }
      };
      final ExceptionComposer.ForInvariantCondition forInvariantCondition = new ExceptionComposer.ForInvariantCondition() {
      };
      final ExceptionComposer.ForPostCondition forPostCondition = new ExceptionComposer.ForPostCondition() {
      };
      final ExceptionComposer.ForValidation forValidation = new ExceptionComposer.ForValidation() {
      };
      if (isJunit4(properties))
        return ExceptionComposer.createExceptionComposerForJUnit4(forPrecondition, forInvariantCondition, forPostCondition, forValidation, assertionProvider.reportComposer());
      return ExceptionComposer.createExceptionComposerForOpentest4J(forPrecondition, forInvariantCondition, forPostCondition, forValidation, assertionProvider.reportComposer());
    }

    default boolean isJunit4(Properties properties) {
      return true;
    }

    default ReportComposer createReportComposer() {
      return ReportComposer.createDefaultReportComposer();
    }

    default MessageComposer createMessageComposer() {
      return MessageComposer.createDefaultMessageComposer();
    }
  }
}
