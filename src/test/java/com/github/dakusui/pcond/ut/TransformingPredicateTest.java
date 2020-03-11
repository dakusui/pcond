package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TransformingPredicateTest {
  @Test
  public void testEquals() {
    Predicate<String> p = Predicates.<String, String>when(Functions.identity()).then(Predicates.isEqualTo("hello"));
    Predicate<String> q = Predicates.<String, String>when(Functions.identity()).then(Predicates.isEqualTo("hello"));
    Predicate<String> r = Predicates.<String, String>when(Functions.identity()).then(Predicates.isNotNull());
    Predicate<String> s = Predicates.<String, String>when(Functions.stringify()).then(Predicates.isEqualTo("hello"));
    Object stranger = new Object();

    assertThat(
        p,
        allOf(
            is(p),
            is(q),
            not(is(r)),
            not(is(s)),
            not(is(stranger))));
  }

  @Test
  public void testHashCode() {
    Predicate<String> p = Predicates.<String, String>when(Functions.identity()).then(Predicates.isEqualTo("hello"));
    Predicate<String> q = Predicates.<String, String>when(Functions.identity()).then(Predicates.isEqualTo("hello"));
    Predicate<String> r = Predicates.<String, String>when(Functions.identity()).then(Predicates.isNotNull());
    Predicate<String> s = Predicates.<String, String>when(Functions.stringify()).then(Predicates.isEqualTo("hello"));

    assertThat(
        p.hashCode(),
        allOf(
            is(p.hashCode()),
            is(q.hashCode()),
            not(is(r.hashCode())),
            not(is(s.hashCode()))));

  }

  @Test
  public void testHashCodeSimply() {
    Function<String, String> identity = Functions.identity();
    Predicate<String> equalToHello = Predicates.isEqualTo("hello");
    Predicate<String> p = Predicates.when(identity).then(equalToHello);
    assertEquals(
        identity.hashCode() + equalToHello.hashCode(),
        p.hashCode());
  }
}
