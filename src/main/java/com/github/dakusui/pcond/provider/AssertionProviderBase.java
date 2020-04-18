package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.functions.Evaluable;
import com.github.dakusui.pcond.functions.Evaluator;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.spaces;
import static java.util.stream.Collectors.joining;

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
    checkValue(value, cond, this::composeMessageForPrecondition, AssertionError::new);
  }

  @Override
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValue(value, cond, this::composeMessageForPostcondition, AssertionError::new);
  }

  @Override
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValue(value, cond, this::composeMessageForAssertion, AssertionError::new);
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

  default <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E {
    if (useEvaluator() && cond instanceof Evaluable) {
      @SuppressWarnings("unchecked")
      Evaluator.Result result = Evaluator.evaluate(value, (Evaluable<? super T>) cond);
      if (result.result())
        return value;
      String b = messageComposer.apply(value, cond) + String.format("%n") +
          result.stream()
              .map(AssertionProviderBase::formatRecord)
              .collect(joining(String.format("%n")));
      throw exceptionComposer.apply(b);
    } else {
      if (!cond.test(value))
        throw exceptionComposer.apply(messageComposer.apply(value, cond));
      return value;
    }
  }

  static String formatRecord(Evaluator.Result.Record r) {
    return String.format("%-" + evaluableNameWidth() + "s -> %s",
        String.format("%s%s",
            spaces(r.level() * 2),
            String.format("%s%s", r.name(), r.input().map(InternalUtils::formatObject).map(v -> "(" + v + ")").orElse(""))),
        r.output().map(InternalUtils::formatObject).orElse("<<OUTPUT MISSING>>"));
  }

  static int evaluableNameWidth() {
    return 58;
  }

  default boolean useEvaluator() {
    return true;
  }
}
