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
    public Predicate<IN> build() {
      return (Predicate<IN>) PrintablePredicateFactory.TransformingPredicate.Factory.create(this.name, this.function).check(this.predicate);
    }

    static class PredicateHolder<T> implements Predicate<T> {
      Predicate<T> predicate = null;

      @Override
      public boolean test(T t) {
        requireState(this.predicate, Objects::nonNull, () -> "A predicate is not set to this object.");
        return this.predicate.test(t);
      }
    }

    /**
     * @param <B>   The type of this object.
     * @param <OIN> Original input type.
     * @param <OUT> (Current) Output type.
     */
    public static class Builder0<B extends Builder0<B, OIN, OUT>, OIN, OUT> {
      private Function<OIN, OUT> chain = null;

      /**
       *
       */
      @SuppressWarnings("unchecked")
      public Builder0(Function<? super OIN, ? extends OUT> chain) {
        this.chain = (Function<OIN, OUT>) chain;
      }

      /**
       * This constructor is only called by {@link Builder0#create()} method, which
       * makes `OIN` equal to `OUT`.
       * This is the only situation where the field `chain` becomes `null`.
       */
      private Builder0() {
        this.chain = null;
      }


      @SuppressWarnings("unchecked")
      public <BB extends Matcher.Builder.Builder0<BB, OIN, NOUT>, NOUT> BB chain(Function<OUT, NOUT> function) {
        return (BB) chain(function, f -> (BB) new Builder0<BB, OIN, NOUT>(this.chain.andThen(function)));
      }

      /**
       * **NOTE:** unless the parameter-less private constructor is called
       * through the {@link this#create()} method, the {@link this#chain} field will not
       * become `null`.
       * The method makes the same `OIN` and `OUT`.
       * Thus, the rawtype casting exercised in the `if` statement is safe.
       *
       * @see this#Builder0()
       */
      @SuppressWarnings({ "unchecked", "rawtypes" })
      public <BB extends Matcher.Builder.Builder0<BB, OIN, NOUT>, NOUT> BB chain(Function<OUT, NOUT> function, Function<Function<OIN, NOUT>, BB> constructor) {
        if (this.chain == null)
          return (BB) constructor.apply((Function)function);
        return constructor.apply(this.chain.andThen(function));
      }

      public StringMatcherBuilderBuilder0<OIN> valueIsString() {
        return new StringMatcherBuilderBuilder0<>(Functions.cast(String.class));
      }


      public <BB extends Matcher.Builder.Builder0<BB, NOIN, OUT>, NOIN> BB valueIs(Class<NOIN> inputClass) {
        return (BB)this;
      }
      public <BB extends Matcher.Builder.Builder0<BB, List<E>, OUT>, E> BB listOf(E elementClass) {
        return (BB)this;
      }

      public Builder<OIN, OUT> then() {
        return new Builder<>(requireNonNull(this.chain));
      }

      @SuppressWarnings("unchecked")
      public static <B extends Builder0<B, OIN, OIN>, OIN> B create() {
        return (B)new Builder0<B, OIN, OIN>();
      }
    }
  }
}
