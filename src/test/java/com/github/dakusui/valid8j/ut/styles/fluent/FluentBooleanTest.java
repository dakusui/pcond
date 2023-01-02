package com.github.dakusui.valid8j.ut.styles.fluent;

import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.shared.utils.ut.TestBase;
import org.junit.Test;

import static com.github.dakusui.pcond.fluent.Fluents.booleanValue;
import static com.github.dakusui.pcond.propertybased.utils.TestCaseUtils.exerciseStatementExpectingComparisonFailure;
import static com.github.dakusui.pcond.propertybased.utils.TestCaseUtils.exerciseStatementExpectingPass;

public class FluentBooleanTest extends TestBase {
  @Test
  public void givenFalse_whenIsTrue_thenComparisonFailure() {
    exerciseStatementExpectingComparisonFailure(booleanValue(false).then().isTrue());
  }

  @Test
  public void givenFalse_whenIsTrueAndIsTrue_thenComparisonFailure() {
    exerciseStatementExpectingComparisonFailure(booleanValue(false).then().isTrue().isTrue());
  }

  @Test
  public void givenFalse_whenToBooleanIsTrue_thenComparisonFailure() {
    exerciseStatementExpectingComparisonFailure(booleanValue(false).toBoolean(Functions.identity()).then().isTrue());
  }

  @Test
  public void givenFalse_whenTransformIsTrue_thenComparisonFailure() {
    exerciseStatementExpectingComparisonFailure(booleanValue(false).transform(b -> b.then().isTrue().done()));
  }


  @Test
  public void givenFalse_whenCheckIsTrue_thenComparisonFailure() {
    exerciseStatementExpectingComparisonFailure(booleanValue(false).then().check(b -> b.isTrue().done()));
  }

  @Test
  public void givenTrue_whenIsTrue_thenPass() {
    exerciseStatementExpectingPass(booleanValue(true).then().isTrue());
  }
}
