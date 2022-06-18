package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.ValueChecker;
import com.github.dakusui.pcond.provider.ExceptionComposer;

import java.util.function.Function;
import java.util.function.Predicate;

public enum Validations {
  ;
  static final Validator VALIDATOR = createValidator(ValueChecker.INSTANCE.configuration().exceptionComposer().defaultForValidate());

  public static <T> T validate(T value, Predicate<? super T> cond) {
    return VALIDATOR.validate(value, cond);
  }

  public static <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) {
    return VALIDATOR.validate(value, cond, exceptionFactory);
  }

  public static <T> T validateNonNull(T value) {
    return VALIDATOR.validateNonNull(value);
  }

  public static <T> T validateArgument(T value, Predicate<? super T> cond) {
    return VALIDATOR.validateArgument(value, cond);
  }

  public static <T> T validateState(T value, Predicate<? super T> cond) {
    return VALIDATOR.validateState(value, cond);
  }

  public static Validator createValidator(ExceptionComposer.ForValidate exceptionComposerForValidate) {
    return Validator.create(exceptionComposerForValidate);
  }

  interface Validator {
    ExceptionComposer.ForValidate exceptionComposerForValidate();


    default <T> T validate(T value, Predicate<? super T> cond) {
      return ValueChecker.INSTANCE.validate(value, cond, exceptionComposerForValidate());
    }

    default <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) {
      return ValueChecker.INSTANCE.validate(value, cond, exceptionFactory::apply);
    }

    default <T> T validateNonNull(T value) {
      return ValueChecker.INSTANCE.validateNonNull(value, exceptionComposerForValidate());
    }

    default <T> T validateArgument(T value, Predicate<? super T> cond) {
      return ValueChecker.INSTANCE.validateArgument(value, cond, exceptionComposerForValidate());
    }

    default <T> T validateState(T value, Predicate<? super T> cond) {
      return ValueChecker.INSTANCE.validateState(value, cond, exceptionComposerForValidate());
    }

    static Validator create(ExceptionComposer.ForValidate exceptionComposerForValidate) {
      return () -> exceptionComposerForValidate;
    }
  }
}
