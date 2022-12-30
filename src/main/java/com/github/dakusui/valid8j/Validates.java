package com.github.dakusui.valid8j;

import com.github.dakusui.pcond.validator.ExceptionComposer;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.validator.Validator.INSTANCE;

public enum Validates {
  ;
  static final Validator VALIDATOR = createValidator(com.github.dakusui.pcond.validator.Validator.INSTANCE.configuration().exceptionComposer().defaultForValidate());

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
    return Validates.Validator.create(exceptionComposerForValidate);
  }

  interface Validator {
    ExceptionComposer.ForValidate exceptionComposerForValidate();


    default <T> T validate(T value, Predicate<? super T> cond) {
      return INSTANCE.validate(value, cond, exceptionComposerForValidate());
    }

    default <T, E extends RuntimeException> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionFactory) {
      return INSTANCE.validate(value, cond, exceptionFactory::apply);
    }

    default <T> T validateNonNull(T value) {
      return INSTANCE.validateNonNull(value, exceptionComposerForValidate());
    }

    default <T> T validateArgument(T value, Predicate<? super T> cond) {
      return INSTANCE.validateArgument(value, cond, exceptionComposerForValidate());
    }

    default <T> T validateState(T value, Predicate<? super T> cond) {
      return INSTANCE.validateState(value, cond, exceptionComposerForValidate());
    }

    static Validator create(ExceptionComposer.ForValidate exceptionComposerForValidate) {
      return () -> exceptionComposerForValidate;
    }
  }
}
