package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Printables;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyFunction;
import static com.github.dakusui.pcond.internals.InternalUtils.isDummyFunction;

public interface ITransformer<TX extends ITransformer<TX, OIN, OUT>, OIN, OUT> extends Matcher<OIN, OUT>, AsPhraseFactory.ForTransformer<OIN> {
  @SuppressWarnings("unchecked")
  static <
      OIN,
      COUT,
      NOUT,
      B extends ITransformer<
          B,
          OIN,
          COUT>,
      C extends BiFunction<
          B,
          Function<COUT, NOUT>,
          BB>,
      BB extends ITransformer<
          BB,
          OIN,
          NOUT>>
  BB transform(B parent, Function<? super COUT, NOUT> f, C constructor) {
    return constructor.apply(parent, (Function<COUT, NOUT>) f);
  }

  @SuppressWarnings("unchecked")
  static <I, M, O> Function<I, O> chainFunctions(Function<I, ? extends M> func, Function<? super M, O> after) {
    if (isDummyFunction(func) && isDummyFunction(after))
      return dummyFunction();
    if (isDummyFunction(func))
      return (isDummyFunction(after)) ? dummyFunction() : (Function<I, O>) after;
    else
      return isDummyFunction(after) ? (Function<I, O>) func : func.andThen(after);
  }

  Function<? super OIN, ? extends OUT> function();

  String transformerName();

  OIN originalInputValue();

  @SuppressWarnings("unchecked")
  default <
      NOUT,
      BB extends ITransformer<BB, OIN, NOUT>>
  BB transform(Function<? super OUT, NOUT> f, BiFunction<TX, Function<OUT, NOUT>, BB> factory) {
    return transform((TX) this, f, factory);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> Verifier<?, OIN, NOUT> allOf(Predicate<? super NOUT>... predicates) {
    return this.then().asValueOf((NOUT) value()).allOf(predicates);
  }

  @SuppressWarnings("unchecked")
  default <NOUT> Verifier<?, OIN, NOUT> anyOf(Predicate<? super NOUT>... predicates) {
    return this.then().asValueOf((NOUT) value()).anyOf(predicates);
  }

  default <O> IObjectTransformer<OIN, O> exercise(Function<? super OUT, O> f) {
    return applyFunction(f);
  }

  default <O> IObjectTransformer<OIN, O> applyFunction(Function<? super OUT, O> f) {
    return transformToObject(f);
  }

  default <O> IObjectTransformer<OIN, O> transformToObject(Function<? super OUT, O> f) {
    return this.transform(f, (TX, func) -> new ObjectTransformer<>(transformerName(), this, func, originalInputValue()));
  }

  default IStringTransformer<OIN> transformToString(Function<OUT, String> f) {
    return this.transform(f, (TX, func) -> stringTransformer(this, func));
  }

  static <TX extends ITransformer<TX, OIN, OUT>, OIN, OUT> IStringTransformer<OIN> stringTransformer(ITransformer<TX, OIN, OUT> transformer, Function<OUT, String> func) {
    return new StringTransformer<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
  }

  default <E> ListTransformer<OIN, E> transformToList(Function<OUT, List<E>> f) {
    return this.transform(f, (TX, func) -> new ListTransformer<>(transformerName(), this, func, originalInputValue()));
  }

  default <E> StreamTransformer<OIN, E> transformToStream(Function<OUT, Stream<E>> f) {
    return this.transform(f, (TX, func) -> new StreamTransformer<>(transformerName(), this, func, originalInputValue()));
  }

  default IIntegerTransformer<OIN> transformToInteger(Function<? super OUT, Integer> f) {
    return this.transform(f, (TX, func) -> new IntegerTransformer<>(transformerName(), this, func, originalInputValue()));
  }

  default IBooleanTransformer<OIN> transformToInBoolean(Function<? super OUT, Boolean> f) {
    return this.transform(f, (TX, func) -> new BooleanTransformer<>(transformerName(), this, func, originalInputValue()));
  }

  IVerifier<?, OIN, OUT> then();

  @SuppressWarnings("unchecked")
  default TX peek(Consumer<OUT> consumer) {
    applyFunction(v -> {
      consumer.accept(v);
      return v;
    });
    return (TX) this;
  }

  @Override
  default StringTransformer<OIN> asString() {
    return (StringTransformer<OIN>) stringTransformer(this, Printables.function("treatAsString", v -> (String) v));
  }

  @Override
  default IntegerTransformer<OIN> asInteger() {
    return new IntegerTransformer<>(transformerName(), this, Printables.function("treatAsInteger", v -> (Integer) v), originalInputValue());
  }

  @Override
  default BooleanTransformer<OIN> asBoolean() {
    return new BooleanTransformer<>(transformerName(), this, Printables.function("tratAsBoolean", v -> (Boolean) v), originalInputValue());
  }

  @Override
  @SuppressWarnings("unchecked")
  default <NOUT> IObjectTransformer<OIN, NOUT> asValueOf(NOUT value) {
    return new ObjectTransformer<>(transformerName(), this, Printables.function("treatAs[NOUT]", v -> (NOUT) v), originalInputValue());
  }

  @Override
  @SuppressWarnings("unchecked")
  default <E> ListTransformer<OIN, E> asListOf(E value) {
    return new ListTransformer<>(transformerName(), this, Printables.function("treatAsList", v -> (List<E>) v), originalInputValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  default <E> StreamTransformer<OIN, E> asStreamOf(E value) {
    return new StreamTransformer<>(transformerName(), this, Printables.function("treatAsStream[NOUT]", v -> (Stream<E>) v), originalInputValue());
  }
}
