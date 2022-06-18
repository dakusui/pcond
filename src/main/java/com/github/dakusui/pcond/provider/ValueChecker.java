package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.executionFailure;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

/**
 * An interface of a policy for behaviours on 'contract violations'.
 */
public interface ValueChecker {
  /**
   * A constant field that holds the default provider instance.
   */
  ValueChecker INSTANCE = createAssertionProvider(System.getProperties());

  Configuration configuration();

  /**
   * Returns a provider instance created from a given `Properties` object.
   * This method reads the value for the FQCN of this class (`com.github.dakusui.pcond.provider.AssertionProvider`) and creates an instance of a class specified by the value.
   * If the value is not set, this value instantiates an object of `DefaultAssertionProvider` and returns it.
   *
   * @param properties A {@code Properties} object from which an {@code AssertionProvider} is created
   * @return Created provider instance.
   */
  static ValueChecker createAssertionProvider(Properties properties) {
    return new Impl(properties);
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
    return require(value, Predicates.isNotNull(), configuration().exceptionComposer().forRequire()::exceptionForNonNullViolation);
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
    return require(value, cond, configuration().exceptionComposer().forRequire()::exceptionForIllegalArgument);
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
    return require(value, cond, configuration().exceptionComposer().forRequire()::exceptionForIllegalState);
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
    return require(value, cond, msg -> configuration().exceptionComposer().forRequire().exceptionForGeneralViolation(msg));
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
    return checkValueAndThrowIfFails(
        value,
        cond,
        this.configuration().messageComposer()::composeMessageForPrecondition,
        explanation -> exceptionFactory.apply(explanation.toString()));
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
    return checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForValidation,
        explanation -> exceptionFactory.apply(explanation.toString()));
  }

  /**
   * Checks a value if it is not `null`.
   * If it is not `null`, the value itself will be returned.
   * If it is, an exception created by `configuration().exceptionComposer().forEnsure().exceptionForNonNullViolation()` will be thrown.
   * This method is intended for ensuring a "post-condition".
   *
   * @param value The value to be checked.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T ensureNonNull(T value) {
    return ensure(value, Predicates.isNotNull(), configuration().exceptionComposer().forEnsure()::exceptionForNonNullViolation);
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   * If it does not, an exception created by `configuration().exceptionComposer().forEnsure().exceptionForIllegalState()` will be thrown.
   * This method is intended for ensuring a "post-condition" of a state.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return ensure(value, cond, configuration().exceptionComposer().forEnsure()::exceptionForIllegalState);
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   * If it does not, an exception created by `configuration().exceptionComposer().forEnsure().exceptionForGeneralViolation()` will be thrown.
   * This method is intended for ensuring a "post-condition".
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  default <T> T ensure(T value, Predicate<? super T> cond) {
    return ensure(value, cond, msg -> configuration().exceptionComposer().forEnsure().exceptionForGeneralViolation(msg));
  }

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   * If it does not, an exception created by `exceptionComposer` will be thrown.
   * This method is intended for ensuring a "post-condition".
   *
   * @param value             The value to be checked.
   * @param cond              The requirement to check the {@code value}.
   * @param exceptionComposer A function to create an exception to be thrown when
   *                          `cond` is not met.
   * @param <T>               The type of the value.
   * @return The value.
   */
  default <T> T ensure(T value, Predicate<? super T> cond, Function<String, Throwable> exceptionComposer) {
    return checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForPostcondition,
        explanation -> exceptionComposer.apply(explanation.toString()));
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
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForAssertion,
        explanation -> configuration().exceptionComposer().forAssert().exceptionInvariantConditionViolation(explanation.toString()));
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
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForPrecondition,
        explanation -> configuration().exceptionComposer().forAssert().exceptionPreconditionViolation(explanation.toString()));
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
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForPostcondition,
        explanation -> configuration().exceptionComposer().forAssert().exceptionPostconditionViolation(explanation.toString()));
  }

  /**
   * Executes a test assertion for a given `value` using a predicate `cond`.
   * If the `cond` is not satisfied by the `value`, an exception created by `configuration().messageComposer().composeMessageForAssertion()`
   * will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A predicate to check a given `value`.
   * @param <T>   The type of the `value`.
   */
  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForAssertion,
        explanation -> configuration().exceptionComposer().forAssertThat().testFailedException(explanation, configuration().reportComposer()));
  }

  /**
   * Executes a test assumption check for a given `value` using a predicate `cond`.
   * If the `cond` is not satisfied by the `value`, an exception created by `configuration().messageComposer().composeMessageForAssertion()`
   * will be thrown.
   *
   * @param value A value to be checked.
   * @param cond  A predicate to check a given `value`.
   * @param <T>   The type of the `value`.
   */
  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(
        value,
        cond,
        configuration().messageComposer()::composeMessageForAssertion,
        explantion -> configuration().exceptionComposer().forAssertThat().testSkippedException(explantion, configuration().reportComposer()));
  }

  @SuppressWarnings("unchecked")
  default <T> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionFactory<Throwable> exceptionFactory) {
    if (this.configuration().useEvaluator() && cond instanceof Evaluable) {
      Evaluator evaluator = Evaluator.create();
      try {
        ((Evaluable<T>) cond).accept(value, evaluator);
      } catch (Error error) {
        throw error;
      } catch (Throwable t) {
        String message = format("An exception(%s) was thrown during evaluation of value: %s: %s", t, value, cond);
        throw executionFailure(configuration().reportComposer().composeExplanation(message, evaluator.resultEntries(), t), t);
      }
      if (evaluator.resultValue())
        return value;
      List<Evaluator.Entry> entries = evaluator.resultEntries();//result.entries;
      throw createException(exceptionFactory, configuration().reportComposer().composeExplanation(messageComposer.apply(value, cond), entries, null));
    } else {
      if (!cond.test(value))
        throw createException(exceptionFactory, configuration().reportComposer().composeExplanation(messageComposer.apply(value, cond), emptyList(), null));
      return value;
    }
  }

  static RuntimeException createException(ExceptionFactory<?> exceptionFactory, Explanation explanation) {
    Throwable t = exceptionFactory.apply(explanation);
    if (t instanceof Error)
      throw (Error) t;
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;
    throw new AssertionError(format("Checked exception(%s) cannot be used for validation.", t.getClass()), t);
  }

  interface Configuration {
    int summarizedStringLength();

    boolean useEvaluator();

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

    /**
     * Returns an exception composer, which is responsible for creating an exception
     * object of an appropriate type for a context.
     *
     * @return An exception composer.
     */
    ExceptionComposer exceptionComposer();

    enum Utils {
      ;

      static Configuration configure(Properties properties) {
        return new Builder()
            .useEvaluator(Boolean.parseBoolean(properties.getProperty( "useEvaluator", "true")))
            .exceptionComposerForRequire(instantiate(ExceptionComposer.ForPrecondition.class, properties.getProperty( "exceptionComposerFactory", "com.github.dakusui.pcond.provider.ExceptionComposer.ForPrecondition.Default")))
            .exceptionComposerForEnsure(instantiate(ExceptionComposer.ForPostCondition.class, properties.getProperty( "exceptionComposerFactory", "com.github.dakusui.pcond.provider.ExceptionComposer.ForPostCondition.Default")))
            .defaultExceptionComposerForValidate(instantiate(ExceptionComposer.ForValidate.class, properties.getProperty( "exceptionComposerFactory", "com.github.dakusui.pcond.provider.ExceptionComposer.ForValidate.Default")))
            .exceptionComposerForAssert(instantiate(ExceptionComposer.ForAssertion.class, properties.getProperty( "exceptionComposerFactory", "com.github.dakusui.pcond.provider.ExceptionComposer.ForAssertion.Default")))
            .exceptionComposerForAssertThat(instantiate(ExceptionComposer.ForTestAssertion.class, properties.getProperty( "exceptionComposerFactory", "com.github.dakusui.pcond.provider.ExceptionComposer.ForTestAssertion.JUnit4")))
            .messageComposer(instantiate(MessageComposer.class, properties.getProperty( "reportComposer", "com.github.dakusui.pcond.provider.MessageComposer.Default")))
            .reportComposer(instantiate(ReportComposer.class, properties.getProperty( "messageComposer", "com.github.dakusui.pcond.provider.ReportComposer.Default")))
            .build();
      }

      static <E> E instantiate(Class<E> baseClass, String className) {
        try {
          return (E)Class.forName(className).newInstance();
        } catch (InstantiationException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }

    }

    class Builder {
      boolean useEvaluator;
      int     summarizedStringLength;


      MessageComposer messageComposer;
      ReportComposer  reportComposer;
      private ExceptionComposer.ForPrecondition  exceptionComposerForPrecondition;
      private ExceptionComposer.ForPostCondition exceptionComposerForPostCondition;
      private ExceptionComposer.ForValidate      defaultExceptionComposerForValidate;
      private ExceptionComposer.ForAssertion     exceptionComposerForAssert;
      private ExceptionComposer.ForTestAssertion exceptionComposerForAssertThat;

      public Builder() {
        this.useEvaluator(true)
            .summarizedStringLength(40)
            .exceptionComposerForRequire(new ExceptionComposer.ForPrecondition.Default())
            .exceptionComposerForEnsure(new ExceptionComposer.ForPostCondition.Default())
            .defaultExceptionComposerForValidate(new ExceptionComposer.ForValidate.Default())
            .exceptionComposerForAssert(new ExceptionComposer.ForAssertion.Default())
            .exceptionComposerForAssertThat(new ExceptionComposer.ForTestAssertion.JUnit4())
            .messageComposer(new MessageComposer.Default())
            .reportComposer(new ReportComposer.Default());
      }


      public Builder useEvaluator(boolean useEvaluator) {
        this.useEvaluator = useEvaluator;
        return this;
      }

      public Builder summarizedStringLength(int summarizedStringLength) {
        this.summarizedStringLength = summarizedStringLength;
        return this;
      }

      public Builder exceptionComposerForRequire(ExceptionComposer.ForPrecondition exceptionComposerForPrecondition) {
        this.exceptionComposerForPrecondition = exceptionComposerForPrecondition;
        return this;
      }

      public Builder exceptionComposerForEnsure(ExceptionComposer.ForPostCondition exceptionComposerForPostCondition) {
        this.exceptionComposerForPostCondition = exceptionComposerForPostCondition;
        return this;
      }

      public Builder defaultExceptionComposerForValidate(ExceptionComposer.ForValidate exceptionComposerForValidate) {
        this.defaultExceptionComposerForValidate = exceptionComposerForValidate;
        return this;
      }

      public Builder exceptionComposerForAssert(ExceptionComposer.ForAssertion exceptionComposerForAssert) {
        this.exceptionComposerForAssert = exceptionComposerForAssert;
        return this;
      }

      public Builder exceptionComposerForAssertThat(ExceptionComposer.ForTestAssertion exceptionComposerForAssertThat) {
        this.exceptionComposerForAssertThat = exceptionComposerForAssertThat;
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

      public Configuration build() {
        return new Configuration() {
          private final ExceptionComposer exceptionComposer = new ExceptionComposer.Impl(
              exceptionComposerForPrecondition,
              exceptionComposerForPostCondition,
              defaultExceptionComposerForValidate,
              exceptionComposerForAssert,
              exceptionComposerForAssertThat
          );

          @Override
          public int summarizedStringLength() {
            return Builder.this.summarizedStringLength;
          }

          @Override
          public boolean useEvaluator() {
            return Builder.this.useEvaluator;
          }

          /**
           * Returns an exception composer, which is responsible for creating an exception
           * object of an appropriate type for a context.
           *
           * @return An exception composer.
           */
          public ExceptionComposer exceptionComposer() {
            return this.exceptionComposer;
          }


          @Override
          public MessageComposer messageComposer() {
            return Builder.this.messageComposer;
          }

          @Override
          public ReportComposer reportComposer() {
            return Builder.this.reportComposer;
          }
        };
      }
    }
  }

  class Impl implements ValueChecker {

    private final Configuration configuration;

    public Impl(Properties properties) {
      this.configuration = new Configuration.Builder()
          .useEvaluator(true)
          .build();
    }

    @Override
    public Configuration configuration() {
      return this.configuration;
    }
  }
}
