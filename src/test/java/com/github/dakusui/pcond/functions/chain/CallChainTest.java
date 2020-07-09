package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.MultiFunction;
import org.junit.Test;

import static com.github.dakusui.pcond.functions.chain.ChainUtils.instanceMethod;
import static com.github.dakusui.pcond.functions.chain.ChainUtils.parameter;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class CallChainTest {
  @Test
  public void test0() {
    MultiFunction<Boolean> func = CallChain.create("hello", "startsWith").build();
    System.out.println(func.apply(emptyList()));
  }

  @Test
  public void test() {
    MultiFunction<Boolean> func = CallChain.create(instanceMethod("hello", "startsWith", "h")).build();
    System.out.println(func.apply(emptyList()));
  }

  @Test
  public void test2() {
    MultiFunction<Boolean> func = CallChain.create(instanceMethod(parameter(0), "startsWith", parameter(0))).build();
    System.out.println(func.apply(asList("hello", "h")));
  }

  @Test
  public void test3() {
    MultiFunction<Boolean> func = CallChain.create("helloWorld", "substring", 5).andThen("startsWith", "W").build();
    System.out.println(func.apply(emptyList()));
  }

  @Test
  public void test4() {
    MultiFunction<Boolean> func = CallChain.create(parameter(0), "substring", parameter(1)).andThen("startsWith", parameter(2)).build();
    System.out.println(func.apply(asList("helloWorld", 5, "W")));
  }
}

