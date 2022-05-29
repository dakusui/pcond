package com.github.dakusui.pcond.sandbox;

import org.junit.Test;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.Fluents.when;
import static com.github.dakusui.pcond.TestAssertions.assertStatement;
import static com.github.dakusui.pcond.forms.Predicates.*;

@SuppressWarnings("NewClassNamingConvention")
public class Sandbox {
  @Test
  public void test() {
    System.getProperties().forEach((k, v) -> System.out.println(k + "=<" + v + ">"));
  }

  @Test
  public void hello() {
    Predicate<Integer> p = i -> 0 <= i && i < 100;
    System.out.println(p);
  }

  @Test
  public void hello2() {
    Predicate<Integer> p = and(ge(0), lt(100));
    System.out.println(p);
  }

  @Test
  public void hello3() {
    assertStatement(when("hello").substring(2).then().isEqualTo("world"));
  }
}
