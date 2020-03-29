package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.functions.Predicates;

import java.util.function.Predicate;

public interface AssertionProviderBase<AE extends Throwable> extends AssertionProvider<AE> {
  @Override
  default <T> T requireNonNull(T value) {
    checkNotNull(value);
    return value;
  }

  @Override
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    checkArgument(value, cond);
    return value;
  }

  @Override
  default <T> T requireState(T value, Predicate<? super T> cond) {
    checkState(value, cond);
    return value;
  }

  @Override
  default <T> T require(T value, Predicate<? super T> cond) {
    checkPrecondition(value, cond);
    return value;
  }

  @Override
  default <T> T validate(T value, Predicate<? super T> cond) throws AE {
    validation(value, cond);
    return value;
  }

  @Override
  default <T> T ensureNonNull(T value) {
    checkNotNull(value);
    return value;
  }

  @Override
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    checkState(value, cond);
    return value;
  }

  @Override
  default <T> T ensure(T value, Predicate<? super T> cond) {
    checkPostcondition(value, cond);
    return value;
  }

  @Override
  default <T> void validation(T value, Predicate<? super T> cond) throws AE {
    if (!cond.test(value))
      throw applicationException(composeMessageForValidation(value, cond));
  }

  default <T> void checkNotNull(T value) {
    Predicate<T> cond = Predicates.isNotNull();
    if (!cond.test(value))
      throw new NullPointerException(composeMessageForAssertion(value, cond));
  }

  default <T> void checkArgument(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalArgumentException(composeMessageForPrecondition(value, cond));
  }

  default <T> void checkState(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalStateException(composeMessageForPrecondition(value, cond));
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

  <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate);

  <T> String composeMessageForValidation(T t, Predicate<? super T> predicate);

  AE applicationException(String message) throws AE;

}
