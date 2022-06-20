package com.github.dakusui.pcond.ut.fluent;

import org.junit.Test;

import static com.github.dakusui.pcond.fluent.MoreFluents.assertWhen;
import static com.github.dakusui.pcond.fluent.MoreFluents.valueOf;

public class MoreFluentComparableNumberVerifierTest {
  @Test
  public void test_equalTo() {
    double var = 1.0d;
    assertWhen(valueOf(var).then().equalTo(1.0d));
  }

  @Test
  public void test_lessThan() {
    double var = 1.0d;
    assertWhen(valueOf(var).then().lessThan(2.0d));
  }

  @Test
  public void test_lessThanOrEqualTo() {
    double var = 1.0d;
    assertWhen(valueOf(var).then().lessThanOrEqualTo(2.0d));
  }

  @Test
  public void test_greaterThan() {
    double var = 1.0d;
    assertWhen(valueOf(var).then().greaterThan(0.0d));
  }

  @Test
  public void test_greaterThanOrEqualTo() {
    double var = 1.0d;
    assertWhen(valueOf(var).then().greaterThanOrEqualTo(0.0d));
  }

  @Test
  public void test_Integer() {
    int var = 1;
    assertWhen(valueOf(var).then().equalTo(1));
  }

  @Test
  public void test_Long() {
    long var = 1;
    assertWhen(valueOf(var).then().equalTo(1L));
  }

  @Test
  public void test_Short() {
    short var = 1;
    assertWhen(valueOf(var).then().equalTo((short) 1));
  }

  @Test
  public void test_Float() {
    float var = 1.0f;
    assertWhen(valueOf(var).then().equalTo(1.0f));
  }

  @Test
  public void test_Double() {
    double var = 1.0;
    assertWhen(valueOf(var).then().equalTo(1.0));
  }
}
