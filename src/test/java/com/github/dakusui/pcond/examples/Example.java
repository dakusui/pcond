package com.github.dakusui.pcond.examples;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Collections;
import java.util.List;

import static com.github.dakusui.pcond.Preconditions.*;
import static com.github.dakusui.pcond.functions.Functions.size;
import static com.github.dakusui.pcond.functions.Predicates.gt;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Example {
  private static final int numLoops = 1_000_000_000;

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
