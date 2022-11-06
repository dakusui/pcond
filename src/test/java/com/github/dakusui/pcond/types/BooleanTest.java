package com.github.dakusui.pcond.types;

import com.github.dakusui.shared.TestUtils;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static com.github.dakusui.shared.FluentTestUtils.when;
import static com.github.dakusui.shared.TestUtils.validate;

public class BooleanTest extends TestBase {
  @Test
  public void booleanTest() {
    boolean value = true;
    validate(value, when().asBoolean().then().isTrue());
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void booleanTestFail() {
    boolean value = true;
    try {
      validate(value, when().asBoolean().then().isFalse());
    } catch (TestUtils.IllegalValueException e) {
      e.printStackTrace();
      // TODO
//      MatcherAssert.assertThat(e.getExpected(), CoreMatchers.containsString("true->isFalse->true"));
//      MatcherAssert.assertThat(e.getActual(), CoreMatchers.containsString("true->isFalse->false"));
      MatcherAssert.assertThat(
          e.getMessage().replaceAll(" +", ""),
          CoreMatchers.containsString("true->isFalse->true"));
      MatcherAssert.assertThat(
          e.getMessage().replaceAll(" +", ""),
          CoreMatchers.containsString("true->isFalse->false"));
      throw e;
    }
  }

  @Test
  public void booleanTransformerTest() {
    boolean value = true;
    validate(value, when().asObject().asBoolean().then().isTrue());
  }

  @Test(expected = TestUtils.IllegalValueException.class)
  public void booleanTransformerTestFail() {
    boolean value = true;
    validate(value, when().asObject().asBoolean().then().isFalse());
  }
}
