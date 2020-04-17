package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.functions.Evaluator;
import com.github.dakusui.pcond.functions.Predicates;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AssertionProviderBase<AE extends Exception> extends AssertionProvider<AE> {
  @Override
  default <T> T requireNonNull(T value) {
    return checkValue(value, Predicates.isNotNull(), this::composeMessageForPrecondition, NullPointerException::new);
  }

  @Override
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    return checkValue(value, cond, this::composeMessageForPrecondition, IllegalArgumentException::new);
  }

  @Override
  default <T> T requireState(T value, Predicate<? super T> cond) {
    return checkValue(value, cond, this::composeMessageForPrecondition, IllegalStateException::new);
  }

  @Override
  default <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E {
    return require(value, cond, this.<E>exceptionComposerForPrecondition());
  }

  @Override
  default <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValue(value, cond, this::composeMessageForPrecondition, exceptionComposer);
  }

  @Override
  default <T> T validate(T value, Predicate<? super T> cond) throws AE {
    return validate(value, cond, this::applicationException);
  }

  @Override
  default <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValue(value, cond, this::composeMessageForValidation, exceptionComposer);
  }


  @Override
  default <T> T ensureNonNull(T value) {
    return checkValue(value, Predicates.isNotNull(), this::composeMessageForPostcondition, NullPointerException::new);
  }

  @Override
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return checkValue(value, cond, this::composeMessageForPostcondition, IllegalStateException::new);
  }

  @Override
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E {
    return ensure(value, cond, this.<E>exceptionComposerForPostcondition());
  }

  @Override
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValue(value, cond, this::composeMessageForPostcondition, exceptionComposer);
  }

  @Override
  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForPrecondition(value, cond));
  }

  @Override
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForPostcondition(value, cond));
  }

  @Override
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForAssertion(value, cond));
  }

  @SuppressWarnings("unchecked")
  default <E extends Exception> Function<String, E> exceptionComposerForPrecondition() {
    return message -> (E) new PreconditionViolationException(message);
  }

  @SuppressWarnings("unchecked")
  default <E extends Exception> Function<String, E> exceptionComposerForPostcondition() {
    return message -> (E) new PostconditionViolationException(message);
  }

  <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate);

  <T> String composeMessageForValidation(T t, Predicate<? super T> predicate);

  AE applicationException(String message);

  static <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    if (isInDetailMode() && cond instanceof Evaluable) {
      Evaluator.Result result = Evaluator.evaluate(value, (Evaluable<? super T>) cond);
      if (result.result())
        return value;
      StringBuilder b = new StringBuilder(messageComposer.apply(value, cond));
      b.append(String.format("%n"));
      result.formatTo(b);
      throw exceptionComposer.apply(b.toString());
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(messageComposer.apply(value, cond));
      return value;
    }
  }

  static boolean isInDetailMode() {
    return true;
  }
}
