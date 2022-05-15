package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.provider.impls.BaseAssertionProvider;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

public interface AssertionProviderBase<AE extends Exception> extends AssertionProvider<AE> {
  @Override
  default <T> T requireNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this::composeMessageForPrecondition, ExceptionComposer.from(NullPointerException::new));
  }

  @Override
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this::composeMessageForPrecondition, ExceptionComposer.from(IllegalArgumentException::new));
  }

  @Override
  default <T> T requireState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this::composeMessageForPrecondition, ExceptionComposer.from(IllegalStateException::new));
  }

  @Override
  default <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E {
    return require(value, cond, this.<E>exceptionComposerForPrecondition());
  }

  @Override
  default <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this::composeMessageForPrecondition, ExceptionComposer.from(exceptionComposer));
  }

  @Override
  default <T> T validate(T value, Predicate<? super T> cond) throws AE {
    return validate(value, cond, this::applicationException);
  }

  @Override
  default <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this::composeMessageForValidation, ExceptionComposer.from(exceptionComposer));
  }


  @Override
  default <T> T ensureNonNull(T value) {
    return checkValueAndThrowIfFails(value, Predicates.isNotNull(), this::composeMessageForPostcondition, ExceptionComposer.from(NullPointerException::new));
  }

  @Override
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    return checkValueAndThrowIfFails(value, cond, this::composeMessageForPostcondition, ExceptionComposer.from(IllegalStateException::new));
  }

  @Override
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E {
    return ensure(value, cond, this.<E>exceptionComposerForPostcondition());
  }

  @Override
  default <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return checkValueAndThrowIfFails(value, cond, this::composeMessageForPostcondition, ExceptionComposer.from(exceptionComposer));
  }

  @Override
  default <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this::composeMessageForPrecondition, AssertionError::new);
  }

  @Override
  default <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this::composeMessageForPostcondition, AssertionError::new);
  }

  @Override
  default <T> void checkInvariant(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this::composeMessageForAssertion, AssertionError::new);
  }

  @Override
  default <T> void assertThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this::composeMessageForAssertion, this::<Error>testFailedException);
  }

  @SuppressWarnings("RedundantTypeArguments")
  @Override
  default <T> void assumeThat(T value, Predicate<? super T> cond) {
    checkValueAndThrowIfFails(value, cond, this::composeMessageForAssertion, this::<RuntimeException>testSkippedException);
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

  <T extends RuntimeException> T testSkippedException(String message);

  default <T extends RuntimeException> T testSkippedException(Explanation explanation) {
    return testSkippedException(explanation.toString());
  }

  <T extends Error> T testFailedException(String message);

  default <T extends Error> T testFailedException(Explanation explanation) {
    return testFailedException(explanation.toString());
  }

  <T, E extends Throwable> T checkValue(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, Function<String, E> exceptionComposer) throws E;

  default <T, E extends Throwable> T checkValueAndThrowIfFails(T value, Predicate<? super T> cond, BiFunction<T, Predicate<? super T>, String> messageComposer, ExceptionComposer<E> exceptionComposer) throws E {
    return checkValue(value, cond, messageComposer, msg -> exceptionComposer.apply(Explanation.fromMessage(msg)));
  }

  interface ExceptionComposer<E extends Throwable> extends Function<Explanation, E> {
    static <E extends Throwable> ExceptionComposer<E> from(Function<String, E> exceptionComposingFunction) {
      return explanation -> exceptionComposingFunction.apply(explanation.toString());
    }
  }

  class Explanation {
    private final String message;
    private final String expected;
    private final String actual;

    public Explanation(String message, String expected, String actual) {
      this.message = message;
      this.expected = expected;
      this.actual = actual;
    }

    public String message() {
      return this.message;
    }

    public String expected() {
      return this.expected;
    }

    public String actual() {
      return this.actual;
    }

    public String toString() {
      // Did not include "expected" because it is too much overlapping "actual" in most cases.
      return actual != null ?
          format("%s%n%s", message, actual) :
          message;
    }

    public static Explanation fromMessage(String msg) {
      return new Explanation(msg, BaseAssertionProvider.composeReport(null, null, new AtomicInteger(0)), BaseAssertionProvider.composeReport(null, null, new AtomicInteger(0)));
    }
  }
}
