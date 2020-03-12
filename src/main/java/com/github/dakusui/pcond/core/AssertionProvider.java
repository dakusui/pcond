package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.internals.Exceptions;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public interface AssertionProvider {
  AssertionProvider INSTANCE = createAssertionProvider();

  static AssertionProvider createAssertionProvider() {
    AssertionProvider ret = new Default();
    if (!InternalUtils.isAssertionEnabled())
      ret = new Passthrough();
    return ret;
  }

  <T, E extends Throwable> T requireNonNull(T value) throws E;

  <T, E extends Throwable> T requireArgument(T value, Predicate<? super T> cond) throws E;

  <T, E extends Throwable> T requireState(T value, Predicate<? super T> cond) throws E;

  <T, E extends Throwable> T require(T value, Predicate<? super T> cond) throws E;

  <T, E extends Throwable> T ensureNonNull(T value) throws E;

  <T, E extends Throwable> T ensureState(T value, Predicate<? super T> cond) throws E;

  <T, E extends Throwable> T ensure(T value, Predicate<? super T> cond) throws E;

  boolean shouldStub();

  class Default implements AssertionProvider {
    private static <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
      return String.format("value:%s violated precondition:value %s", formatObject(value), predicate);
    }

    public static <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
      return String.format("value:%s violated postcondition:value %s", formatObject(value), predicate);
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T requireNonNull(T value) {
      return InternalUtils.check(value, Predicates.isNotNull(), Exceptions.nullPointer(Default::composeMessageForPrecondition));
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T requireArgument(T value, Predicate<? super T> cond) {
      return InternalUtils.check(value, cond, Exceptions.illegalArgument(Default::composeMessageForPrecondition));
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T requireState(T value, Predicate<? super T> cond) {
      return InternalUtils.check(value, cond, Exceptions.illegalState(Default::composeMessageForPrecondition));
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T require(T value, Predicate<? super T> cond) {
      return InternalUtils.check(value, cond, (v, p) -> new Error(composeMessageForPrecondition(v, p)));
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T ensureNonNull(T value) {
      return InternalUtils.check(value, Predicates.isNotNull(), Exceptions.nullPointer(Default::composeMessageForPostcondition));
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T ensureState(T value, Predicate<? super T> cond) {
      return InternalUtils.check(value, cond, Exceptions.illegalState(Default::composeMessageForPostcondition));
    }

    @SuppressWarnings("unused")
    @Override
    public <T, E extends Throwable> T ensure(T value, Predicate<? super T> cond) {
      return InternalUtils.check(value, cond, (v, p) -> new Error(composeMessageForPostcondition(v, p)));
    }

    @Override
    public boolean shouldStub() {
      return false;
    }
  }

  @SuppressWarnings("unused")
  class Passthrough implements AssertionProvider {
    @Override
    public <T, E extends Throwable> T requireNonNull(T value) {
      return value;
    }

    @Override
    public <T, E extends Throwable> T requireArgument(T value, Predicate<? super T> cond) {
      return value;
    }

    @Override
    public <T, E extends Throwable> T requireState(T value, Predicate<? super T> cond) {
      return value;
    }

    @Override
    public <T, E extends Throwable> T require(T value, Predicate<? super T> cond) {
      return value;
    }

    @Override
    public <T, E extends Throwable> T ensureNonNull(T value) {
      return value;
    }

    @Override
    public <T, E extends Throwable> T ensureState(T value, Predicate<? super T> cond) {
      return value;
    }

    @Override
    public <T, E extends Throwable> T ensure(T value, Predicate<? super T> cond) {
      return value;
    }

    @Override
    public boolean shouldStub() {
      return true;
    }
  }
}
