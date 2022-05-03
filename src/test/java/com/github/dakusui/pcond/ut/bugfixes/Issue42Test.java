package com.github.dakusui.pcond.ut.bugfixes;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static com.github.dakusui.pcond.Preconditions.require;
import static com.github.dakusui.pcond.functions.Functions.*;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.ut.bugfixes.issue42.Issue42Utils.createPrivateExtendingPublicMap;
import static org.junit.Assert.assertEquals;

public class Issue42Test {

  @Test
  public void givenPrivateClassOverridingPublicMethod$whenPublicMethodIsCalled$thenCallSucceeds() {
    Map<String, String> map = require(
        createPrivateExtendingPublicMap("Hello"),
        transform(mapKeySet()).check(not(isEmpty())));
    assertEquals(Collections.singleton("Hello"), map.keySet());
  }

  private <K> Function<Map<? extends K, ?>, Set<K>> mapKeySet() {
    return call(instanceMethod(parameter(), "keySet"));
  }
}
