package com.github.dakusui.thincrest.ut.styles;

import com.github.dakusui.pcond.fluent.Fluents;
import org.junit.Test;

import static com.github.dakusui.thincrest.TestFluents.assertStatement;
import static com.github.dakusui.pcond.fluent.Fluents.value;

public class MoreFluentComparableNumberCheckerTest {
  @Test
  public void test_equalTo() {
    double var = 1.0d;
    assertStatement(Fluents.value(var).then().equalTo(1.0d));
  }

  @Test
  public void test_lessThan() {
    double var = 1.0d;
    assertStatement(Fluents.value(var).then().lessThan(2.0d));
  }

  @Test
  public void test_lessThanOrEqualTo() {
    double var = 1.0d;
    assertStatement(Fluents.value(var).then().lessThanOrEqualTo(2.0d));
  }

  @Test
  public void test_greaterThan() {
    double var = 1.0d;
    assertStatement(Fluents.value(var).then().greaterThan(0.0d));
  }

  @Test
  public void test_greaterThanOrEqualTo() {
    double var = 1.0d;
    assertStatement(Fluents.value(var).then().greaterThanOrEqualTo(0.0d));
  }

  @Test
  public void test_Integer() {
    int var = 1;
    assertStatement(Fluents.value(var).then().equalTo(1));
  }

  @Test
  public void test_Long() {
    long var = 1;
    assertStatement(Fluents.value(var).then().equalTo(1L));
  }

  @Test
  public void test_Short() {
    short var = 1;
    assertStatement(Fluents.value(var).then().equalTo((short) 1));
  }

  @Test
  public void test_Float() {
    float var = 1.0f;
    assertStatement(Fluents.value(var).then().equalTo(1.0f));
  }

  @Test
  public void test_Double() {
    double var = 1.0;
    assertStatement(Fluents.value(var).then().equalTo(1.0));
  }
}
