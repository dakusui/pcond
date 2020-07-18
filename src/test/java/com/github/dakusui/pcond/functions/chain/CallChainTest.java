package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.MultiFunction;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.functions.Predicates.greaterThan;
import static com.github.dakusui.pcond.functions.chain.ChainUtils.instanceMethod;
import static com.github.dakusui.pcond.functions.chain.ChainUtils.parameter;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class CallChainTest {
  @Test
  public void test0() {
    MultiFunction<Boolean> func = CallChain.create("hello", "startsWith", parameter(0)).build();
    System.out.println(func);
    System.out.println(func.apply(singletonList("h")));
  }

  @Test
  public void test() {
    MultiFunction<Boolean> func = CallChain.create(instanceMethod("hello", "startsWith", "h")).build();
    System.out.println(func);
    System.out.println(func.apply(emptyList()));
  }

  @Test
  public void test2() {
    MultiFunction<Boolean> func = CallChain.create(instanceMethod(parameter(0), "startsWith", parameter(1))).build();
    System.out.println(func);
    System.out.println(func.apply(asList("hello", "h")));
  }

  @Test
  public void test3() {
    MultiFunction<Boolean> func = CallChain.create("helloWorld", "substring", 5).andThen("startsWith", "W").build();
    System.out.println(func);
    System.out.println(func.apply(emptyList()));
  }

  @Test
  public void test4() {
    MultiFunction<Boolean> func = CallChain.create(parameter(0), "substring", parameter(1))
        .andThen("startsWith", parameter(2)).build();
    System.out.println(func);
    System.out.println(func.apply(asList("helloWorld", 5, "W")));
  }

  @Test
  public void test5() {
    Predicate<String> p = CallChain.create(parameter(0), "length")
        .andThen(greaterThan(2))
        .toPredicate();

    System.out.println(p);
    System.out.println(p.test("hello"));
  }

  @Test
  public void test5a() {
    MultiFunction<Integer> p = CallChain.create(parameter(0), "length").build();

    System.out.println(p);
    System.out.println(p.apply(singletonList("hello")));

  }

  @Test
  public void test5b() {
    MultiFunction<Integer> p = CallChain.create(parameter(0), "length").andThen(greaterThan(1)).build();

    System.out.println(p);
    System.out.println(p.apply(singletonList("hello")));
  }

  @Test
  public void test5c() {
    MultiFunction<Boolean> p = MultiFunction.toMulti(greaterThan(1));

    System.out.println(p);
    System.out.println(p.apply(singletonList(6)));
  }
}

