package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.fluent.verifiers.*;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.internals.InternalChecks;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static java.util.Objects.requireNonNull;

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
    implements Matcher<OIN, OUT> {
  private final Function<OIN, OUT> function;
  private final String             transformerName;

  /**
   *
   */
  @SuppressWarnings("unchecked")
  //  public Transformer(Function<? super OIN, ? extends OUT> function) {
  //    this.function = (Function<OIN, OUT>) function;
  //  }

  public <IN> Transformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    this.transformerName = transformerName;
    this.function = (Function<OIN, OUT>) chainFunctions(parent == null ? null : parent.function, function);
  }

  @SuppressWarnings("unchecked")
  public <
      NOUT,
      BB extends Transformer<BB, OIN, NOUT>>
  BB transform(Function<? super OUT, NOUT> f, BiFunction<TX, Function<OUT, NOUT>, BB> factory) {
    return Transformer.transform((TX) this, f, factory);
  }

  public Function<OIN, ? extends OUT> function() {
    return this.function;
  }

  @SafeVarargs
  public final <NOUT> Verifier<?, OIN, NOUT> tee(Predicate<? super NOUT>... predicates) {
    return this.<NOUT>thenAsObject().allOf(predicates);
  }

  public <O> ObjectTransformer<OIN, O> exercise(Function<? super OUT, O> f) {
    return applyFunction(f);
  }

  public <O> ObjectTransformer<OIN, O> exercise(String name, Function<? super OUT, O> f) {
    return exercise(Printables.function(name, f));
  }

  public <O> ObjectTransformer<OIN, O> applyFunction(Function<? super OUT, O> f) {
    return transformToObject(f);
  }

  public <O> ObjectTransformer<OIN, O> applyFunction(String name, Function<? super OUT, O> f) {
    return applyFunction(Printables.function(name, f));
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

  @SuppressWarnings("unchecked")
  private static <I, M, O> Function<I, O> chainFunctions(Function<I, ? extends M> func, Function<? super M, O> after) {
    if (func == null)
      // In case, func == null, <I> will become the same as M.
      // So, this cast is safe.
      return (Function<I, O>) after;
    return func.andThen(after);
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

  public abstract Verifier<?, OIN, OUT> then(Function<OUT, OUT> converter);

  @SuppressWarnings("unchecked")
  public <NOUT> ObjectVerifier<OIN, NOUT> thenAsObject() {
    return this.thenAsObject(Printables.function("treatAsObject[NOUT]", v -> (NOUT) v));
  }

  public <NOUT> ObjectVerifier<OIN, NOUT> thenAsObject(Function<OUT, NOUT> function) {
    return new ObjectVerifier<>(transformerName, chainFunctions(this.function, requireNonNull(function)), dummyPredicate());
  }

  public StringVerifier<OIN> thenAsString() {
    return this.thenAsString(Printables.function("treatAsString", v -> (String) v));
  }

  public StringVerifier<OIN> thenAsString(Function<OUT, String> toString) {
    requireFunctionIsSet("asString()");
    return new StringVerifier<>(transformerName, chainFunctions(this.function, toString), dummyPredicate());
  }

  public <E> ListVerifier<OIN, E> thenAsList(Function<OUT, List<E>> converter) {
    requireFunctionIsSet("asList(Function<OUT, List<E>)");
    return new ListVerifier<>(transformerName, chainFunctions(this.function, converter), dummyPredicate());
  }

  /**
   * You should only use this method  when you know that the `OIN` is already an
   * instance of {@link Integer}.
   *
   * @return An `IntegerVerifier` object.
   */
  public IntegerVerifier<OIN> thenAsInteger() {
    return this.thenAsInteger(Printables.function("treatAsInteger", v -> (Integer) v));
  }

  public IntegerVerifier<OIN> thenAsInteger(Function<OUT, Integer> converter) {
    requireFunctionIsSet("asInteger(Function<OUT, Integer>)");
    return new IntegerVerifier<>(transformerName, chainFunctions(this.function, converter), dummyPredicate());
  }

  /**
   * You should only use this method  when you know that the `OIN` is already an
   * instance of {@link Integer}.
   *
   * @return An `IntegerVerifier` object.
   */
  @SuppressWarnings("unchecked")
  public <E> StreamVerifier<OIN, E> thenAsStream() {
    return this.thenAsStream(Printables.function("treatAsStream", v -> (Stream<E>) v));
  }

  public <E> StreamVerifier<OIN, E> thenAsStream(Function<OUT, Stream<E>> converter) {
    requireFunctionIsSet("asStream(Function<OUT, Stream<E>)");
    return new StreamVerifier<>(transformerName, chainFunctions(this.function, converter), dummyPredicate());
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> as(NOUT value) {
    return asValueOf(value);
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> asValueOfClass(Class<NOUT> klass) {
    return asValueOf(value());
  }

  @SuppressWarnings("unchecked")
  public <NOUT> ObjectTransformer<OIN, NOUT> asValueOf(NOUT value) {
    return toObjectWith(Printables.function("treatAs[NOUT]", v -> (NOUT) v));
  }

  public StringTransformer<OIN> asString() {
    return toStringWith(Printables.function("treatAsString", v -> (String) v));
  }

  @SuppressWarnings({ "unchecked", "RedundantCast" })
  public <E> ListTransformer<OIN, E> asList() {
    return asListOf((E) value());
  }

  @SuppressWarnings("unchecked")
  public <E> ListTransformer<OIN, E> asListOf(E value) {
    return toListWith(Printables.function("treatAsList", v -> (List<E>) v));
  }

  @SuppressWarnings({ "unchecked", "RedundantCast" })
  public <E> ListTransformer<OIN, E> asListOfClass(Class<E> klass) {
    return asListOf((E) value());
  }

  public IntegerTransformer<OIN> asInteger() {
    return toIntegerWith(Printables.function("treatAsInteger", v -> (Integer) v));
  }

  @SuppressWarnings("unchecked")
  public <E> StreamTransformer<OIN, E> asStream() {
    return toStreamWith(Printables.function("treatAsStream", v -> (Stream<E>) v));
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> toObjectWith(Function<OUT, NOUT> function) {
    return new ObjectTransformer<>(null, this, function);
  }

  public StringTransformer<OIN> toStringWith(Function<OUT, String> toString) {
    return new StringTransformer<>(null, this, toString);
  }

  public <E> ListTransformer<OIN, E> toListWith(Function<OUT, List<E>> converter) {
    return new ListTransformer<>(null, this, converter);
  }

  public IntegerTransformer<OIN> toIntegerWith(Function<OUT, Integer> converter) {
    return new IntegerTransformer<>(null, this, converter);
  }

  public <E> StreamTransformer<OIN, E> toStreamWith(Function<OUT, Stream<E>> converter) {
    return new StreamTransformer<>(null, this, converter);
  }

  private void requireFunctionIsSet(String methodName) {
    InternalChecks.requireState(this, v -> v.function != null, () -> "You should first add an action to be exercised before calling this method. Perhaps you wanted to call '" + methodName + "'?");
  }

  ////
  // BEGIN: Methods for java.lang.Object come here.

  /**
   * Corresponds to {@code toString()} method.
   *
   * @return this object the method appended.
   */
  public StringTransformer<OIN> stringify() {
    return this.transformToString(Functions.stringify());
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> cast(Class<NOUT> klass) {
    return this.transformToObject(Functions.cast(klass));
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> invoke(String methodName, Object... args) {
    return this.transformToObject(Functions.call(MethodQuery.instanceMethod(
        Functions.parameter(), methodName, args)));
  }

  public <NOUT> ObjectTransformer<OIN, NOUT> invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.transformToObject(Functions.call(MethodQuery.classMethod(klass, methodName, args)));
  }
  // END: Methods for java.lang.Object come here.
  ////

  public static <T> Predicate<? super T> dummyPredicate() {
    return Printables.predicate("DUMMY:ALWAYSTRUE", Predicates.alwaysTrue());
  }
}
