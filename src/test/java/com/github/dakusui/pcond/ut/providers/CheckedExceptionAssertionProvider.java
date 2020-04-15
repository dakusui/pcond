package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Predicate;

public class CheckedExceptionAssertionProvider implements AssertionProvider<IOException> {
  @Override
  public <T> T requireNonNull(T value) {
    return null;
  }

  @Override
  public <T> T requireArgument(T value, Predicate<? super T> cond) {
    return null;
  }

  @Override
  public <T> T requireState(T value, Predicate<? super T> cond) {
    return null;
  }

  @Override
  public <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E {
    return null;
  }

  @Override
  public <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return null;
  }

  @Override
  public <T> T validate(T value, Predicate<? super T> cond) throws IOException {
    throw new IOException("FAIL!");
  }

  @Override
  public <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return null;
  }

  @Override
  public <T> T ensureNonNull(T value) {
    return null;
  }

  @Override
  public <T> T ensureState(T value, Predicate<? super T> cond) {
    return null;
  }

  @Override
  public <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E {
    return null;
  }

  @Override
  public <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
    return null;
  }

  @Override
  public <T> void checkInvariant(T value, Predicate<? super T> cond) {

  }

  @Override
  public <T> void checkPrecondition(T value, Predicate<? super T> cond) {

  }

  @Override
  public <T> void checkPostcondition(T value, Predicate<? super T> cond) {

  }
}
