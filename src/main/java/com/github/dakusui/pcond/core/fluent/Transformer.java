package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.*;
import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Fluent.value;
import static com.github.dakusui.pcond.core.fluent.Transformer.Factory.*;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyFunction;
import static com.github.dakusui.pcond.internals.InternalUtils.isDummyFunction;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * The transformer interface.
 *
 * @param <TX>  The type of the {@link Transformer} implementation.
 * @param <OIN> The type of the original input value.
 * @param <OUT> The output of the transformation.
 */
public interface Transformer<
    TX extends Transformer<TX, OIN, OUT>,
    OIN,
    OUT> extends
    Matcher<OIN>,
    AsPhraseFactory.ForTransformer<OIN> {
  @SuppressWarnings("unchecked")
  static <
      OIN,
      COUT,
      NOUT,
      B extends Transformer<
          B,
          OIN,
          COUT>,
      C extends BiFunction<
          B,
          Function<COUT, NOUT>,
          BB>,
      BB extends Transformer<
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
      BB extends Transformer<BB, OIN, NOUT>>
  BB transform(Function<? super OUT, NOUT> f, BiFunction<TX, Function<OUT, NOUT>, BB> factory) {
    return transform((TX) this, f, factory);
  }

  @SuppressWarnings("unchecked")
  default Statement<OIN> thenVerifyAllOf(List<Function<? super TX, Statement<OIN>>> funcs) {
    return Fluents.statementAllOf(
        Transformer.this.originalInputValue(),
        funcs.stream()
            .map(each -> each.apply((TX) Transformer.this))
            .collect(toList()));
  }

  @SuppressWarnings("unchecked")
  default <NOUT> Checker<?, OIN, NOUT> thenVerifyWithAllOf(List<? extends Predicate<? super NOUT>> predicates) {
    return this.thenVerifyWith(Predicates.allOf(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default <NOUT> Checker<?, OIN, NOUT> thenVerifyWithAnyOf(List<? extends Predicate<? super NOUT>> predicates) {
    return this.thenVerifyWith(Predicates.anyOf(predicates.toArray(new Predicate[0])));
  }

  @SuppressWarnings("unchecked")
  default <NOUT> Checker<?, OIN, NOUT> thenVerifyWith(Predicate<? super NOUT> predicate) {
    return this.then().asValueOf((NOUT) value()).verifyWith(predicate);
  }


  default <O> ObjectTransformer<OIN, O> exercise(Function<? super OUT, O> f) {
    return applyFunction(f);
  }

  default <O> ObjectTransformer<OIN, O> applyFunction(Function<? super OUT, O> f) {
    return transformToObject(f);
  }

  default <O extends Throwable> ThrowableTransformer<OIN, O> expectException(Class<O> exceptionClass, Function<? super OUT, ?> f) {
    requireNonNull(exceptionClass);
    return this.transform(Functions.expectingException(exceptionClass, f), (TX, func) -> throwableTransformer(this, func));
  }

  default <O> ObjectTransformer<OIN, O> transformToObject(Function<? super OUT, O> f) {
    return this.transform(f, (TX, func) -> objectTransformer(this, func));
  }

  default StringTransformer<OIN> transformToString(Function<OUT, String> f) {
    return this.transform(f, (TX, func) -> stringTransformer(this, func));
  }

  default <E> ListTransformer<OIN, E> transformToList(Function<OUT, List<E>> f) {
    return this.transform(f, (TX, func) -> listTransformer(this, func));
  }

  default <E> StreamTransformer<OIN, E> transformToStream(Function<OUT, Stream<E>> f) {
    return this.transform(f, (TX, func) -> streamTransformer(this, func));
  }

  default IntegerTransformer<OIN> transformToInteger(Function<? super OUT, Integer> f) {
    return this.transform(f, (TX, func) -> integerTransformer(this, func));
  }

  default IntegerTransformer<OIN> transformToLong(Function<? super OUT, Integer> f) {
    return this.transform(f, (TX, func) -> integerTransformer(this, func));
  }

  default ShortTransformer<OIN> transformToShort(Function<? super OUT, Short> f) {
    return this.transform(f, (TX, func) -> shortTransformer(this, func));
  }

  default FloatTransformer<OIN> transformToFloat(Function<? super OUT, Float> f) {
    return this.transform(f, (TX, func) -> floatTransformer(this, func));
  }

  default DoubleTransformer<OIN> transformToDouble(Function<? super OUT, Double> f) {
    return this.transform(f, (TX, func) -> doubleTransformer(this, func));
  }

  default BooleanTransformer<OIN> transformToInBoolean(Function<? super OUT, Boolean> f) {
    return this.transform(f, (TX, func) -> booleanTransformer(this, func));
  }

  /**
   * A method to finish the transformation stage and return a {@link Checker} object.
   *
   * @return A verifier object.
   */
  Checker<?, OIN, OUT> then();

  @SuppressWarnings("unchecked")
  default TX peek(Consumer<OUT> consumer) {
    return (TX) this.applyFunction(v -> {
      consumer.accept(v);
      return v;
    });
  }

  @Override
  default StringTransformer<OIN> asString() {
    return stringTransformer(this, Printables.function("treatAsString", v -> (String) v));
  }

  @Override
  default IntegerTransformer<OIN> asInteger() {
    return integerTransformer(this, Printables.function("treatAsInteger", v -> (Integer) v));
  }

  @Override
  default LongTransformer<OIN> asLong() {
    return longTransformer(this, Printables.function("treatAsLong", v -> (Long) v));
  }

  @Override
  default ShortTransformer<OIN> asShort() {
    return shortTransformer(this, Printables.function("treatAsShort", v -> (Short) v));
  }

  @Override
  default DoubleTransformer<OIN> asDouble() {
    return doubleTransformer(this, Printables.function("treatAsDouble", v -> (Double) v));
  }

  @Override
  default FloatTransformer<OIN> asFloat() {
    return floatTransformer(this, Printables.function("treatAsShort", v -> (Float) v));
  }

  @Override
  default BooleanTransformer<OIN> asBoolean() {
    return booleanTransformer(this, Printables.function("treatAsBoolean", v -> (Boolean) v));
  }

  @SuppressWarnings("unchecked")
  default <OUT2 extends Throwable> ThrowableTransformer<OIN, OUT2> asThrowable() {
    return throwableTransformer(this, Printables.function("treatAsThrowable", v -> (OUT2) v));
  }

  @Override
  @SuppressWarnings("unchecked")
  default <NOUT> ObjectTransformer<OIN, NOUT> asValueOf(NOUT value) {
    return objectTransformer(this, Printables.function("treatAs[NOUT]", v -> (NOUT) v));
  }

  @Override
  @SuppressWarnings("unchecked")
  default <E> ListTransformer<OIN, E> asListOf(E value) {
    return listTransformer(this, Printables.function("treatAsList", v -> (List<E>) v));
  }

  @SuppressWarnings("unchecked")
  @Override
  default <E> StreamTransformer<OIN, E> asStreamOf(E value) {
    return streamTransformer(this, Printables.function("treatAsStream[NOUT]", v -> (Stream<E>) v));
  }

  enum Factory {
    ;

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> StringTransformer<OIN> stringTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, String> func) {
      return new StringTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> BooleanTransformer<OIN> booleanTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Boolean> function) {
      return new BooleanTransformer.Impl<>(transformer.transformerName(), transformer, function, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> IntegerTransformer<OIN> integerTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Integer> func) {
      return new IntegerTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> LongTransformer<OIN> longTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Long> func) {
      return new LongTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> ShortTransformer<OIN> shortTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Short> func) {
      return new ShortTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> DoubleTransformer<OIN> doubleTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Double> func) {
      return new DoubleTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT> FloatTransformer<OIN> floatTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Float> func) {
      return new FloatTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }


    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT, E> StreamTransformer<OIN, E> streamTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, Stream<E>> func) {
      return new StreamTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT, E> ListTransformer<OIN, E> listTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, List<E>> func) {
      return new ListTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT, O> ObjectTransformer<OIN, O> objectTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, O> func) {
      return new ObjectTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }

    public static <TX extends Transformer<TX, OIN, OUT>, OIN, OUT, O extends Throwable> ThrowableTransformer<OIN, O> throwableTransformer(Transformer<TX, OIN, OUT> transformer, Function<OUT, O> func) {
      return new ThrowableTransformer.Impl<>(transformer.transformerName(), transformer, func, transformer.originalInputValue());
    }
  }

  /**
   * Method names start with `as` or contain `As` suggests that the methods should be
   * used when you know the type of the object you are treating at the line of your code.
   *
   * One starts with `into` or contains `Into` should be used for objects you need to
   * apply a function in order to convert it to treat it in the following lines.
   *
   * @param <TX>  The type of this object.
   * @param <OIN> Original input type.
   * @param <OUT> (Current) Output type.
   */
  abstract class Base<
      TX extends Transformer<TX, OIN, OUT>,
      OIN, OUT>
      implements
      Transformer<TX, OIN, OUT> {
    private final Function<OIN, OUT> function;
    private final String             transformerName;

    private final OIN originalInputValue;

    /**
     * Constructs an instance of this class.
     *
     * @param <IN>               The type of the input.
     * @param transformerName    THe name of transformer. This can be {@code null}.
     * @param parent             The parent of the new transformer. {@code null} if it is a root.
     * @param function           A function with which a given value is converted.
     * @param originalInputValue An original input value, if available. Otherwise {@code null}.
     */
    @SuppressWarnings("unchecked")
    public <IN> Base(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
      this.transformerName = transformerName;
      this.function = (Function<OIN, OUT>) Transformer.chainFunctions(parent == null ? dummyFunction() : parent.function(), function);
      this.originalInputValue = originalInputValue;
    }

    @Override
    public Function<? super OIN, ? extends OUT> function() {
      return this.function;
    }

    @Override
    public String transformerName() {
      return this.transformerName;
    }

    @Override
    public OIN originalInputValue() {
      return this.originalInputValue;
    }
  }
}
