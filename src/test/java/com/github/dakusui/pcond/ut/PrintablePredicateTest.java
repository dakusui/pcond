package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.functions.PrintablePredicate;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class PrintablePredicateTest {
  private abstract static class Conj {
    <T> Predicate<T> create(String name, Predicate<T> p, Predicate<T> q) {
      return create(new PrintablePredicate<>(() -> name + "1", p), new PrintablePredicate<>(() -> name + "2", q));
    }

    abstract <T> Predicate<T> create(Predicate<T> predicate, Predicate<T> predicate1);
  }

  public static class And extends Conj {
    @Test
    public void test() {
      Predicate<?> p1 = create("P", Predicates.isNotNull(), Predicates.isNotNull());
      Predicate<?> p2 = create("P", Predicates.isNotNull(), Predicates.isNotNull());
      Predicate<?> q = create("Q", Predicates.isNotNull(), Predicates.isNotNull());
      Predicate<?> r1 = create("R", Predicates.isNull(), Predicates.isNotNull());
      Predicate<?> r2 = create("R", Predicates.isNotNull(), Predicates.isNull());
      assertThat(
          p1,
          is(p2));
      assertThat(
          p1,
          is(q));
      assertThat(
          p1,
          not(is(r1)));
      assertThat(
          p1,
          not(is(r2)));
      assertThat(
          p1,
          allOf(
              is(p1),
              is(p2),
              is(q),
              not(is(r1)),
              not(is(r2))
          ));
    }

    @Override
    <T> Predicate<T> create(Predicate<T> predicate1, Predicate<T> predicate2) {
      return predicate1.and(predicate2);
    }
  }

  public static class Or {

  }

  public static class Negate {

  }
}
