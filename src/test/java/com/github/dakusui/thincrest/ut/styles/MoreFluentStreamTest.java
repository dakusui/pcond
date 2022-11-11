package com.github.dakusui.thincrest.ut.styles;

import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.thincrest.TestFluents.assertStatement;
import static com.github.dakusui.pcond.fluent.Fluents.value;
import static com.github.dakusui.pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.pcond.forms.Predicates.isNotNull;

public class MoreFluentStreamTest {
  @Test
  public void test_noneMatche() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(value(var).then().noneMatch(isEqualTo("HELLO")));
  }

  @Test
  public void test_anyMatch() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(value(var).then().anyMatch(isEqualTo("world")));
  }

  @Test
  public void test_allMatch() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(value(var).then().allMatch(isNotNull()));
  }
}
