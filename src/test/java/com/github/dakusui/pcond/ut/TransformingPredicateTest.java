package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Preconditions;
import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class TransformingPredicateTest {
  @Test
  public void testEquals() {
    Predicate<String> p = Preconditions.<String, String>when(Functions.identity()).then(Predicates.equalTo("hello"));
    Predicate<String> q = Preconditions.<String, String>when(Functions.identity()).then(Predicates.equalTo("hello"));
    Predicate<String> r = Preconditions.<String, String>when(Functions.identity()).then(Predicates.isNotNull());
    Predicate<String> s = Preconditions.<String, String>when(Functions.stringify()).then(Predicates.equalTo("hello"));
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
    Predicate<String> p = Preconditions.<String, String>when(Functions.identity()).then(Predicates.equalTo("hello"));
    Predicate<String> q = Preconditions.<String, String>when(Functions.identity()).then(Predicates.equalTo("hello"));
    Predicate<String> r = Preconditions.<String, String>when(Functions.identity()).then(Predicates.isNotNull());
    Predicate<String> s = Preconditions.<String, String>when(Functions.stringify()).then(Predicates.equalTo("hello"));

    assertThat(
        p.hashCode(),
        allOf(
            is(p.hashCode()),
            is(q.hashCode()),
            not(is(r.hashCode())),
            not(is(s.hashCode()))));

  }
}
