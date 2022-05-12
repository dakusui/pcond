package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.matchers.transformers.StringMatcherBuilderBuilder0;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

public class Matcher<IN, IM> extends PrintablePredicateFactory.TransformingPredicate<IN, IM> {
  public Matcher(String name, Predicate<? super IN> predicate, Function<? super IM, ? extends IN> function) {
    super(name, predicate, function);
  }

  public static class Builder<IN, IM> extends PrintablePredicateFactory.Messaged<IN> {
    private       String                             name;
    private final Function<? super IN, ? extends IM> function;
    private final PredicateHolder<IM>                predicate;

    public Builder(Function<? super IN, ? extends IM> function) {
      super(() -> "", new PredicateHolder<>(), singletonList(new Object()));
      this.name = name;
      this.function = function;
      this.predicate = (PredicateHolder<IM>) this.rawPredicate();
    }

    public String name() {
      return this.name;
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

    @SafeVarargs
    public final Builder<IN, IM> allOf(Predicate<? super IM>... predicates) {
      return verifyWith(Predicates.allOf(predicates));
    }

    @SafeVarargs
    public final Builder<IN, IM> anyOf(Predicate<? super IM>... predicates) {
      return verifyWith(Predicates.anyOf(predicates));
    }

    @SafeVarargs
    public final Builder<IN, IM> and(Predicate<? super IM>... predicates) {
      return verifyWith(Predicates.and(predicates));
    }

    @SafeVarargs
    public final Builder<IN, IM> or(Predicate<? super IM>... predicates) {
      return verifyWith(Predicates.or(predicates));
    }

    @SuppressWarnings("unchecked")
    public Builder<IN, IM> verifyWith(Predicate<? super IM> predicate) {
      if (this.predicate.predicate == null)
        this.predicate.predicate = (Predicate<IM>) predicate;
      else
        this.predicate.predicate = this.predicate.predicate.and(predicate);
      return this;
    }

    @SuppressWarnings("unchecked")
    private Predicate<IN> build(Predicate<? super IM> predicate) {
      return (Predicate<IN>) PrintablePredicateFactory.TransformingPredicate.Factory.create(this.name, this.function).check(predicate);
    }

    static class PredicateHolder<T> implements Predicate<T> {
      Predicate<T> predicate = null;

      @Override
      public boolean test(T t) {
        requireState(this.predicate, Objects::nonNull, () -> "A predicate is not set to this object.");
        return this.predicate.test(t);
      }
    }

    public static class Builder0<B extends Builder0<B, IN, OUT>, IN, OUT> {
      private Function<IN, OUT> chain = null;
      private String            name;

      /**
       *
       */
      @SuppressWarnings("unchecked")
      public Builder0(Function<? super IN, ? extends OUT> chain) {
        this.chain = (Function<IN, OUT>) chain;
      }


      @SuppressWarnings("unchecked")
      public <BB extends Matcher.Builder.Builder0<BB, IN, NOUT>, NOUT> BB chain(Function<OUT, NOUT> function) {
        return (BB) chain(function, f -> (BB) new Builder0<BB, IN, NOUT>(this.chain.andThen(function)));
      }

      public <BB extends Matcher.Builder.Builder0<BB, IN, NOUT>, NOUT> BB chain(Function<OUT, NOUT> function, Function<Function<IN, NOUT>, BB> constructor) {
        return constructor.apply(chainFunction(this.chain, function));
      }

      private static <IN, OUT, NOUT> Function<IN, NOUT> chainFunction(Function<IN, OUT> function, Function<OUT, NOUT> after) {
        return function.andThen(after);
      }

      private static <IN> Builder0<?, IN, IN> create() {
        return new Builder0<>(Functions.identity());
      }

      public StringMatcherBuilderBuilder0<IN> valueIsString() {
        return new StringMatcherBuilderBuilder0<>(Functions.cast(String.class));
      }


      public Builder<IN, OUT> then() {
        return new Builder<>(requireNonNull(this.chain));
      }

      public B name(String name) {
        this.name = name;
        //noinspection unchecked
        return (B) this;
      }

      public <BB extends Builder0<BB, OIN, List<E>>, OIN, E> BB valueIsListOf(E e) {
        return (BB) this.chain(v -> (List<E>) v);
      }
    }
  }
}
