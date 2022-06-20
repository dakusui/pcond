package com.github.dakusui.pcond.ut.fluent;

import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.fluent.MoreFluents.assertWhen;
import static com.github.dakusui.pcond.fluent.MoreFluents.valueOf;
import static com.github.dakusui.pcond.forms.Predicates.isNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MoreFluentListTest {
  @Test
  public void test_elementAt() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).elementAt(0).then().isEqualTo("hello"));
  }

  @Test
  public void test_size() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).size().then().equalTo(2));
  }

  @Test
  public void test_subList() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).subList(1).then().isEqualTo(singletonList("world")));
  }

  @Test
  public void test_subList$int$int() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).subList(1, 2).then().isEqualTo(singletonList("world")));
  }

  @Test
  public void test_stream() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).stream().then().allMatch(isNotNull()));
  }

  @Test
  public void test_isEmpty() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).isEmpty().then().isFalse());
  }
}
