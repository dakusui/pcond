package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.provider.impls.DefaultAssertionProvider;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Objects;
import java.util.Properties;

import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static com.github.dakusui.pcond.utils.TestUtils.numLines;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

public class DefaultAssertionProviderTest extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void withoutEvaluator_conj_thenFail() {
    try {
      createAssertionProvider(useEvaluator(newProperties(), false))
          .requireArgument("Hello", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertEquals(1, numLines(e.getMessage()));
      throw e;
    }
  }

  @Test
  public void withoutEvaluator_conj_thenPass() {
    createAssertionProvider(useEvaluator(newProperties(), false))
        .requireArgument("Hello World, everyone", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
  }

  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100_conj() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void withEvaluator_nativePredicate() {
    String value = createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
        .requireArgument("Hello", v -> v.equals("Hello"));
    assertThat(value, equalTo("Hello"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_disj_thenFail() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello", or(isEqualTo("hello"), isEqualTo("HELLO")));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(lineAt(e.getMessage(), 1), allOf(
          CoreMatchers.containsString("||"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.containsString("Hello"),
          CoreMatchers.containsString("isEqualTo[\"hello\"]"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 3), allOf(
          CoreMatchers.containsString("  isEqualTo[\"HELLO\"]"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      throw e;
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_transforming_thenFail() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("Hello", transform(Functions.length()).check(Predicates.gt(10)));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(lineAt(e.getMessage(), 1),
          CoreMatchers.containsString("transform")
      );
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.containsString("length"),
          CoreMatchers.containsString("Hello"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("5")

      ));
      assertThat(lineAt(e.getMessage(), 3),
          CoreMatchers.containsString("check")
      );
      assertThat(lineAt(e.getMessage(), 4), allOf(
          CoreMatchers.containsString(">[10]"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      throw e;
    }
  }

  @Test
  public void withEvaluator_disj_thenPass() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("hello", or(isEqualTo("hello"), isEqualTo("HELLO")));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * Expected message:
   * <pre>
   * java.lang.IllegalArgumentException: value:null violated precondition:value ((isNotNull&&!isEmpty)&&length >[10])
   * null -> &&          ->     false
   *           isNotNull -> false
   * </pre>
   */
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100$whenNull() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument(null, and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(lineAt(e.getMessage(), 1), allOf(
          CoreMatchers.containsString("&&"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.containsString("isNotNull"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      throw e;
    }
  }

  /**
   * Expected message:
   * <pre>
   * java.lang.IllegalArgumentException: value:"hello" violated precondition:value (isNotNull&&!isEmpty&&length >[10])
   * "hello" -> &&          ->     false
   *              isNotNull ->   true
   *              !         ->   true
   *                isEmpty -> false
   *              =>        ->   false
   *                length  -> 5
   * 5       ->     >[10]   -> false
   * </pre>
   */
  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100$whenShorterThan10() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument("hello", and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(lineAt(e.getMessage(), 1), allOf(
          CoreMatchers.containsString("&&"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.containsString("  isNotNull"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("true")

      ));
      assertThat(lineAt(e.getMessage(), 3), allOf(
          CoreMatchers.containsString("  !"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("true")

      ));
      assertThat(lineAt(e.getMessage(), 4), allOf(
          CoreMatchers.containsString("  isEmpty"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 5),
          CoreMatchers.containsString("  transform")
      );
      assertThat(lineAt(e.getMessage(), 6), allOf(
          CoreMatchers.containsString("  length"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("5")

      ));
      assertThat(lineAt(e.getMessage(), 7),
          CoreMatchers.containsString("  check")
      );
      assertThat(lineAt(e.getMessage(), 8), allOf(
          CoreMatchers.containsString("  >[10]"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      throw e;
    }
  }

  public DefaultAssertionProvider createAssertionProvider(Properties properties) {
    return new DefaultAssertionProvider(properties);
  }

  public static Properties useEvaluator(Properties properties, boolean useEvaluator) {
    properties.setProperty(DefaultAssertionProvider.class.getName() + ".useEvaluator", Objects.toString(useEvaluator));
    return properties;
  }

  public static Properties nameWidth(Properties properties, int columns) {
    properties.setProperty(DefaultAssertionProvider.class.getName() + ".evaluableNameWidth", Objects.toString(columns));
    return properties;
  }

  public static Properties newProperties() {
    return new Properties();
  }
}
