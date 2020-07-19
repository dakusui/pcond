package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.preds.LeafPredUtils;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class PrintablesPredicateTest {
  public static class LeafPred {
    @Test
    public void test() {
      Predicate<?> p1 = Predicates.isNotNull();
      Predicate<?> p2 = Predicates.isNotNull();
      Predicate<?> q = Predicates.isEqualTo("hello");
      Predicate<?> qq = Predicate.isEqual("hello");
      Object o = new Object();
      assertThat(
          p1,
          allOf(
              is(p1),
              is(p2),
              not(is(q)),
              not(is(qq)),
              not(is(o))));
    }

    @Test
    public void testHashCode() {
      Predicate<String> pp = Predicate.isEqual("hello");
      Predicate<String> p = Printables.predicate("hello", pp);
      assertEquals(pp.hashCode(), p.hashCode());
    }
  }

  private abstract static class Conj extends TestBase.ForAssertionEnabledVM {
    @Test
    public void test() {
      Predicate<?> p1 = create("P", Predicates.isNotNull(), Predicates.isNotNull());
      Predicate<?> p2 = create("P", Predicates.isNotNull(), Predicates.isNotNull());
      Predicate<?> q = create("Q", Predicates.isNotNull(), Predicates.isNotNull());
      Predicate<?> r1 = create("R", Predicates.isNull(), Predicates.isNotNull());
      Predicate<?> r2 = create("R", Predicates.isNotNull(), Predicates.isNull());
      Object o = new Object();
      assertThat(
          p1,
          allOf(
              is(p1),
              is(p2),
              is(q),
              not(is(r1)),
              not(is(r2)),
              not(is(o))
          ));
      assertThat(
          p1.hashCode(),
          allOf(
              is(p1.hashCode()),
              is(p2.hashCode()),
              is(q.hashCode()),
              not(is(0))));
    }

    <T> Predicate<T> create(String name, Predicate<T> p, Predicate<T> q) {
      return create(new LeafPredUtils.LeafPred<>(() -> name + "1", p), new LeafPredUtils.LeafPred<>(() -> name + "2", q));
    }

    abstract <T> Predicate<T> create(PrintablePredicate<T> predicate, PrintablePredicate<T> predicate1);
  }

  public static class And extends Conj {
    @Override
    <T> Predicate<T> create(PrintablePredicate<T> predicate1, PrintablePredicate<T> predicate2) {
      return predicate1.and(predicate2);
    }

    @Test
    public void testWithNonPrintable() {
      Predicate<Object> p1 = Predicates.alwaysTrue().and(v -> true);
      System.out.println(p1);
      System.out.println(p1.test("hello"));
      assertThat(p1.toString(), startsWith("(alwaysTrue&&"));
      assertTrue(p1.test("hello"));
    }
  }

  public static class Or extends Conj {
    @Override
    <T> Predicate<T> create(PrintablePredicate<T> predicate1, PrintablePredicate<T> predicate2) {
      return predicate1.or(predicate2);
    }
  }

  public static class Negate extends TestBase.ForAssertionEnabledVM {
    @Test
    public void test() {
      Predicate<?> p = Predicates.isNotNull();
      Predicate<?> q = Predicates.isNotNull();
      Predicate<?> n = Predicates.isNotNull().negate();
      assertThat(
          p,
          allOf(
              is(p),
              is(q),
              not(is(n))));
    }

    @Test
    public void test2() {
      Predicate<?> p = Predicates.isNotNull().negate();
      Predicate<?> q = Predicates.isNotNull().negate();
      Predicate<?> n = Predicates.isNotNull();
      assertThat(
          p,
          allOf(
              is(p),
              is(q),
              not(is(n))));
      assertThat(
          p.hashCode(),
          allOf(
              is(p.hashCode()),
              is(q.hashCode()),
              not(is(0))));
    }
  }
}
