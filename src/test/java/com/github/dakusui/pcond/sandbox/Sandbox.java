package com.github.dakusui.pcond.sandbox;

import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Predicates.*;

public class Sandbox {
  @Test
  public void test() {
    System.getProperties().forEach((k, v) -> {
      System.out.println(k + "=<" + v + ">");
    });
  }

  @Test
  public void hello() {
    Predicate<Integer> p = i -> 0 <= i && i < 100;
    System.out.println(p.toString());
  }

  @Test
  public void hello2() {
    Predicate<Integer> p = and(ge(0), lt(100));
    System.out.println(p.toString());
  }
}
