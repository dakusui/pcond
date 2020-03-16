package com.github.dakusui.pcond.sandbox;

import org.junit.Test;

public class Sandbox {
  @Test
  public void test() {
    System.getProperties().forEach((k, v)-> {
      System.out.println(k + "=<" + v + ">");
    });
  }
}
