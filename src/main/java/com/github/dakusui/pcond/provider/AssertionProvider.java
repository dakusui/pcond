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
    return require(value, Predicates.isNotNull(), exceptionComposer().forRequire()::exceptionForNonNullViolation);
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
    return require(value, cond, exceptionComposer().forRequire()::exceptionForIllegalArgument);
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
    return require(value, cond, exceptionComposer().forRequire()::exceptionForIllegalState);
  }

  /**
   * A method to check if a given `value` satisfies a precondition given as `cond`.
   * If the `cond` is satisfied, the `value` itself will be returned.
   * Otherwise, an exception returned by {@link ExceptionComposer#forRequire()#preconditionViolationException(String)}
   * is thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check if `value` satisfies.
   * @param <T>   The of the `value`.
   * @return The `value`, if `cond` is satisfied.
   */
  default <T> T require(T value, Predicate<? super T> cond) {
    return require(value, cond, msg -> this.exceptionComposer().forRequire().exceptionForGeneralViolation(msg));
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
    return checkValue(value, cond, this.messageComposer()::composeMessageForPrecondition, exceptionFactory);
  }

  /**
   * Validates the given `value`.
   * If the value satisfies a condition `cond`, the value itself will be returned.
   * This method is intended to be used by {@link com.github.dakusui.pcond.Validations#validate(Object, Predicate, Function)}
   * method.
   * Otherwise, an exception created by `this#exceptionComposer().exceptionForGeneralViolation()`
   * will be thrown.
   *
   * @param value The value to be checked.
   * @param cond  A condition to validate the `value`.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T validate(T value, Predicate<? super T> cond) {
    return validate(value, cond, exceptionComposer().forValidate()::exceptionForGeneralViolation);
  }

  default <T> T validateNonNull(T value) {
    return validate(value, Predicates.isNotNull(), exceptionComposer().forValidate()::exceptionForNonNullViolation);
  }

  default <T> T validateArgument(T value, Predicate<? super T> cond) {
    return validate(value, cond, exceptionComposer().forValidate()::exceptionForIllegalArgument);
  }

  default <T> T validateState(T value, Predicate<? super T> cond) {
    return validate(value, cond, exceptionComposer().forValidate()::exceptionForIllegalState);
  }

  default <T> T validate(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionFactory) {
    return checkValue(value, cond, this.messageComposer()::composeMessageForValidation, exceptionFactory);
  }

  default <T> T ensureNonNull(T value) {
    return ensure(value, Predicates.isNotNull(), exceptionComposer().forEnsure()::exceptionForNonNullViolation);
  }

  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return ensure(value, cond, exceptionComposer().forEnsure()::exceptionForIllegalState);
  }

  default <T> T ensure(T value, Predicate<? super T> cond) {
    return ensure(value, cond, msg -> this.exceptionComposer().forEnsure().exceptionForGeneralViolation(msg));
  }

  default <T> T ensure(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionComposer) {
    return checkValue(value, cond, this.messageComposer()::composeMessageForPostcondition, exceptionComposer);
  }

  /**
   * A method to check if a `value` satisfies a predicate `cond`.
   *
   * This method is intended to be used by {@link com.github.dakusui.pcond.Assertions#that(Object, Predicate)}.
   * If the condition is not satisfied, an exception created by `this.exceptionComposer().forAssert().exceptionInvariantConditionViolation()`
   * method will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check the `value`.
   * @param <T>   The type of `value`.
   */
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValue(value, cond, this.messageComposer()::composeMessageForAssertion, exceptionComposer().forAssert()::exceptionInvariantConditionViolation);
  }

  /**
   * A method to check if a `value` satisfies a predicate `cond`.
   *
   * This method is intended to be used by {@link com.github.dakusui.pcond.Assertions#precondition(Object, Predicate)}.
   * If the condition is not satisfied, an exception created by `this.exceptionComposer().forAssert().exceptionPreconditionViolation()`
   * method will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check the `value`.
   * @param <T>   The type of `value`.
   */
  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    checkValue(value, cond, this.messageComposer()::composeMessageForPrecondition, exceptionComposer().forAssert()::exceptionPreconditionViolation);
  }

  /**
   * A method to check if a `value` satisfies a predicate `cond`.
   *
   * This method is intended to be used by {@link com.github.dakusui.pcond.Assertions#postcondition(Object, Predicate)} .
   * If the condition is not satisfied, an exception created by `this.exceptionComposer().forAssert().exceptionPostconditionViolation()`
   * method will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A condition to check the `value`.
   * @param <T>   The type of `value`.
   */
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValue(value, cond, this.messageComposer()::composeMessageForPostcondition, exceptionComposer().forAssert()::exceptionPostconditionViolation);
  }

  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::testFailedException);
  }

  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this.messageComposer()::composeMessageForAssertion, this.exceptionComposer()::testSkippedException);
  }

  default <T> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, Throwable> exceptionFactory) {
    return checkValueAndThrowIfFails(value, cond, messageComposer, explanation -> exceptionFactory.apply(explanation.toString()));
  }

  default <T> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<Throwable> exceptionFactory) {
    return checkValue(value, cond, messageComposer, msg -> exceptionFactory.apply(reportComposer().explanationFromMessage(msg)));
  }

  interface Configuration {
    static Configuration create(Class<? extends AssertionProvider> providerClass, Properties properties) {
      return new Configuration() {
        final boolean useEvaluator = useEvaluator(providerClass, properties);

        @Override
        public boolean useEvaluator() {
          return this.useEvaluator;
        }

        public ExceptionComposer createExceptionComposerFromProperties(AssertionProvider assertionProvider) {
          final ExceptionComposer.ForPrecondition forPrecondition = IllegalArgumentException::new;
          final ExceptionComposer.ForPostCondition forPostCondition = new ExceptionComposer.ForPostCondition() {
          };
          final ExceptionComposer.ForValidation forValidation = new ExceptionComposer.ForValidation() {
          };
          final ExceptionComposer.ForAssertion forAssertion = new ExceptionComposer.ForAssertion() {
          };
          if (isJunit4())
            return ExceptionComposer.createExceptionComposerForJUnit4(forPrecondition, forPostCondition, forValidation, forAssertion, assertionProvider.reportComposer());
          return ExceptionComposer.createExceptionComposerForOpentest4J(forPrecondition, forPostCondition, forValidation, forAssertion, assertionProvider.reportComposer());
        }

        private boolean isJunit4() {
          return true;
        }

        private boolean useEvaluator(Class<?> myClass, Properties properties) {
          return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
        }
      };
    }

    default int summarizedStringLength() {
      return 40;
    }

    boolean useEvaluator();

    ExceptionComposer createExceptionComposerFromProperties(AssertionProvider assertionProvider);

    default ReportComposer createReportComposer() {
      return ReportComposer.createDefaultReportComposer();
    }

    default MessageComposer createMessageComposer() {
      return MessageComposer.createDefaultMessageComposer();
    }
  }
}
