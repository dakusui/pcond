package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.functions.Predicates;

import java.util.function.Predicate;

public interface AssertionProviderBase<AE extends Throwable> extends AssertionProvider<AE> {
  @Override
  default <T> T requireNonNull(T value) {
    Predicate<T> cond = Predicates.isNotNull();
    if (!cond.test(value))
      throw new NullPointerException(composeMessageForAssertion(value, cond));
    return value;
  }

  @Override
  default <T> T requireArgument(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalArgumentException(composeMessageForPrecondition(value, cond));
    return value;
  }

  @Override
  default <T> T requireState(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalStateException(composeMessageForPrecondition(value, cond));
    return value;
  }

  @Override
  default <T> T require(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForPrecondition(value, cond));
    return value;
  }

  @Override
  default <T> T validate(T value, Predicate<? super T> cond) throws AE {
    if (!cond.test(value))
      throw applicationException(composeMessageForValidation(value, cond));
    return value;
  }

  @Override
  default <T> T ensureNonNull(T value) {
    Predicate<T> cond = Predicates.isNotNull();
    if (!cond.test(value))
      throw new NullPointerException(composeMessageForAssertion(value, cond));
    return value;
  }

  @Override
  default <T> T ensureState(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalStateException(composeMessageForPrecondition(value, cond));
    return value;
  }

  @Override
  default <T> T ensure(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForPrecondition(value, cond));
    return value;
  }

  @Override
  default <T> boolean validation(T value, Predicate<? super T> cond) throws AE {
    if (!cond.test(value))
      throw applicationException(composeMessageForValidation(value, cond));
    return true;
  }

  default <T> boolean nonNull(T value) {
    Predicate<T> cond = Predicates.isNotNull();
    if (!cond.test(value))
      throw new NullPointerException(composeMessageForAssertion(value, cond));
    return true;
  }

  default  <T> boolean argument(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalArgumentException(composeMessageForPrecondition(value, cond));
    return true;
  }

  default  <T> boolean state(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new IllegalStateException(composeMessageForPrecondition(value, cond));
    return true;
  }

  @Override
  default  <T> boolean precondition(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForPrecondition(value, cond));
    return true;
  }

  @Override
  default  <T> boolean postcondition(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForPostcondition(value, cond));
    return true;
  }

  @Override
  default  <T> boolean that(T value, Predicate<? super T> cond) {
    if (!cond.test(value))
      throw new AssertionError(composeMessageForAssertion(value, cond));
    return true;
  }

  <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate);

  <T> String composeMessageForValidation(T t, Predicate<? super T> predicate);

  AE applicationException(String message) throws AE;

}
