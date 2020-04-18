package com.github.dakusui.pcond.provider;

import com.github.dakusui.pcond.internals.InternalUtils;
import com.github.dakusui.pcond.provider.impls.DefaultAssertionProvider;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An interface of a policy for behaviours on 'contract violations'.
 *
 * @param <AE> The type of exception that should be thrown on an application error.
 */
public interface AssertionProvider<AE extends Throwable> {
  /**
   * A constant field that holds the default provider instance.
   */
  AssertionProvider<?> INSTANCE = createAssertionProvider(System.getProperties());

  /**
   * Returns a provider instance created from a given `Properties` object.
   * This method reads the value for the FQCN of this class (`com.github.dakusui.pcond.provider.AssertionProvider`) and creates an instance of a class specified by the value.
   * If the value is not set, this value instantiates an object of `DefaultAssertionProvider` and returns it.
   *
   * @param properties A {@code Properties} object from which an {@code AssertionProvider} is created
   * @return Created provider instance.
   */
  static AssertionProvider<?> createAssertionProvider(Properties properties) {
    String propertyKeyName = AssertionProvider.class.getCanonicalName();
    if (properties.containsKey(propertyKeyName)) {
      return InternalUtils.createInstanceFromClassName(AssertionProvider.class, properties.getProperty(propertyKeyName));
    }
    return new DefaultAssertionProvider(properties);
  }

  /**
   * Checks a value if it is {@code null} or not.
   * If it is not a {@code null}, this method returns the given value itself.
   *
   * @param value The given value.
   * @param <T>   The type of the value.
   * @return The {@code value}.
   */
  <T> T requireNonNull(T value);

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  <T> T requireArgument(T value, Predicate<? super T> cond);

  /**
   * Checks a value if it meets a requirement specified by {@code cond}.
   * If it does, the value itself will be returned.
   *
   * @param value The value to be checked.
   * @param cond  The requirement to check the {@code value}.
   * @param <T>   The type of the value.
   * @return The value.
   */
  <T> T requireState(T value, Predicate<? super T> cond);

  <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E;

  <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> T validate(T value, Predicate<? super T> cond) throws AE;

  <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> T ensureNonNull(T value);

  <T> T ensureState(T value, Predicate<? super T> cond);

  <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E;

  <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E;

  <T> void checkInvariant(T value, Predicate<? super T> cond);

  <T> void checkPrecondition(T value, Predicate<? super T> cond);

  <T> void checkPostcondition(T value, Predicate<? super T> cond);
}
