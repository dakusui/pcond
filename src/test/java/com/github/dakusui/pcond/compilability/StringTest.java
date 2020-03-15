package com.github.dakusui.pcond.compilability;

import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import java.io.Serializable;
import java.util.Map;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Predicates.*;

public class StringTest {
  @Test(expected = IllegalArgumentException.class)
  public void testMatchesRegex() {
    String var = "hello";
    requireArgument(var, matchesRegex("HELLO"));
    requireArgument(var, and(isNotNull(), matchesRegex("HELLO")));
    requireArgument(var, and(isNotNull(), or(matchesRegex("hello")), startsWith("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStartsWith() {
    String var = "hello";
    requireArgument(var, startsWith("HELLO"));
    requireArgument(var, and(isNotNull(), startsWith("HELLO")));
    requireArgument(var, and(isNotNull(), or(startsWith("hello")), startsWith("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testContainsString() {
    String var = "hello";
    requireArgument(var, containsString("HELLO"));
    requireArgument(var, and(isNotNull(), containsString("HELLO")));
    requireArgument(var, and(isNotNull(), or(startsWith("hello")), containsString("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEndsWith() {
    String var = "hello";
    requireArgument(var, startsWith("HELLO"));
    requireArgument(var, and(isNotNull(), startsWith("HELLO")));
    requireArgument(var, and(isNotNull(), or(startsWith("hello")), startsWith("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEqualTo() {
    String var = "hello";
    requireArgument(var, isEqualTo("HELLO"));
    requireArgument(var, and(isNotNull(), isEqualTo("HELLO")));
    requireArgument(var, and(isNotNull(), or(isEqualTo("hello")), isEqualTo("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEqualsIgnoreCase() {
    String var = "hello";
    requireArgument(var, equalsIgnoreCase("HELLO"));
    requireArgument(var, and(alwaysTrue(), isNotNull(), equalsIgnoreCase("HELLO")));
    requireArgument(var, and(isNotNull(), or(equalsIgnoreCase("hello")), equalsIgnoreCase("h")));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsEmptyString() {
    String var = "hello";
    requireArgument(var, isEmptyString());
    requireArgument(var, and(isNotNull(), isEmptyString()));
    requireArgument(var, and(isNotNull(), or(isEmptyString()), isEmptyString()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsEmptyOrNullString() {
    String var = "hello";
    requireArgument(var, isEmptyOrNullString());
    requireArgument(var, and(isEmptyOrNullString(), isEmptyOrNullString()));
    requireArgument(var, and(isNotNull(), or(isEmptyOrNullString()), isEmptyOrNullString()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComparison() {
    String var = "hello";
    requireArgument(var, and(ge("A"), lt("Z")));
    requireArgument(var, or(le("a"), gt("z")));
    requireArgument(var, Predicates.eq("HELLO"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSameAs() {
    String var = "hello";
    requireArgument(var, isSameReferenceAs("A"));
    requireArgument(var, or(isSameReferenceAs("A"), isSameReferenceAs("B")));
  }

  @Test
  public void testIsInstanceOf() {
    String var = "hello";
    requireArgument(var, isInstanceOf(String.class));
    requireArgument(var, and(
        isInstanceOf(String.class),
        isInstanceOf(Serializable.class),
        isInstanceOf(Comparable.class),
        not(isInstanceOf(Map.class))));
  }
}
