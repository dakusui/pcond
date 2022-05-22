package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Printables;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyFunction;
import static com.github.dakusui.pcond.internals.InternalUtils.isDummyFunction;

/**
 * Method names start with `as` or contain `As` suggests that the methods should be
 * used when you know the type of the object you are treating at the line of your code.
 * <p>
 * One starts with `into` or contains `Into` should be used for objects you need to
 * apply a function in order to convert it to treat it in the following lines.
 *
 * @param <TX>  The type of this object.
 * @param <OIN> Original input type.
 * @param <OUT> (Current) Output type.
 */
public abstract class Transformer<
    TX extends Transformer<TX, OIN, OUT>,
    OIN, OUT>
    implements Matcher<OIN, OUT>,
    AsPhraseFactory.ForTransformer<OIN, OUT, TX> {
  private final Function<OIN, OUT> function;
  private final String             transformerName;

  @SuppressWarnings("unchecked")
  public <IN> Transformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    this.transformerName = transformerName;
    this.function = (Function<OIN, OUT>) chainFunctions(parent == null ? dummyFunction() : parent.function, function);
  }

  public Function<? super OIN, ? extends OUT> function() {
    return this.function;
  }

  public String transformerName() {
    return this.transformerName;
  }

  @SuppressWarnings("unchecked")
  public <
      NOUT,
      BB extends Transformer<BB, OIN, NOUT>>
  BB transform(Function<? super OUT, NOUT> f, BiFunction<TX, Function<OUT, NOUT>, BB> factory) {
    return Transformer.transform((TX) this, f, factory);
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public final <NOUT> Verifier<?, OIN, NOUT> tee(Predicate<? super NOUT>... predicates) {
    return this.then().asValueOf((NOUT) value()).allOf(predicates);
  }

  public <O> ObjectTransformer<OIN, O> exercise(Function<? super OUT, O> f) {
    return applyFunction(f);
  }

  public <O> ObjectTransformer<OIN, O> applyFunction(Function<? super OUT, O> f) {
    return transformToObject(f);
  }

  public <O> ObjectTransformer<OIN, O> transformToObject(Function<? super OUT, O> f) {
    return this.transform(f, (TX, func) -> new ObjectTransformer<>(transformerName, this, func));
  }

  public StringTransformer<OIN> transformToString(Function<OUT, String> f) {
    return this.transform(f, (TX, func) -> new StringTransformer<>(transformerName, this, func));
  }

  public <E> ListTransformer<OIN, E> transformToList(Function<OUT, List<E>> f) {
    return this.transform(f, (TX, func) -> new ListTransformer<>(transformerName, this, func));
  }

  public <E> StreamTransformer<OIN, E> transformToStream(Function<OUT, Stream<E>> f) {
    return this.transform(f, (TX, func) -> new StreamTransformer<>(transformerName, this, func));
  }

  public IntegerTransformer<OIN> transformToInteger(Function<? super OUT, Integer> f) {
    return this.transform(f, (TX, func) -> new IntegerTransformer<>(transformerName, this, func));
  }

  public BooleanTransformer<OIN> transformToInBoolean(Function<? super OUT, Boolean> f) {
    return this.transform(f, (TX, func) -> new BooleanTransformer<>(transformerName, this, func));
  }

  @SuppressWarnings("unchecked")
  static <I, M, O> Function<I, O> chainFunctions(Function<I, ? extends M> func, Function<? super M, O> after) {
    /*
    if (isDummyFunction(func))
      // In case, func == null, <I> will become the same as M.
      // So, this cast is safe.
      return (Function<I, O>) (isDummyFunction(after) ? dummyFunction() : after);
    return isDummyFunction(after) ?
        (Function<I, O>) func :
        func.andThen(after);
     */
    if (isDummyFunction(func) && isDummyFunction(after))
      return dummyFunction();
    if (isDummyFunction(func))
      return (isDummyFunction(after)) ? dummyFunction() : (Function<I, O>) after;
    else
      return isDummyFunction(after) ? (Function<I, O>) func : func.andThen(after);
  }

  @SuppressWarnings("unchecked")
  private static <
      OIN,
      COUT,
      NOUT,
      B extends Transformer<B, OIN, COUT>,
      C extends BiFunction<
          B,
          Function<COUT, NOUT>,
          BB>,
      BB extends Transformer<BB, OIN, NOUT>>
  BB transform(B parent, Function<? super COUT, NOUT> f, C constructor) {
    return constructor.apply(parent, (Function<COUT, NOUT>) f);
  }

  public abstract Verifier<?, OIN, OUT> then();

  @Override
  public StringTransformer<OIN> asString() {
    return new StringTransformer<>(transformerName, this, Printables.function("treatAsString", v -> (String) v));
  }

  @Override
  public IntegerTransformer<OIN> asInteger() {
    return new IntegerTransformer<>(transformerName, this, Printables.function("treatAsInteger", v -> (Integer) v));
  }

  @Override
  public BooleanTransformer<OIN> asBoolean() {
    return new BooleanTransformer<>(transformerName, this, Printables.function("tratAsBoolean", v -> (Boolean) v));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <NOUT> ObjectTransformer<OIN, NOUT> asValueOf(NOUT value) {
    return new ObjectTransformer<>(transformerName, this, Printables.function("treatAs[NOUT]", v -> (NOUT) v));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> ListTransformer<OIN, E> asListOf(E value) {
    return new ListTransformer<>(transformerName, this, Printables.function("treatAsList", v -> (List<E>) v));
  }

  @SuppressWarnings("unchecked")
  @Override
  public <E> StreamTransformer<OIN, E> asStreamOf(E value) {
    return new StreamTransformer<>(transformerName, this, Printables.function("treatAsStream[NOUT]", v -> (Stream<E>) v));
  }
}
