package com.github.dakusui.pcond.ut.bugfixes;

import com.github.dakusui.pcond.utils.TestBase;
import org.junit.Test;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.elementAt;
import static com.github.dakusui.pcond.functions.Functions.size;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

public class Issue11Test extends TestBase {
  @Test(expected = IllegalArgumentException.class)
  public void whenReproduceIssue() {
    Object[] args = new Object[] { 123 };
    try {
      requireArgument(asList(args),
          and(transform(size()).check(isEqualTo(1)),
              transform(elementAt(0)).check(and(isNotNull(), isInstanceOf(String.class)))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      assertThat(lineAt(e.getMessage(), 1), containsString("&&"));
      assertThat(lineAt(e.getMessage(), 2), containsString("=>"));
      assertThat(lineAt(e.getMessage(), 3), containsString("size"));
      assertThat(lineAt(e.getMessage(), 4), containsString("isEqualTo[1]"));
      assertThat(lineAt(e.getMessage(), 5), containsString("=>"));
      assertThat(lineAt(e.getMessage(), 6), containsString("at[0]"));
      assertThat(lineAt(e.getMessage(), 7), containsString("&&"));
      assertThat(lineAt(e.getMessage(), 8), containsString("isNotNull"));
      assertThat(lineAt(e.getMessage(), 9), containsString("isInstanceOf"));
      throw e;
    }
  }
}
