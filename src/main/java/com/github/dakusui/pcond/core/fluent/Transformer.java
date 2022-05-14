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
  private final Function<OIN, OUT> function;

  /**
   *
   */
  @SuppressWarnings("unchecked")
  public <COUT> Transformer(Function<? super COUT, ? extends OUT> function) {
    this.function = (Function<OIN, OUT>) function;
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
  BB transformToList(B parent, Function<COUT, NOUT> f, C constructor) {
    return constructor.apply(parent, f);
  }

  Function<? super OIN, ? extends OUT> function() {
    return this.function;
  }


  @SuppressWarnings("unchecked")
  public ToStringTransformer<OIN> transformToString(Function<OUT, String> f) {
    return Transformer.transformToList((B) this,
        f,
        (b, outnoutFunction) -> new ToStringTransformer<>(outnoutFunction));
  }

  @SuppressWarnings("unchecked")
  public <O> ToObjectTransformer<OIN, O> transformToObject(Function<OUT, O> f) {
    return Transformer.transformToList((B) this,
        f,
        (b, outnoutFunction) -> new ToObjectTransformer<>(outnoutFunction));
  }

  @SuppressWarnings("unchecked")
  public <E>
  ToObjectTransformer<OIN, List<E>> transformToList(Function<OUT, List<E>> f) {
    return Transformer.transformToList(
        (B) this,
        f,
        (B b, Function<OUT, List<E>> function) -> new ToObjectTransformer<>(function));
  }

  public ObjectVerifier<OIN, OUT> then() {
    return new ObjectVerifier<>(requireNonNull(this.function));
  }

}
