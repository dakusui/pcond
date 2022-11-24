package com.github.dakusui.pcond;

import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.shared.ApplicationException;
import com.github.dakusui.thincrest.TestAssertions;
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
      validate("",
          not(                                             // (1)
              transform(length())                          // (2)
                  .check(lt(100))),                  // (3)
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'' violated: !length <[100]"));
      // expected (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("not"),
              CoreMatchers.containsString("->true")));
      // actual (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("not"),
              CoreMatchers.containsString("->false")));
      // (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 3)),
          CoreMatchers.allOf(
              CoreMatchers.not(CoreMatchers.containsString("Mismatch:")),
              CoreMatchers.containsString("transform:length"),
              CoreMatchers.containsString("->0")));
      // expected (3)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 4)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("check:<[100]"),
              CoreMatchers.containsString("->false")));
      // actual (3)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 5)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("check:<[100]"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$notMergedWhenMismatch() {
    try {
      validate("Hello",
          not(                                     // (1)
              equalTo("Hello")),             // (2)
          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'Hello' violated: !=[Hello]"));
      // expected (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("->not"),
              CoreMatchers.containsString("->true")
          ));
      // actual (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("->not"),
              CoreMatchers.containsString("->false")));
      // expected (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 3)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("=[Hello]"),
              CoreMatchers.containsString("->false")));
      // actual (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 4)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("=[Hello]"),
              CoreMatchers.containsString("->true")));
      throw e;
    }
  }

  @Test(expected = ApplicationException.class)
  public void whenInvertedTrasformingPredicateFails_thenPrintDesignedMessage$mergedWhenNotMismatch() {
    try {
      //validate(
      TestAssertions.assertThat(
          "Hello",
          and(                                      // (1)
              not(equalTo("Hello!")),         // (2)
              alwaysFalse())                        // (3)
      );
//          ApplicationException::new);
    } catch (ApplicationException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 0)),
          CoreMatchers.equalTo("Value:'Hello' violated: (!=[Hello!]&&alwaysFalse)"));
      // expected (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 1)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("and"),
              CoreMatchers.containsString("->true")));
      // actual (1)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 2)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("'Hello'"),
              CoreMatchers.containsString("and"),
              CoreMatchers.containsString("->false")));
      // (2)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 3)),
          CoreMatchers.allOf(
              CoreMatchers.not(CoreMatchers.containsString("Mismatch<:")),
              CoreMatchers.containsString("not(=[Hello!])"),
              CoreMatchers.containsString("->true")));
      // expected (3)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 4)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch<:"),
              CoreMatchers.containsString("alwaysFalse"),
              CoreMatchers.containsString("->true")));
      // actual (3)
      MatcherAssert.assertThat(
          simplifyString(lineAt(e.getMessage(), 5)),
          CoreMatchers.allOf(
              CoreMatchers.containsString("Mismatch>:"),
              CoreMatchers.containsString("alwaysFalse"),
              CoreMatchers.containsString("->false")));
      throw e;
    }
  }

  private static Predicate<String> alwaysFalse() {
    return Printables.predicate("alwaysFalse", v -> false);
  }
}
