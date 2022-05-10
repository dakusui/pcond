package com.github.dakusui.pcond.core.printable;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class Matcher<IN, IM> extends PrintablePredicateFactory.TransformingPredicate<IN, IM> {
  public Matcher(String name, Predicate<? super IN> predicate, Function<? super IM, ? extends IN> function) {
    super(name, predicate, function);
  }

  public static class Builder<IN, IM> {
    private final String                             name;
    private final Function<? super IN, ? extends IM> function;

    public Builder(String name, Function<? super IN, ? extends IM> function) {
      this.name = name;
      this.function = function;
    }

    /**
     * Use this method, when compiler is not able to figure out intended intermediate type.
     *
     * @param <NEW_IM> An intermediate type explicitly specified.
     * @return This object cast by the intermediate type
     */
    @SuppressWarnings("unchecked")
    public <NEW_IM> Builder<IN, NEW_IM> into(Class<NEW_IM> klass) {
      return (Builder<IN, NEW_IM>) this;
    }

    public <NIM> Builder<IN, NIM> andThen(Function<? super IM, ? extends NIM> function) {
      return new Builder<>(this.name, this.function.andThen(function));
    }

    public Predicate<IN> thenVerifyWith(Predicate<? super IM> predicate) {
      return build(requireNonNull(predicate));
    }

    @SuppressWarnings("unchecked")
    private Predicate<IN> build(Predicate<? super IM> predicate) {
      return (Predicate<IN>) PrintablePredicateFactory.TransformingPredicate.Factory.create(this.name, this.function).check(predicate);
    }

    public static class Builder0<IN> {
      private String name;

      /**
       *
       */
      public Builder0() {
        this.name = null;
      }

      @SuppressWarnings("unchecked")
      public <NEW_IN> Builder0<NEW_IN> forType(@SuppressWarnings("unused") Class<NEW_IN> classObject) {
        return (Builder0<NEW_IN>) this;
      }

      public <NIM> Builder<IN, NIM> transformBy(Function<? super IN, ? extends NIM> function) {
        return new Builder<>(name, function);
      }

      public Builder0<IN> name(String name) {
        this.name = name;
        return this;
      }
    }
  }
}
