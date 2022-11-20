package com.github.dakusui.thincrest.ut.styles;

import com.github.dakusui.pcond.fluent.Fluents;
import org.junit.Test;

import java.util.List;

import static com.github.dakusui.pcond.forms.Predicates.isNotNull;
import static com.github.dakusui.thincrest.TestFluents.assertStatement;
import static java.util.Arrays.asList;

/**
 * Temporarily commented out for improving new fluent model.
 */
public class MoreFluentListTest {
  @Test
  public void test_elementAt() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listValue(var).elementAt(0).then().isEqualTo("hello"));
  }

  @Test
  public void test_size() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listValue(var).size().then().equalTo(2));
  }

  /*
  @Test
  public void test_subList() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listStatement(var).subList(1).then().isEqualTo(singletonList("world")));
  }

  @Test
  public void test_subList$int$int() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listStatement(var).subList(1, 2).then().isEqualTo(singletonList("world")));
  }
   */
  @Test
  public void test_stream() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listValue(var).stream().then().allMatch(isNotNull()));
  }

  @Test
  public void test_isEmpty() {
    List<String> var = asList("hello", "world");
    assertStatement(Fluents.listValue(var).isEmpty().then().isFalse());
  }
}
