package com.github.dakusui.pcond.core.printable;

import java.util.function.Function;
import java.util.function.Predicate;

public class Matcher<IN, IM> extends PrintablePredicateFactory.TransformingPredicate<IN, IM> {
  public Matcher(String name, Predicate<? super IN> predicate, Function<? super IM, ? extends IN> function) {
    super(name, predicate, function);
  }

  public static class Builder<IN, IM> {
    private String           name;
    private Predicate<IM>    predicate;
    private Function<IN, IM> function;

    public Builder() {
      this.predicate = PrintablePredicateFactory.Leaf.ALWAYS_TRUE.instance();
    }

    @SuppressWarnings("unchecked")
    public <NEW_IN> Builder<NEW_IN, IM> forType(@SuppressWarnings("unused") Class<NEW_IN> classObject) {
      return (Builder<NEW_IN, IM>) this;
    }

    @SuppressWarnings("unchecked")
    public <NEW_IM> Builder<IN, NEW_IM> as(@SuppressWarnings("unused") Class<NEW_IM> classObject) {
      return (Builder<IN, NEW_IM>) this;
    }

    public Predicate<IN> build() {
      return new Matcher<>(this.name, this.predicate, this.function);
    }
  }
}
