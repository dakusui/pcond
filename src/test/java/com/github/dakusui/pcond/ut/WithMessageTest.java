package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Validates;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.utils.TestBase;
import com.github.dakusui.pcond.utils.TestUtils;
import com.github.dakusui.pcond.validator.ApplicationException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static org.hamcrest.MatcherAssert.assertThat;

public class WithMessageTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void whenWithMessagePredicateFails_thenPrintDesignedMessage() throws ApplicationException {
    try {
      Validates.validate("Value",
          Predicates.withMessage("<Hello, world>",
              Predicates.not(Predicates.alwaysTrue())), ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace(System.out);
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.containsString("Value:'Value' violated: <Hello, world>"));
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.containsString("'Value'-><Hello, world>:not(alwaysTrue)->true"));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenNestedWithMessagePredicateFails_thenPrintDesignedMessage() throws ApplicationException {
    try {
      Validates.validate("Value",
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
              CoreMatchers.containsString("Hello, world"),
              CoreMatchers.containsString("not ->false"),
              CoreMatchers.containsString("Always true!"),
              CoreMatchers.containsString("alwaysTrue->true")));
      assertThat(
          TestUtils.simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Hello, world"),
              CoreMatchers.containsString("not ->false"),
              CoreMatchers.containsString("Always true!"),
              CoreMatchers.containsString("alwaysTrue->true")));
      throw e;
    }
  }

  @Test
  public void whenTrasformingPredicateFails_thenPrintDesignedMessage() {
    Validates.validate("", not(transform(length()).check(lt(100))), ApplicationException::new);
  }

  @Test
  public void abc() {
    Validates.validate("Hello", not(equalTo("Hello")), ApplicationException::new);
  }
}
