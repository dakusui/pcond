package com.github.dakusui.crest.examples;

import org.junit.Test;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class Basic {
  @Test
  public void example0() {
    try {
      assertThat(
          "hello, world",
          asString().equalsIgnoreCase("HELLO, WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example1() {
    try {
      assertThat(
          "hello, world",
          asString().equalsIgnoreCase("HELLO! WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example2() {
    try {
      assertThat(
          "hello, world",
          asString("toUpperCase").equalsIgnoreCase("HELLO! WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example3() {
    try {
      assertThat(
          "hello, world",
          asString(call("toUpperCase").$()).equalsIgnoreCase("HELLO! WORLD").$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example4() {
    try {
      assertThat(
          "hello, world",
          asString().check(call("toUpperCase").$(), equalsIgnoreCase("HELLO! WORLD")).$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example5() {
    // Acceptable strings
    // - _Hello, World!
    // - +HELLO, world!
    // - -hello, WORLD!
    try {
      assertThat(
          "*howdy, world",
          asString(call("substring", 1).$())
              .check(call("toUpperCase").$(), startsWith("HELLO"))
              .check(call("toLowerCase").$(), containsString("world!"))
              .$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void example6() {
    // Acceptable strings
    //   0123456
    // - _Hello, World!
    // - +HELLO, world!
    // - -hello, WORLD!
    // - -Hello,world!
    try {
      assertThat(
          "*howdy, world",
          allOf(
              asString(call("substring", 1).$())
                  .check(call("toUpperCase").$(), startsWith("HELLO"))
                  .$(),
              asString(call("substring", 7).$())
                  .check(call("toLowerCase").$(), containsString("world!"))
                  .$()));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
  @Test
  public void example6b() {
    // Acceptable strings
    //   0123456
    // - _Hello, World!
    // - +HELLO, world!
    // - -hello, WORLD!
    // - -Hello,world!
    try {
      assertThat(
          "*howdy, world",
          allOf(
              asString(call("substring", 1).andThen("toLowerCase").$())
                  .check(call("toUpperCase").$(), startsWith("HELLO"))
                  .$(),
              asString(call("substring", 7).$())
                  .check(call("toLowerCase").andThen("toLowerCase").$(), containsString("world!"))
                  .$()));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }
}
