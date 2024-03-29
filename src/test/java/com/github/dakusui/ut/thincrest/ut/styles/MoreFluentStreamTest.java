package com.github.dakusui.ut.thincrest.ut.styles;

import com.github.dakusui.pcond.fluent.Statement;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.thincrest.TestFluents.assertStatement;
import static com.github.dakusui.pcond.forms.Predicates.isEqualTo;
import static com.github.dakusui.pcond.forms.Predicates.isNotNull;

public class MoreFluentStreamTest {
  @Test
  public void test_noneMatche() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(Statement.streamValue(var).then().noneMatch(isEqualTo("HELLO")));
  }

  @Test
  public void test_anyMatch() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(Statement.streamValue(var).then().anyMatch(isEqualTo("world")));
  }

  @Test
  public void test_allMatch() {
    Stream<String> var = Stream.of("hello", "world");
    assertStatement(Statement.streamValue(var).then().allMatch(isNotNull()));
  }
}
