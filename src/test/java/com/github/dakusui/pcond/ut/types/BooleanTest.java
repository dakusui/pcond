package com.github.dakusui.pcond.ut.types;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ComparisonFailure;
import org.junit.Test;

import static com.github.dakusui.pcond.ut.FluentsInternalTest.Utils.when;

public class BooleanTest extends TestBase {
  @Test
  public void booleanTest() {
    boolean value = true;
    TestAssertions.assertThat(value, when().asBoolean().then().isTrue());
  }

  @Test(expected = ComparisonFailure.class)
  public void booleanTestFail() {
    boolean value = true;
    try {
      TestAssertions.assertThat(value, when().asBoolean().then().isFalse());
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("true->isFalse->true"));
      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("true->isFalse->false"));
      throw e;
    }
  }

  @Test
  public void booleanTransformerTest() {
    boolean value = true;
    TestAssertions.assertThat(value, when().asObject().asBoolean().then().isTrue());
  }

  @Test(expected = ComparisonFailure.class)
  public void booleanTransformerTestFail() {
    boolean value = true;
    TestAssertions.assertThat(value, when().asObject().asBoolean().then().isFalse());
  }
}
