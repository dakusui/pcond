package com.github.dakusui.pcondtest;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.shared.utils.TestBase;
import com.github.dakusui.shared.utils.TestUtils;
import com.github.dakusui.shared.ApplicationException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.github.dakusui.shared.utils.TestUtils.lineAt;
import static com.github.dakusui.shared.TestUtils.validate;
import static org.hamcrest.MatcherAssert.assertThat;

public class WithMessageTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void whenWithMessagePredicateFails_thenPrintDesignedMessage() throws ApplicationException {
    try {
      validate("Value",
          Predicates.withMessage("<Hello, world>",
              Predicates.not(Predicates.alwaysTrue())), ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace(System.out);
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.containsString("Value:'Value' violated: <Hello, world>"));
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("'Value'-><Hello, world>:not"),
              CoreMatchers.containsString("->false")));
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("alwaysTrue"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenNestedWithMessagePredicateFails_thenPrintDesignedMessage() throws ApplicationException {
    try {
      validate("Value",
          Predicates.withMessage("Hello, world", Predicates.not(
              Predicates.withMessage("Always true!", Predicates.alwaysTrue()))),
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace(System.out);
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.containsString("Value:'Value' violated: Hello, world"));
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("'Value'"),
              CoreMatchers.containsString("Hello, world:"),
              CoreMatchers.containsString("not"),
              CoreMatchers.containsString("->false")));
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("Always true!:"),
              CoreMatchers.containsString("alwaysTrue"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }
}
