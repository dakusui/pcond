package com.github.dakusui.pcond.ut.bugfixes;

import com.github.dakusui.pcond.utils.TestBase;
import org.junit.Test;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.forms.Functions.elementAt;
import static com.github.dakusui.pcond.forms.Functions.size;
import static com.github.dakusui.pcond.forms.Predicates.*;
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
      int i = 0;
      assertThat(lineAt(e.getMessage(), ++i), containsString("&&"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("transform"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("size"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("check"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("isEqualTo[1]"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("transform"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("at[0]"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("check"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("&&"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("isNotNull"));
      assertThat(lineAt(e.getMessage(), ++i), containsString("isInstanceOf"));
      throw e;
    }
  }
}
