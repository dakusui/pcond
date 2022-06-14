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

  /**
   * Returns an exception composer, which is responsible for creating an exception
   * object of an appropriate type for a context.
   *
   * @return An exception composer.
   */
  ExceptionComposer exceptionComposer();

  /**
   * Returns a message composer, which is responsible for composing an appropriate message for
   * a context.
   *
   * @return A message composer.
   */
  MessageComposer messageComposer();

  /**
   * Returns a report composer, which is responsible for composing an appropriate "report" for
   * a context.
   *
   * @return A report composer
   */
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
   * Otherwise, an exception created by `forValidate.exceptionForGeneralViolation()`
   * will be thrown.
   * This method is intended to be used by {@link com.github.dakusui.pcond.Validations#validate(Object, Predicate, Function)}
   * method.
   *
   * @param value       The value to be checked.
   * @param cond        A condition to validate the `value`.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validate(T value, Predicate<? super T> cond, ExceptionComposer.ForValidate forValidate) {
    return validate(value, cond, forValidate::exceptionForGeneralViolation);
  }

  /**
   * Validates the given `value`.
   * If the value is not `null`, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForGeneralViolation()`
   * will be thrown.
   * This method is intended to be used by {@link com.github.dakusui.pcond.Validations#validateNonNull(Object)}
   * method.
   *
   * @param value       The value to be checked.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validateNonNull(T value, ExceptionComposer.ForValidate forValidate) {
    return validate(value, Predicates.isNotNull(), forValidate::exceptionForNonNullViolation);
  }

  /**
   * Validates the given argument variable `value`.
   * If the value satisfies a condition `cond` for checking an argument variable, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForIllegalArgument()`
   * will be thrown.
   * This method is intended to be used by {@link com.github.dakusui.pcond.Validations#validateArgument(Object, Predicate)}
   * method.
   *
   * @param value       The value to be checked.
   * @param cond        A condition to validate the `value`.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validateArgument(T value, Predicate<? super T> cond, ExceptionComposer.ForValidate forValidate) {
    return validate(value, cond, forValidate::exceptionForIllegalArgument);
  }

  /**
   * Validates the given state variable `value`.
   * If the value satisfies a condition `cond` for checking a state, the value itself will be returned.
   * Otherwise, an exception created by `forValidate.exceptionForIllegalState()`
   * will be thrown.
   * This method is intended to be used by {@link com.github.dakusui.pcond.Validations#validateState(Object, Predicate)}
   * method.
   *
   * @param value       The value to be checked.
   * @param cond        A condition to validate the `value`.
   * @param forValidate An exception composer for "validate" methods.
   * @param <T>         The type of the value.
   * @return The value itself.
   */
  default <T> T validateState(T value, Predicate<? super T> cond, ExceptionComposer.ForValidate forValidate) {
    return validate(value, cond, forValidate::exceptionForIllegalState);
  }

  /**
   * Validates the given variable `value`.
   * If the value satisfies a condition `cond`, the value itself will be returned.
   * Otherwise, an exception created by `exceptionFactory` will be thrown.
   * This method is intended to be used by {@link com.github.dakusui.pcond.Validations#validate(Object, Predicate, Function)}
   * method.
   *
   * @param value            The value to be checked.
   * @param cond             A condition to validate the `value`.
   * @param exceptionFactory A function to create an exception when the `cond` is not satisfied.
   * @param <T>              The type of the value.
   * @return The value itself.
   */
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
    int summarizedStringLength();

    boolean useEvaluator();

    ExceptionComposer createExceptionComposer(ReportComposer reportComposer);

    MessageComposer messageComposer();

    ReportComposer reportComposer();

    ReportComposer createReportComposer();

    MessageComposer createMessageComposer();

    class Builder {
      Class<? extends AssertionProvider> assertionProvider;
      Properties                         properties;
      boolean                            useEvaluator;
      int                                summarizedStringLength;


      Function<ReportComposer, ExceptionComposer> exceptionComposerFactory;
      MessageComposer                             messageComposer;
      ReportComposer                              reportComposer;

      public Builder(Properties properties) {
        this.assertionProviderClass(AssertionProviderImpl.class)
            .useEvaluator(useEvaluator(assertionProvider, properties))
            .summarizedStringLength(40)
            .exceptionComposerFactory(new Function<ReportComposer, ExceptionComposer>() {
              final ExceptionComposer.ForPrecondition forPrecondition = IllegalArgumentException::new;
              final ExceptionComposer.ForPostCondition forPostCondition = new ExceptionComposer.ForPostCondition() {
              };
              final ExceptionComposer.ForValidate forValidate = new ExceptionComposer.ForValidate() {
              };
              final ExceptionComposer.ForAssertion forAssertion = new ExceptionComposer.ForAssertion() {
              };

              @Override
              public ExceptionComposer apply(ReportComposer reportComposer) {
                if (isJunit4())
                  return ExceptionComposer.createExceptionComposerForJUnit4(forPrecondition, forPostCondition, forValidate, forAssertion, reportComposer);
                return ExceptionComposer.createExceptionComposerForOpentest4J(forPrecondition, forPostCondition, forValidate, forAssertion, reportComposer);
              }

              private boolean isJunit4() {
                return true;
              }
            })
            .messageComposer(MessageComposer.createDefaultMessageComposer())
            .reportComposer(ReportComposer.createDefaultReportComposer())
            .properties(properties);
      }

      public Builder properties(Properties properties) {
        this.properties = properties;
        return this;
      }

      public Builder assertionProviderClass(Class<? extends AssertionProvider> assertionProvider) {
        this.assertionProvider = assertionProvider;
        return this;
      }

      public Builder useEvaluator(boolean useEvaluator) {
        this.useEvaluator = useEvaluator;
        return this;
      }

      public Builder summarizedStringLength(int summarizedStringLength) {
        this.summarizedStringLength = summarizedStringLength;
        return this;
      }

      public Builder exceptionComposerFactory(Function<ReportComposer, ExceptionComposer> exceptionComposerFactory) {
        this.exceptionComposerFactory = exceptionComposerFactory;
        return this;
      }

      public Builder messageComposer(MessageComposer messageComposer) {
        this.messageComposer = messageComposer;
        return this;
      }

      public Builder reportComposer(ReportComposer reportComposer) {
        this.reportComposer = reportComposer;
        return this;
      }

      private boolean useEvaluator(Class<?> myClass, Properties properties) {
        return Boolean.parseBoolean(properties.getProperty(myClass.getName() + ".useEvaluator", "true"));
      }

      public Configuration build() {
        return new Configuration() {

          @Override
          public int summarizedStringLength() {
            return Builder.this.summarizedStringLength;
          }

          @Override
          public boolean useEvaluator() {
            return Builder.this.useEvaluator;
          }

          public ExceptionComposer createExceptionComposer(ReportComposer reportComposer) {
            return Builder.this.exceptionComposerFactory.apply(reportComposer);
          }

          @Override
          public MessageComposer messageComposer() {
            return Builder.this.messageComposer;
          }

          @Override
          public ReportComposer reportComposer() {
            return Builder.this.reportComposer;
          }

          @Override
          public ReportComposer createReportComposer() {
            return Builder.this.reportComposer;
          }

          @Override
          public MessageComposer createMessageComposer() {
            return Builder.this.messageComposer;
          }
        };
      }
    }
  }
}
