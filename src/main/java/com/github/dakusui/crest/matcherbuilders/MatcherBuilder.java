package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.pcond.functions.Predicates;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/*
 */
public interface MatcherBuilder<IN, OUT, SELF extends MatcherBuilder<IN, OUT, SELF>> {
  enum Op {
    AND {
      @SuppressWarnings("unchecked")
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return Matcher.Conjunctive.create((List<Matcher<? super I>>) matchers);
      }
    },
    OR {
      @SuppressWarnings("unchecked")
      @Override
      <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers) {
        return Matcher.Disjunctive.create((List<Matcher<? super I>>) matchers);
      }
    };

    @SuppressWarnings("unchecked")
    <I, O> Matcher<? super I> create(List<Predicate<? super O>> predicates, Function<? super I, ? extends O> function) {
      return create(
          predicates.stream(
          ).map(
              predicate -> (Matcher<Object>) Matcher.Leaf.create(predicate, function)
          ).collect(
              toList()
          )
      );
    }

    abstract <I> Matcher<? super I> create(List<? extends Matcher<? super I>> matchers);
  }

  SELF check(Predicate<? super OUT> predicate);

  <MEDIUM> SELF check(Function<? super OUT, ? extends MEDIUM> function, Predicate<? super MEDIUM> predicate);

  default SELF check(String methodName, Object... args) {
    return check(Predicates.invoke(methodName, args));
  }

  default SELF equalTo(OUT value) {
    return this.check(Predicates.equalTo(value));
  }

  Matcher<? super IN> all();

  Matcher<? super IN> any();

  /**
   * Synonym for {@code all()}.
   *
   * @return A matcher built by this object
   * @see MatcherBuilder#all
   */
  default Matcher<? super IN> matcher() {
    return all();
  }

  /**
   * Synonym for {$code matcher()}. You can use this method to make your code look
   * more 'natural' (as an English sentence).
   *
   * @return A built matcher.
   */
  default Matcher<? super IN> $() {
    return matcher();
  }
}
