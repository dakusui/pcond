package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Validations;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidationsTest extends TestBase {
  public static class AppLevelException extends RuntimeException {
    AppLevelException(String message) {
      super(message);
    }
  }

  @Test(expected = AppLevelException.class)
  public void test() {
    try {
      @SuppressWarnings("ConstantConditions") Object ret = Validations.validate(null, Predicates.not(Predicates.equalTo(null)), AppLevelException::new);
      System.out.println(ret);
    } catch (AppLevelException e) {
      assertEquals("value:null violated runtime check:value !equalTo[null]", e.getMessage());
      throw e;
    }
  }

  @Test
  public void test2() {
    @SuppressWarnings("ConstantConditions") Object ret = Validations.validate("Hello", Predicates.not(Predicates.equalTo(null)), AppLevelException::new);
    System.out.println(ret);
    assertEquals("Hello", ret);
  }
}
