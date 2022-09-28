package com.github.dakusui.pcond.ut.fluent;

import com.github.dakusui.pcond.fluent.Fluents;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.fluent.Fluents.assertWhen;
import static com.github.dakusui.pcond.fluent.Fluents.value;
import static com.github.dakusui.pcond.forms.Predicates.isNotNull;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MoreFluentListTest {
  @Test
  public void test_elementAt() {
    List<String> var = asList("hello", "world");
    assertWhen(Fluents.value(var).elementAt(0).then().isEqualTo("hello"));
  }

  @Test
  public void test_size() {
    List<String> var = asList("hello", "world");
    assertWhen(Fluents.value(var).size().then().equalTo(2));
  }

  @Test
  public void test_subList() {
    List<String> var = asList("hello", "world");
    assertWhen(Fluents.value(var).subList(1).then().isEqualTo(singletonList("world")));
  }

  @Test
  public void test_subList$int$int() {
    List<String> var = asList("hello", "world");
    assertWhen(Fluents.value(var).subList(1, 2).then().isEqualTo(singletonList("world")));
  }

  @Test
  public void test_stream() {
    List<String> var = asList("hello", "world");
    assertWhen(Fluents.value(var).stream().then().allMatch(isNotNull()));
  }

  @Test
  public void test_isEmpty() {
    List<String> var = asList("hello", "world");
    assertWhen(Fluents.value(var).isEmpty().then().isFalse());
  }
}
