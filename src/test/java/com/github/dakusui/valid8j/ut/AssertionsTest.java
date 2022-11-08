package com.github.dakusui.valid8j.ut;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.validator.Validator;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.valid8j.Assertions;
import com.github.dakusui.valid8j.ValidationFluents;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Properties;

import static com.github.dakusui.pcond.fluent.Fluents.statement;
import static com.github.dakusui.pcond.fluent.Fluents.value;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class AssertionsTest {
  public static class Passing {
    @Test
    public void testAssertThatValue$thenPass() {
      String var = "10";
      assert Assertions.that(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test
    public void fluent$testAssertThat$thenPassing() {
      String var = "10";
      assert ValidationFluents.that(value(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void fluent$testAssertAll$thenPassing() {
      String var = "10";
      assert ValidationFluents.all(value(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }


    @Test
    public void testAssertPrecondition$thenPassing() {
      String var = "10";
      assert Assertions.precondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test
    public void fluent$testAssertPrecondition$thenPassing() {
      String var = "10";
      assert ValidationFluents.precondition(value(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void fluent$testAssertPreconditions$thenPassing() {
      String var = "10";
      assert ValidationFluents.preconditions(value(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void testAssertPostcondition$thenPassing() {
      String var = "10";
      assert Assertions.postcondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test
    public void fluent$testAssertPostcondition$thenPassing() {
      String var = "10";
      assert ValidationFluents.postcondition(value(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }

    @Test
    public void fluent$testAssertPostconditions$thenPassing() {
      String var = "10";
      assert ValidationFluents.postconditions(value(var).thenWith(b -> b.statement(Predicates.ge("10").and(Predicates.lt("20")))));
    }
  }

  public static class Failing extends TestBase.ForAssertionEnabledVM {
    @Test(expected = AssertionError.class)
    public void testAssertThat$thenFailing() {
      String var = "20";
      assert Assertions.that(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test(expected = AssertionError.class)
    public void testAssertPrecondition$thenFailing() {
      String var = "20";
      assert Assertions.precondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }

    @Test(expected = AssertionError.class)
    public void testAssertPostcondition$thenFailing() {
      String var = "20";
      assert Assertions.postcondition(var, Predicates.ge("10").and(Predicates.lt("20")));
    }
  }

  public static class MessageTest {
    @Test
    public void composeMessage$thenComposed() {
      assertEquals("Value:\"hello\" violated: isNull", new Validator.Impl(new Properties()).configuration().messageComposer().composeMessageForAssertion("hello", Predicates.isNull()));
    }
  }
}
