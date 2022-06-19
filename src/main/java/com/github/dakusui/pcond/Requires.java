package com.github.dakusui.pcond;

import com.github.dakusui.pcond.valuechecker.ValueChecker;

import java.util.function.Predicate;

/**
 * A class that offers entry-points for checking "preconditions" with a normal
 * conditional statement of Java language.
 * That is, `if` or ternary operator, not an `assert` statement.
 * This means, the user of your product is not able to disable the checks you
 * write at runtime, unlike ones written using `assert` statements.
 *
 * The author of library thinks these methods should be used in "public facing"
 * context when the provider of the value to be examined introduced
 * a bug, which makes the condition `false`.
 *
 * For instance, it is an intended use-case, where you call `requireArgument` for
 * a value passed by the caller and the user is expected to make the value satisfy
 * a certain requirement.
 */
public enum Requires {
  ;

  public static <T> T requireNonNull(T value) {
    return ValueChecker.INSTANCE.requireNonNull(value);
  }

  public static <T> T requireArgument(T value, Predicate<? super T> cond) {
    return ValueChecker.INSTANCE.requireArgument(value, cond);
  }

  public static <T> T requireState(T value, Predicate<? super T> cond) {
    return ValueChecker.INSTANCE.requireState(value, cond);
  }

  @SuppressWarnings("RedundantThrows")
  public static <T, E extends Throwable> T require(
      T value,
      Predicate<? super T> cond) throws E {
    return ValueChecker.INSTANCE.require(value, cond);
  }

}
