package com.github.dakusui.pcondtest;

import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.shared.ApplicationException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.shared.utils.TestUtils.lineAt;
import static com.github.dakusui.shared.utils.TestUtils.simplifyString;
import static com.github.dakusui.shared.TestUtils.validate;

public class NegateTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$transformIsntLeafAndNotMerged() {
    try {
      validate("", not(transform(length()).check(lt(100))), ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'' violated: !length <[100]"));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("not"),
              CoreMatchers.containsString("->false")));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.not(CoreMatchers.containsString("Mismatch:")),
              CoreMatchers.containsString("transform:length"),
              CoreMatchers.containsString("->0")));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 3)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("check:<[100]"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$notMergedWhenMismatch() {
    try {
      validate("Hello", not(equalTo("Hello")), ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'Hello' violated: !=['Hello']"));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("->false"),
              CoreMatchers.containsString("->not")
          ));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("=['Hello']"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$mergedWhenNotMismatch() {
    try {
      validate("Hello", and(not(equalTo("Hello!")), alwaysFalse()), ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'Hello' violated: (!=['Hello!']&&alwaysFalse)"));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("and"),
              CoreMatchers.containsString("->false")));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.not(CoreMatchers.containsString("Mismatch:")),
              CoreMatchers.containsString("not(=['Hello!'])"),
              CoreMatchers.containsString("->true")));
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 3)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch:"),
              CoreMatchers.containsString("alwaysFalse"),
              CoreMatchers.containsString("->false")));
      throw e;
    }
  }

  private static Predicate<String> alwaysFalse() {
    return Printables.predicate("alwaysFalse", v -> false);
  }
}
