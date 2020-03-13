package com.github.dakusui.pcond.internals;

import com.github.dakusui.pcond.functions.MessageComposer;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public enum Exceptions {
  ;

  public static <T> BiFunction<T, Predicate<? super T>, IllegalStateException> nullPointer(BiFunction<T, Predicate<? super T>, String> messageComposer) {
    return (t, predicate) -> {
      throw new NullPointerException(messageComposer.apply(t, predicate));
    };
  }

  public static <T> BiFunction<T, Predicate<? super T>, IllegalStateException> illegalState(BiFunction<T, Predicate<? super T>, String> messageComposer) {
    return (t, predicate) -> {
      throw new IllegalStateException(messageComposer.apply(t, predicate));
    };
  }

  public static <T> BiFunction<T, Predicate<? super T>, IllegalArgumentException> illegalArgument(MessageComposer<T> messageComposer) {
    return (t, predicate) -> {
      throw new IllegalArgumentException(messageComposer.apply(t, predicate));
    };
  }

  public static InternalException wrap(String message, Throwable cause) {
    throw new InternalException(message, cause);
  }

  public static InternalException wrapIfNecessary(Throwable cause) {
    if (cause instanceof Error)
      throw (Error) cause;
    if (cause instanceof RuntimeException)
      throw (RuntimeException) cause;
    throw wrap(cause.getMessage(), cause);
  }

  public static class InternalException extends RuntimeException {
    InternalException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
