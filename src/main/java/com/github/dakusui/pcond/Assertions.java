package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

/**
 * @formatter:off
 * Use methods in this class with the ```assert``` statement.
 *
 * [source, java]
 * ----
 * import static com.github.dakusui.pcond.functions.Predicates.isNotNull;
 * import static com.github.dakusui.pcond.Assertions.*
 *
 * public class TestClass {
 *   public void aMethod(Object value) {
 *     assert that(value, isNotNull());
 *   }
 * }
 * ----
 * @formatter:on
 */
@SuppressWarnings("JavaDoc")
public enum Assertions {
  ;

  /**
   * A method to be used for checking a value satisfies a given invariant condition.
   *
   * @param value     A value to be checked.
   * @param predicate An invariant condition to check the {@code value}.
   * @param <T>       The type of {@code value}.
   * @return {@code true}, if the condition given as {@code predicate} is satisfied.
   */
  public static <T> boolean that(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.checkInvariant(value, predicate);
    return trueValue();
  }

  /**
   * A method to be used for checking a value satisfies a given pre-condition.
   *
   * @param value     A value to be checked.
   * @param predicate A pre-condition to check the {@code value}.
   * @param <T>       The type of {@code value}.
   * @return {@code true}, if the condition given as {@code predicate} is satisfied.
   */
  public static <T> boolean precondition(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.checkPrecondition(value, predicate);
    return trueValue();
  }

  /**
   * A method to be used for checking a value satisfies a given post-condition.
   *
   * @param value     A value to be checked.
   * @param predicate A post-condition to check the {@code value}.
   * @param <T>       The type of {@code value}.
   * @return {@code true}, if the condition given as {@code predicate} is satisfied.
   */
  public static <T> boolean postcondition(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.checkPostcondition(value, predicate);
    return trueValue();
  }

  /**
   * This method always return {@code true}.
   * This method is defined in order not to let an IDE report a warning
   * Condition 'that(methodType, Predicates.isNotNull())' is always 'true'
   * for caller codes of methods in this class.
   *
   * @return {@code true}
   */
  @SuppressWarnings("ConstantConditions")
  private static boolean trueValue() {
    return Assertions.class != null;
  }
}
