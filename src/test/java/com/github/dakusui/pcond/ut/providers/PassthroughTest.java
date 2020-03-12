package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.core.AssertionProvider;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PassthroughTest {
  final AssertionProvider provider = new AssertionProvider.Passthrough();

  @Test
  public void testRequireNonNull() {
    Object var = "hello";
    assertEquals(var, provider.requireNonNull(var));
  }

  @Test
  public void testRequireArgument() {
    Object var = "hello";
    assertEquals(var, provider.requireArgument(var, Predicates.alwaysTrue().negate()));
  }

  @Test
  public void testRequireState() {
    Object var = "hello";
    assertEquals(var, provider.requireState(var, Predicates.alwaysTrue().negate()));
  }

  @Test
  public void testRequire() {
    Object var = "hello";
    assertEquals(var, provider.require(var, Predicates.alwaysTrue().negate()));
  }

  @Test
  public void testEnsureNonNull() {
    Object var = null;
    assertEquals(var, provider.ensureNonNull(var));
  }

  @Test
  public void testEnsureState() {
    Object var = "hello";
    assertEquals(var, provider.ensureState(var, Predicates.alwaysTrue().negate()));
  }

  @Test
  public void testEnsure() {
    Object var = "hello";
    assertEquals(var, provider.ensure(var, Predicates.alwaysTrue().negate()));
  }

  @Test
  public void testShouldStub() {
    assertTrue(provider.shouldStub());
  }
}
