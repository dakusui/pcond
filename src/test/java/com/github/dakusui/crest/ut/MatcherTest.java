package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;
import org.opentest4j.AssertionFailedError;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.assertThat;


public class MatcherTest {
  @Test(expected = AssertionFailedError.class)
  public void givenFailingMatcher$whenAssertThat$thenFail() {
    assertThat(
        "Hello",
        Matcher.Leaf.<Object, Object>create(wrap(Predicates.equalTo("hello")), wrap(Functions.identity()))
    );
  }

  @Test(expected = AssertionFailedError.class)
  public void givenFailingNestedMatcher$whenAssertThat$thenFail() {
    assertThat(
        "Hello",
        Matcher.Conjunctive.create(Arrays.<Matcher<? super Object>>asList(
            Matcher.Leaf.create(wrap(Predicates.equalTo("hello")), wrap(Functions.identity())),
            Matcher.Leaf.create(wrap(Predicates.equalTo("hello")), wrap(Functions.identity()))
        )));
  }

  private <T> Predicate<T> wrap(Predicate<T> predicate) {
    AtomicBoolean alreadyCalled = new AtomicBoolean(false);
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        if (alreadyCalled.get())
          throw new IllegalStateException("Already called!");
        return predicate.test(t);
      }

      @Override
      public String toString() {
        return predicate.toString();
      }
    };
  }

  private <T, R> Function<T, R> wrap(Function<T, R> function) {
    AtomicBoolean alreadyCalled = new AtomicBoolean(false);
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        if (alreadyCalled.get())
          throw new IllegalStateException("Already called!");
        return function.apply(t);
      }

      @Override
      public String toString() {
        return function.toString();
      }
    };
  }
}
