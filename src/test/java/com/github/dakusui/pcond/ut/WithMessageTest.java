package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.Validations;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.utils.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;

import static com.github.dakusui.pcond.functions.Predicates.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class WithMessageTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void test() throws ApplicationException {
    try {
      Validations.<String, ApplicationException>validate("Value", Predicates.withMessage("Hello, world", not(alwaysTrue())));
    } catch (ApplicationException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage().replaceAll(" +", " ").replaceAll("\"", "'"),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Hello, world -> false"),
              CoreMatchers.containsString("! -> false"),
              CoreMatchers.containsString("alwaysTrue('Value') -> true")
          ));

      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void test2() throws ApplicationException {
    try {
      Validations.<String, ApplicationException>validate("Value",
          Predicates.withMessage("Hello, world", not(
              Predicates.withMessage("Always true!", alwaysTrue()))));
    } catch (ApplicationException e) {
      e.printStackTrace();
      assertThat(
          e.getMessage().replaceAll(" +", " ").replaceAll("\"", "'"),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Hello, world -> false"),
              CoreMatchers.containsString("! -> false"),
              CoreMatchers.containsString("Always true! -> true"),
              CoreMatchers.containsString("alwaysTrue('Value') -> true")
          ));

      throw e;
    }
  }

  @Ignore
  @Test
  public void test3allOf() {
    TestAssertions. assertThat("Hello", Predicates.allOf(not(equalTo("Hello")), equalTo("")));
  }
  @Ignore
  @Test
  public void test3and() {
    TestAssertions. assertThat("Hello", Predicates.and(not(equalTo("Hello")), equalTo("")));
  }
  @Ignore
  @Test
  public void test4() {
    TestAssertions. assertThat("Hello", Predicates.or(not(equalTo("hello")), equalTo("")));
  }
}
