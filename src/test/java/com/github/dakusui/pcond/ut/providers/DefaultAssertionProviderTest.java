package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.provider.impls.DefaultAssertionProvider;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Objects;
import java.util.Properties;

import static com.github.dakusui.pcond.functions.Functions.length;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static com.github.dakusui.pcond.utils.TestUtils.numLines;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
          CoreMatchers.startsWith("||"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.startsWith("  isEqualTo[\"hello\"](\"Hello\")"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 3), allOf(
          CoreMatchers.startsWith("  isEqualTo[\"HELLO\"](\"Hello\")"),
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
      assertThat(lineAt(e.getMessage(), 1), allOf(
          CoreMatchers.startsWith("length"),
          CoreMatchers.containsString("Hello"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("5")

      ));
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.startsWith(">[10]"),
          CoreMatchers.containsString("(5)"),
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


  @Test(expected = IllegalArgumentException.class)
  public void withEvaluator_columns100$whenNull() {
    try {
      createAssertionProvider(nameWidth(useEvaluator(newProperties(), true), 100))
          .requireArgument(null, and(isNotNull(), isEmptyString().negate(), transform(length()).check(gt(10))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(lineAt(e.getMessage(), 1), allOf(
          CoreMatchers.startsWith("&&"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 2), allOf(
          CoreMatchers.startsWith("  &&"),
          CoreMatchers.containsString("->"),
          CoreMatchers.containsString("false")

      ));
      assertThat(lineAt(e.getMessage(), 3), allOf(
          CoreMatchers.startsWith("    isNotNull(null)"),
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
