package com.github.dakusui.pcond.examples;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.github.dakusui.pcond.Assertions.that;
import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.Preconditions.requireState;
import static com.github.dakusui.pcond.functions.Functions.size;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class Example {
  @Test(expected = IllegalArgumentException.class)
  public void testArgument() {
    try {
      checkListSize(Collections.emptyList(), 0);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void checkListSize(List<Object> list, int expectedMinimumSize) {
    requireArgument(list, when(size()).then(gt(expectedMinimumSize)));
  }


  @Test
  public void testCheck1() {
    check1("");
  }

  @Test
  public void testCheck2() {
    check2("");
  }

  @Test
  public void testCheck3() {
    check3("");
  }

  @Test
  public void testCheck4() {
    check4(99);
    check4(100);
  }

  public void check1(String var) {
    String ret = requireArgument(var, and(isNotNull()));
    System.out.println(ret);
  }

  public void check2(String var) {
    String ret = requireArgument(var, and(isNotNull(), not(isEmptyString())));
    System.out.println(ret);
  }

  public void check3(String var) {
    String ret = requireArgument(var, not(or(isNull(), isEmptyString())));
    System.out.println(ret);
  }

  public void check4(int var) {
    Comparable<?> ret = requireArgument(var, and(ge(0), lt(100)));
    System.out.println(ret);
  }

  @Test(expected = IllegalStateException.class)
  public void testState() {
    try {
      System.out.println(requireState(Collections.emptyList(), when("size", size()).then(gt(0))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test
  public void helloAssertThat() {
    helloAssertThat(20);
  }

  public void helloAssertThat(int var) {
    assert that((double) var, and(ge(0.0), lt(20.0)));
  }

}
