package com.github.dakusui.pcond.examples;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.List;

import static com.github.dakusui.pcond.Preconditions.*;
import static com.github.dakusui.pcond.functions.Functions.size;
import static com.github.dakusui.pcond.functions.Predicates.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
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

  @Test(expected = IllegalStateException.class)
  public void testState() {
    try {
      System.out.println(requireState(Collections.emptyList(), when("size", size()).then(gt(0))));
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
      throw e;
    }
  }
}
