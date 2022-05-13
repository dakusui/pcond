package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.matchers.transformers.ObjectMatcherBuilderBuilder0;
import com.github.dakusui.pcond.core.matchers.transformers.StringMatcherBuilderBuilder0;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;

public class Matcher<IN, IM> extends PrintablePredicateFactory.TransformingPredicate<IN, IM> {
  public Matcher(String name, Predicate<? super IN> predicate, Function<? super IM, ? extends IN> function) {
    super(name, predicate, function);
  }

  public static class Builder<IN, IM> {
    private final Function<? super IN, ? extends IM> function;
    private       Predicate<IM>                      predicate;

    public Builder(Function<? super IN, ? extends IM> function) {
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
      if (this.predicate == null)
        this.predicate = (Predicate<IM>) predicate;
      else
        this.predicate = this.predicate.and(predicate);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <AS> Predicate<AS> build() {
      return (Predicate<AS>) PrintablePredicateFactory.TransformingPredicate.Factory.create(this.function).check(this.predicate);
    }

    public <AS> Predicate<AS> as() {
      return build();
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
      BB chainVerifier(B parent, Function<COUT, NOUT> f, C constructor) {
        return constructor.apply(parent, f);
      }

      @SuppressWarnings("unchecked")
      public <
          NOUT,
          C extends BiFunction<B, Function<OUT, NOUT>, BB>,
          BB extends Builder0<BB, OIN, NOUT>>
      BB chain(Function<OUT, NOUT> f, C constructor) {
        return Builder0.chainVerifier((B) this, f, constructor);
      }

      @SuppressWarnings("unchecked")
      public <
          NOUT,
          BB extends Builder0<BB, OIN, NOUT>>
      BB chain(Function<OUT, NOUT> f) {
        return Builder0.chainVerifier((B) this, f, new BiFunction<B, Function<OUT, NOUT>, BB>() {
          @SuppressWarnings("unchecked")
          @Override
          public BB apply(B b, Function<OUT, NOUT> outnoutFunction) {
            return (BB) new ObjectMatcherBuilderBuilder0<OIN, NOUT>(outnoutFunction);
          }
        });
      }

      public StringMatcherBuilderBuilder0<OIN> chainToString(Function<OUT, String> f) {
        return Builder0.chainVerifier((B) this,
            f,
            (b, outnoutFunction) -> new StringMatcherBuilderBuilder0<OIN>(outnoutFunction));
      }

      public <E>
      ObjectMatcherBuilderBuilder0<OIN, List<E>> chainToList(Function<OUT, List<E>> f) {
        return Builder0.<
            OIN,
            OUT,
            B,
            List<E>,
            BiFunction<
                B,
                Function<OUT, List<E>>,
                ObjectMatcherBuilderBuilder0<OIN, List<E>>>,
            ObjectMatcherBuilderBuilder0<OIN, List<E>>
            >chainVerifier(
            (B) this,
            f,
            (B b, Function<OUT, List<E>> function) -> new ObjectMatcherBuilderBuilder0<>(function));
      }


      public Builder<OIN, OUT> then() {
        return new Builder<>(requireNonNull(this.chain));
      }

      public static class Builder1<OIN, OUT> {
        public Builder1() {
        }


        public StringMatcherBuilderBuilder0<OIN> stringValue() {
          return new StringMatcherBuilderBuilder0<OIN>(null);
        }

        public ObjectMatcherBuilderBuilder0<OIN, OUT> forGeneralObject() {
          return new ObjectMatcherBuilderBuilder0<>(null);
        }

        public <E> ObjectMatcherBuilderBuilder0<OIN, List<E>> listValueOf(E value) {
          return new ObjectMatcherBuilderBuilder0<>(null);
        }
      }
    }
  }
}
