package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Validations;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidationsTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void test() throws ApplicationException {
    try {
      @SuppressWarnings("ConstantConditions") Object ret = Validations.<Object, ApplicationException>validate(null, Predicates.not(Predicates.isEqualTo(null)));
      System.out.println(ret);
    } catch (ApplicationException e) {
      assertEquals("Value:null violated: !isEqualTo[null]", e.getMessage());
      throw e;
    }
  }

  @Test
  public void test2() {
    @SuppressWarnings("ConstantConditions") Object ret = Validations.validate("Hello", Predicates.not(Predicates.isEqualTo(null)));
    System.out.println(ret);
    assertEquals("Hello", ret);
  }
}
