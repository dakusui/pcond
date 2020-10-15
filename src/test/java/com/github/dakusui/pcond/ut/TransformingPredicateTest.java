package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.core.identifieable.IdentifiableFunctionFactory;
import com.github.dakusui.pcond.core.identifieable.IdentifiablePredicateFactory;
import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TransformingPredicateTest extends TestBase {
  @Test
  public void testEquals() {
    Predicate<String> p = Predicates.<String, String>transform(Functions.identity()).check(Predicates.isEqualTo("hello"));
    Predicate<String> q = Predicates.<String, String>transform(Functions.identity()).check(Predicates.isEqualTo("hello"));
    Predicate<String> r = Predicates.<String, String>transform(Functions.identity()).check(Predicates.isNotNull());
    Predicate<String> s = Predicates.<String, String>transform(Functions.stringify()).check(Predicates.isEqualTo("hello"));
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
    Predicate<String> p = Predicates.<String, String>transform(Functions.identity()).check(Predicates.isEqualTo("hello"));
    Predicate<String> q = Predicates.<String, String>transform(Functions.identity()).check(Predicates.isEqualTo("hello"));
    Predicate<String> r = Predicates.<String, String>transform(Functions.identity()).check(Predicates.isNotNull());
    Predicate<String> s = Predicates.<String, String>transform(Functions.stringify()).check(Predicates.isEqualTo("hello"));

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
    Predicate<String> p = Predicates.transform(identity).check(equalToHello);

    Predicate<String> q = Predicates.transform(IdentifiableFunctionFactory.function(() -> "", identity))
        .check(IdentifiablePredicateFactory.leaf(() -> "", equalToHello));

    assertEquals(
        p.hashCode(),
        q.hashCode());
  }

  @Test
  public void givenNullForName$whenToString$thenLooksGood() {
    IdentifiablePredicateFactory.TransformingPredicate<Object, Object> p = new IdentifiablePredicateFactory.TransformingPredicate<>(null, Predicates.alwaysTrue(), Functions.identity());
    System.out.println(p.toString());
    assertEquals("identity alwaysTrue", p.toString());
  }

  @Test
  public void givenNonNullName$whenToString$thenLooksGood() {
    IdentifiablePredicateFactory.TransformingPredicate<Object, Object> p = new IdentifiablePredicateFactory.TransformingPredicate<>("hello->", Predicates.alwaysTrue(), Functions.identity());
    System.out.println(p.toString());
    assertEquals("hello->identity alwaysTrue", p.toString());
  }
}
