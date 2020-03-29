package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.impls.DefaultAssertionProvider;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AssertionProvider<AE extends Throwable> {
  AssertionProvider<?> INSTANCE = createAssertionProvider(System.getProperties());

  static AssertionProvider<?> createAssertionProvider(Properties properties) {
    String propertyKeyName = AssertionProvider.class.getCanonicalName();
    if (properties.containsKey(propertyKeyName)) {
      return InternalUtils.createInstanceFromClassName(AssertionProvider.class, properties.getProperty(propertyKeyName));
    }
    return new DefaultAssertionProvider();
  }

  <T> T requireNonNull(T value);

  <T> T requireArgument(T value, Predicate<? super T> cond);

  <T> T requireState(T value, Predicate<? super T> cond);

  <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E;

  <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> T validate(T value, Predicate<? super T> cond) throws AE;

  <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> T ensureNonNull(T value);

  <T> T ensureState(T value, Predicate<? super T> cond);

  <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E;

  <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> void validation(T value, Predicate<? super T> cond) throws AE;

  <T> void checkInvariant(T value, Predicate<? super T> cond);

  <T> void checkPrecondition(T value, Predicate<? super T> cond);

  <T> void checkPostcondition(T value, Predicate<? super T> cond);
}
