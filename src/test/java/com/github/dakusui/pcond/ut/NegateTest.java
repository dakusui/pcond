package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Validates;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.validator.ApplicationException;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Functions.length;
import static com.github.dakusui.pcond.forms.Predicates.*;

public class NegateTest {
  @Test
  public void whenTrasformingPredicateFails_thenPrintDesignedMessage() {
    Validates.validate("", not(transform(length()).check(lt(100))), ApplicationException::new);
  }

  @Test
  public void abc() {
    Validates.validate("Hello", not(equalTo("Hello")), ApplicationException::new);
  }

  @Test
  public void abc2() {
    Validates.validate("Hello", and(not(equalTo("Hello!")), alwaysFalse()), ApplicationException::new);
  }

  private static Predicate<String> alwaysFalse() {
    return Printables.predicate("alwaysFalse", v -> false);
  }
}
