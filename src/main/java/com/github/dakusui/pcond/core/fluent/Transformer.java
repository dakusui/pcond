package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.core.fluent.transformers.ToObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ToStringTransformer;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * @param <B>   The type of this object.
 * @param <OIN> Original input type.
 * @param <OUT> (Current) Output type.
 */
public abstract class Transformer<B extends Transformer<B, OIN, OUT>, OIN, OUT> {
  private Function<OIN, OUT> chain;

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public <COUT> Transformer(Function<? super COUT, ? extends OUT> chain) {
    this.chain = (Function<OIN, OUT>) chain;
  }

  private static <
      OIN,
      COUT,
      B extends Transformer<B, OIN, COUT>,
      NOUT,
      C extends BiFunction<
          B,
          Function<COUT, NOUT>,
          BB>,
      BB extends Transformer<BB, OIN, NOUT>>
  BB chainTransformer(B parent, Function<COUT, NOUT> f, C constructor) {
    return constructor.apply(parent, f);
  }

  private static <
      OIN,
      OUT,
      IM,
      T extends Transformer<T, OIN, OUT>,
      C extends Function<OUT, IM>
      > ObjectVerifier<OIN, IM> chainToVerifier(T transformer, C converter) {
    return chainToVerifier(transformer, converter, (t, c) -> new ObjectVerifier<>(transformer.chain().andThen(converter)));
  }

  private static <
      OIN,
      OUT,
      IM,
      T extends Transformer<T, OIN, OUT>,
      V extends Verifier<V, OIN, IM>,
      C extends Function<OUT, IM>,
      F extends BiFunction<T, C, ? extends V>
      > V chainToVerifier(T transformer, C converter, F factory) {
    return factory.apply(transformer, converter);
  }

  Function<? super OIN, ? extends OUT> chain() {
    return this.chain;
  }


  @SuppressWarnings("unchecked")
  public ToStringTransformer<OIN> chainToString(Function<OUT, String> f) {
    return Transformer.chainTransformer((B) this,
        f,
        (b, outnoutFunction) -> new ToStringTransformer<OIN>(outnoutFunction));
  }

  public <O> ToObjectTransformer<OIN, O> chainToObject(Function<OUT, O> f) {
    return Transformer.chainTransformer((B) this,
        f,
        (b, outnoutFunction) -> new ToObjectTransformer<>(outnoutFunction));
  }

  public <E>
  ToObjectTransformer<OIN, List<E>> chainToList(Function<OUT, List<E>> f) {
    return Transformer.chainTransformer(
        (B) this,
        f,
        (B b, Function<OUT, List<E>> function) -> new ToObjectTransformer<>(function));
  }

  public <B extends Verifier<B, OIN, OUT>> ObjectVerifier<OIN, OUT> then() {
    return new ObjectVerifier<>(requireNonNull(this.chain));
  }

}
