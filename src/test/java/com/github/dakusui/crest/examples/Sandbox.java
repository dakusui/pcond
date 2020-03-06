package com.github.dakusui.crest.examples;

import org.junit.Test;

import java.util.Objects;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.*;
import static java.util.Arrays.asList;

public class Sandbox {
  public static void main(String[] args) {
    new Sandbox().helloAllOfTheWorldThincrest();
  }

  private static <T> T require(T value, Predicate<? super T> predicate) {
    if (predicate.test(value))
      return value;
    throw new RuntimeException();
  }

  private static <T> Predicate<? super T> isNotNull() {
    return (Predicate<T>) Objects::nonNull;
  }


  @Test
  public void helloAllOfTheWorldThincrest() {
    assertThat(
        asList("Hello", "world"),
        allOf(
            asListOf(String.class).allMatch(predicate("==bye", "bye"::equals)).matcher(),
            asListOf(String.class).noneMatch(predicate("==bye", "bye"::equals)).matcher(),
            asListOf(String.class).anyMatch(predicate("==bye", "bye"::equals)).matcher()
        )
    );
  }

  @Test
  public void helloAllOfTheWorldThincrestWithCallChain() {
    assertThat(
        asList("Hello", "world", "everyone"),
        allOf(
            asString(call("get", 0).$()).equalTo("Hello").$(),
            asInteger(call("get", 0).andThen("length").$()).equalTo(5).$()
        )
    );
  }
}
