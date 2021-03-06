package com.github.dakusui.pcond.compilability;

import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.stream;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static java.util.Arrays.asList;

public class ListTest {
  @Test(expected = IllegalArgumentException.class)
  public void testListContains() {
    List<String> var = asList("hello", "world");
    requireArgument(var, contains("HELLO"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCollectionContains() {
    Collection<String> var = asList("hello", "world");
    requireArgument(var, contains("HELLO"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmpty() {
    Collection<String> var = asList("hello", "world");
    requireArgument(var, isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAllMatch() {
    Collection<String> var = asList("hello", "world", null);
    requireArgument(var, transform(stream()).check(allMatch(isNotNull())));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAnyMatch() {
    Collection<String> var = asList("hello", "world");
    requireArgument(var, transform(stream()).check(anyMatch(isNull())));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNoneMatch() {
    Collection<String> var = asList("hello", "world", null);
    requireArgument(var, transform(stream()).check(noneMatch(isNull())));
  }
}
