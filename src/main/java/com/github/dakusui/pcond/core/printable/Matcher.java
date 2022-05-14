package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.matchers.chckers.ListMatcherBuilder;
import com.github.dakusui.pcond.core.matchers.chckers.ObjectMatcherBuilder;
import com.github.dakusui.pcond.core.matchers.chckers.StringMatcherBuilder;
import com.github.dakusui.pcond.core.matchers.transformers.ObjectMatcherBuilderBuilder0;
import com.github.dakusui.pcond.core.matchers.transformers.StringMatcherBuilderBuilder0;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class Matcher<IN, IM> extends PrintablePredicateFactory.TransformingPredicate<IN, IM> {
  public Matcher(String name, Predicate<? super IN> predicate, Function<? super IM, ? extends IN> function) {
    super(name, predicate, function);
  }

  public static class Builder<B extends Builder<B, OIN, IM>, OIN, IM> {
    private final Function<? super OIN, ? extends IM> function;
    private       Predicate<IM>                       predicate;

    public Builder(Function<? super OIN, ? extends IM> function) {
      this.function = function;
      this.predicate = null;
    }

    /**
     * Use this method, when compiler is not able to figure out intended intermediate type.
     *
     * @param <NEW_IM> An intermediate type explicitly specified.
     * @return This object cast by the intermediate type
     */
    @SuppressWarnings("unchecked")
    public <NEW_IM, BB extends Builder<BB, OIN, NEW_IM>> BB into(Class<NEW_IM> klass) {
      return (BB) this;
    }

    @SafeVarargs
    public final B allOf(Predicate<? super IM>... predicates) {
      return (B) verifyWith(Predicates.allOf(predicates));
    }

    @SafeVarargs
    public final B anyOf(Predicate<? super IM>... predicates) {
      return (B) verifyWith(Predicates.anyOf(predicates));
    }

    @SafeVarargs
    public final B and(Predicate<? super IM>... predicates) {
      return (B) verifyWith(Predicates.and(predicates));
    }

    @SafeVarargs
    public final B or(Predicate<? super IM>... predicates) {
      return verifyWith(Predicates.or(predicates));
    }

    public B verifyWith(Predicate<? super IM> predicate) {
      return predicate(predicate);
    }

    @SuppressWarnings("unchecked")
    protected B predicate(Predicate<? super IM> predicate) {
      if (this.predicate == null)
        this.predicate = (Predicate<IM>) predicate;
      else
        this.predicate = this.predicate.and(predicate);
      return (B) this;
    }

    /**
     * Use this method only when you are sure the type you are handling is of `AS`.
     *
     * @param valueType A class of the value you are verifying.
     * @param <AS>      Type to check with your verifier.
     * @return This object
     */
    public <AS>
    ObjectMatcherBuilder<OIN, AS> asObjectOf(Class<AS> valueType) {
      return new ObjectMatcherBuilder<>(Functions.cast(valueType));
    }

    public StringMatcherBuilder<OIN> asString() {
      return new StringMatcherBuilder<>(this.function.andThen(Functions.stringify()));
    }

    public StringMatcherBuilder<OIN> asString(Function<IM, String> converter) {
      return new StringMatcherBuilder<>(this.function.andThen(converter));
    }

    public <E> ListMatcherBuilder<OIN, E> asListOf(Function<IM, List<E>> converter) {
      return new ListMatcherBuilder<>(this.function.andThen(converter));
    }

    @SuppressWarnings("unchecked")
    public <AS> Predicate<AS> build() {
      return (Predicate<AS>) PrintablePredicateFactory.TransformingPredicate.Factory.create(this.function).check(this.predicate);
    }

    /**
     * @param <B>   The type of this object.
     * @param <OIN> Original input type.
     * @param <OUT> (Current) Output type.
     */
    public static abstract class Builder0<B extends Builder0<B, OIN, OUT>, OIN, OUT> {
      private Function<OIN, OUT> chain = null;

      /**
       *
       */
      @SuppressWarnings("unchecked")
      public <COUT> Builder0(Function<? super COUT, ? extends OUT> chain) {
        this.chain = (Function<OIN, OUT>) chain;
      }

      private static <
          OIN,
          COUT,
          B extends Builder0<B, OIN, COUT>,
          NOUT,
          C extends BiFunction<
              B,
              Function<COUT, NOUT>,
              BB>,
          BB extends Builder0<BB, OIN, NOUT>>
      BB chainTransformer(B parent, Function<COUT, NOUT> f, C constructor) {
        return constructor.apply(parent, f);
      }

      private static <
          OIN,
          OUT,
          IM,
          T extends Matcher.Builder.Builder0<T, OIN, OUT>,
          C extends Function<OUT, IM>
          > ObjectMatcherBuilder<OIN, IM> chainToVerifier(T transformer, C converter) {
        return chainToVerifier(transformer, converter, (t, c) -> new ObjectMatcherBuilder<>(transformer.chain().andThen(converter)));
      }

      private static <
          OIN,
          OUT,
          IM,
          T extends Matcher.Builder.Builder0<T, OIN, OUT>,
          V extends Matcher.Builder<V, OIN, IM>,
          C extends Function<OUT, IM>,
          F extends BiFunction<T, C, ? extends V>
          > V chainToVerifier(T transformer, C converter, F factory) {
        return factory.apply(transformer, converter);
      }

      Function<? super OIN, ? extends OUT> chain() {
        return this.chain;
      }


      @SuppressWarnings("unchecked")
      public StringMatcherBuilderBuilder0<OIN> chainToString(Function<OUT, String> f) {
        return Builder0.chainTransformer((B) this,
            f,
            (b, outnoutFunction) -> new StringMatcherBuilderBuilder0<OIN>(outnoutFunction));
      }

      public <O> ObjectMatcherBuilderBuilder0<OIN, O> chainToObject(Function<OUT, O> f) {
        return Builder0.chainTransformer((B) this,
            f,
            (b, outnoutFunction) -> new ObjectMatcherBuilderBuilder0<>(outnoutFunction));
      }

      public <E>
      ObjectMatcherBuilderBuilder0<OIN, List<E>> chainToList(Function<OUT, List<E>> f) {
        return Builder0.chainTransformer(
            (B) this,
            f,
            (B b, Function<OUT, List<E>> function) -> new ObjectMatcherBuilderBuilder0<>(function));
      }

      public <B extends Builder<B, OIN, OUT>> ObjectMatcherBuilder<OIN, OUT> then() {
        return new ObjectMatcherBuilder<>(requireNonNull(this.chain));
      }

      public static class Builder1<OIN, OUT> {
        public Builder1() {
        }

        public StringMatcherBuilderBuilder0<OIN> stringValue() {
          return new StringMatcherBuilderBuilder0<>(null);
        }

        public ObjectMatcherBuilderBuilder0<OIN, OUT> objectValue() {
          return new ObjectMatcherBuilderBuilder0<>(null);
        }

        public ObjectMatcherBuilderBuilder0<OIN, OUT> objectValueOf(OUT value) {
          return new ObjectMatcherBuilderBuilder0<>(null);
        }

        public <E> ObjectMatcherBuilderBuilder0<OIN, List<E>> listValueOf(E value) {
          return new ObjectMatcherBuilderBuilder0<>(null);
        }
      }
    }
  }
}
