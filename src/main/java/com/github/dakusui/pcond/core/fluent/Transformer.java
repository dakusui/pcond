package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.core.fluent.verifiers.*;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * @param <TX>  The type of this object.
 * @param <OIN> Original input type.
 * @param <OUT> (Current) Output type.
 */
public abstract class Transformer<TX extends Transformer<TX, OIN, OUT>, OIN, OUT> {
  private final Function<OIN, OUT> function;

  /**
   *
   */
  @SuppressWarnings("unchecked")
  //  public Transformer(Function<? super OIN, ? extends OUT> function) {
  //    this.function = (Function<OIN, OUT>) function;
  //  }

  public <IN> Transformer(Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
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


  public StringTransformer<OIN> transformToString(Function<OUT, String> f) {
    return this.transform(f, (TX, func) -> new StringTransformer<>(this, func));
  }

  public <O> ObjectTransformer<OIN, O> transformToObject(Function<? super OUT, O> f) {
    return this.transform(f, (TX, func) -> new ObjectTransformer<>(this, func));
  }

  public <E> ListTransformer<OIN, E> transformToList(Function<OUT, List<E>> f) {
    return this.transform(f, (TX, func) -> new ListTransformer<>(this, func));
  }

  public <E> StreamTransformer<OIN, E> transformToStream(Function<OUT, Stream<E>> f) {
    return this.transform(f, (TX, func) -> new StreamTransformer<>(this, func));
  }

  public IntegerTransformer<OIN> transformToInteger(Function<? super OUT, Integer> f) {
    return this.transform(f, (TX, func) -> new IntegerTransformer<>(this, func));
  }

  public <NOUT, BB extends Transformer<BB, OIN, NOUT>> BB chainTo(Class<BB> transformer, Function<OUT, NOUT> map) {
    return this.transformWith(transformer, map);
  }

  @SuppressWarnings("unchecked")
  public TX chain(Function<OUT, OUT> map) {
    return this.transformWith((Class<TX>) this.getClass(), map);
  }

  public <NOUT, C extends Transformer<C, OIN, NOUT>> C transformWith(Class<C> klass, Function<OUT, NOUT> f) {
    Objects.requireNonNull(klass);
    return this.transform(f, (TX, func) -> {
      try {
        C c = klass.getDeclaredConstructor(Function.class).newInstance(chainFunctions(this.function, f));
        return c;
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
        throw new RuntimeException(e);
      } catch (InvocationTargetException | InstantiationException |
               IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @SuppressWarnings("unchecked")
  private static <I, M, O> Function<I, O> chainFunctions(Function<I, ? extends M> func, Function<? super M, O> after) {
    if (func == null)
      // In case, func == null, I will become the same as M.
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

  public <E> StreamVerifier<OIN, E> thenAsStream(Function<OUT, Stream<E>> converter) {
    return new StreamVerifier<>(requireNonNull(this.function).andThen(converter));
  }


  ////
  // BEGIN: Methods for java.lang.Object come here.
  void method() {
    Functions.stringify();
    Functions.cast(null);
    Functions.call(null);
    Functions.classMethod(null, null);
  }

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
}
