package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Validates;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.validator.ApplicationException;
import com.github.dakusui.pcond.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import static com.github.dakusui.pcond.forms.Predicates.alwaysTrue;
import static com.github.dakusui.pcond.forms.Predicates.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class WithMessageTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void test() throws ApplicationException {
    try {
      Validates.validate("Value", Predicates.withMessage("Hello, world", not(alwaysTrue())), ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage().replaceAll(" +", " ").replaceAll("\"", "'"),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Hello, world"),
              CoreMatchers.containsString("! ->false"),
              CoreMatchers.containsString("alwaysTrue->true")
          ));

      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void test2() throws ApplicationException {
    try {
      Validates.validate("Value",
          Predicates.withMessage("Hello, world", not(
              Predicates.withMessage("Always true!", alwaysTrue()))),
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage().replaceAll(" +", " ").replaceAll("\"", "'"),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Hello, world"),
              CoreMatchers.containsString("! ->false"),
              CoreMatchers.containsString("Always true!"),
              CoreMatchers.containsString("alwaysTrue->true")
          ));

      throw e;
    }
  }
}
