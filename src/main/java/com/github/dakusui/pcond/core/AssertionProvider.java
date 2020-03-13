package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.internals.Exceptions;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Properties;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.*;

public interface AssertionProvider {
  AssertionProvider INSTANCE = createAssertionProvider(System.getProperties(), assertFailsWith(false));

  static AssertionProvider createAssertionProvider(Properties properties, boolean assertionEnabled) {
    String propertyKeyName = AssertionProvider.class.getCanonicalName();
    if (properties.containsKey(propertyKeyName)) {
      return InternalUtils.createInstanceFromClassName(AssertionProvider.class, properties.getProperty(propertyKeyName));
    }
    AssertionProvider ret = new Default();
    if (!assertionEnabled)
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

  class Default implements AssertionProvider {
    private static <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
      return String.format("value:%s violated precondition:value %s", formatObject(value), predicate);
    }

    private static <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
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
  }
}
