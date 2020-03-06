package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.core.Matcher;
import com.github.dakusui.pcond.functions.TransformingPredicate;
import com.github.dakusui.pcond.functions.Predicates;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.functions.Predicates.alwaysTrue;

public abstract class ObjectMatcherBuilder<IN, OUT, SELF extends ObjectMatcherBuilder<IN, OUT, SELF>> implements MatcherBuilder<IN, OUT, SELF> {
  private final Function<? super IN, ? extends OUT> function;
  private final List<Predicate<? super OUT>>        predicates;

  public ObjectMatcherBuilder(Function<? super IN, ? extends OUT> function) {
    this.function = function;
    this.predicates = new LinkedList<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SELF check(Predicate<? super OUT> predicate) {
    this.predicates.add(predicate);
    return (SELF) this;
  }

  @Override
  public <P> SELF check(Function<? super OUT, ? extends P> function, Predicate<? super P> predicate) {
    return this.check(new TransformingPredicate<P, OUT>(predicate, function));
  }

  public SELF isNull() {
    return this.check(Predicates.isNull());
  }

  public SELF isNotNull() {
    return this.check(Predicates.isNotNull());
  }

  public SELF isSameAs(OUT value) {
    return this.check(Predicates.isSameAs(value));
  }

  public SELF isInstanceOf(Class<?> value) {
    return this.check(Predicates.isInstanceOf(value));
  }

  @Override
  public Matcher<? super IN> all() {
    return matcher(Op.AND);
  }

  @Override
  public Matcher<? super IN> any() {
    return matcher(Op.OR);
  }

  private Matcher<? super IN> matcher(Op op) {
    if (predicates.isEmpty())
      predicates.add(alwaysTrue());
    return (predicates.size() == 1) ?
        Matcher.Leaf.create(predicates.get(0), this.function) :
        Objects.requireNonNull(op).create(predicates, this.function);
  }
}

