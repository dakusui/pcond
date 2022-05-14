package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.IntegerTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ListTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.StringTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;
import com.github.dakusui.pcond.forms.Functions;

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
  public Transformer(Function<? super OIN, ? extends OUT> function) {
    this.function = (Function<OIN, OUT>) function;
  }

  @SuppressWarnings("unchecked")
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
  BB transform(B parent, Function<? super COUT, NOUT> f, C constructor) {
    return constructor.apply(parent, (Function<COUT, NOUT>) f);
  }

  protected Function<OIN, ? extends OUT> function() {
    return this.function;
  }


  @SuppressWarnings("unchecked")
  public StringTransformer<OIN> transformToString(Function<OUT, String> f) {
    return Transformer.transform((B) this,
        f,
        (b, func) -> new StringTransformer<>(chainFunctions(this.function(), func)));
  }

  @SuppressWarnings("unchecked")
  public <O> ObjectTransformer<OIN, O> transformToObject(Function<OUT, O> f) {
    return Transformer.transform(
        (B) this,
        f,
        (b, func) -> new ObjectTransformer<>(chainFunctions(this.function(), func)));
  }

  @SuppressWarnings("unchecked")
  public <E>
  ListTransformer<OIN, E> transformToList(Function<OUT, List<E>> f) {
    return Transformer.transform(
        (B) this,
        f,
        (b, func) -> new ListTransformer<>(chainFunctions(this.function(), func)));
  }

  @SuppressWarnings("unchecked")
  protected IntegerTransformer<OIN> transformToInteger(Function<? super OUT, Integer> f) {
    return Transformer.transform(
        (B) this,
        f,
        (b, func) -> new IntegerTransformer<>(chainFunctions(this.function(), func)));
  }

  @SuppressWarnings("unchecked")
  private static <I, M, O> Function<I, O> chainFunctions(Function<I, ? extends M> func, Function<? super M, O> after) {
    if (func == null)
      // In case, func == null, I will become the same as M.
      // So, this cast is safe.
      return (Function<I, O>) after;
    return func.andThen(after);
  }

  public abstract Verifier<?, OIN, OUT> then();

  public abstract Verifier<?, OIN, OUT> then(Function<OUT, OUT> converter);

  public <NOUT> ObjectVerifier<OIN, NOUT> thenAsObject(Function<OUT, NOUT> function) {
    return new ObjectVerifier<>(requireNonNull(this.function).andThen(requireNonNull(function)));
  }

  public StringVerifier<OIN> thenAsString() {
    return this.thenAsString(Functions.stringify());
  }

  public StringVerifier<OIN> thenAsString(Function<OUT, String> toString) {
    return new StringVerifier<>((requireNonNull(this.function).andThen(toString)));
  }

  public <E> ListVerifier<OIN, E> thenAsList(Function<OUT, List<E>> converter) {
    return new ListVerifier<>(requireNonNull(this.function).andThen(converter));
  }

  public IntegerVerifier<OIN> thenAsInteger(Function<OUT, Integer> converter) {
    return new IntegerVerifier<>(requireNonNull(this.function).andThen(converter));
  }
}
