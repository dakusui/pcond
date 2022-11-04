package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.transformers.ThrowableTransformer;
import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.checkers.*;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Transformer.chainFunctions;
import static com.github.dakusui.pcond.core.fluent.Checker.Factory.*;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.internals.InternalUtils.*;

/**
 * A verifier interface.
 *
 * @param <V>    The type of the {@link Checker} implementation.
 * @param <OIN>  The type of the original input value.
 * @param <T>The type of the value to be verified by this object.
 */
public interface Checker<V extends Checker<V, OIN, T>, OIN, T>
    extends
    Identifiable,
    Statement<OIN>,
    Evaluable.Transformation<OIN, T>,
    IntoPhraseFactory.ForChecker<OIN, T>,
    AsPhraseFactory.ForChecker<OIN> {

  String transformerName();

  V predicate(Predicate<? super T> predicate);

  Function<? super OIN, ? extends T> function();

  Predicate<? super T> predicate();

  OIN originalInputValue();

  default OIN statementValue() {
    return originalInputValue();
  }

  default Predicate<OIN> statementPredicate() {
    return toPredicate();
  }

  default V testPredicate(Predicate<? super T> predicate) {
    return this.predicate(predicate);
  }

  default Predicate<? super OIN> build() {
    return PrintablePredicateFactory.TransformingPredicate.Factory.create(
            this.transformerName(),
            this.transformerName() != null ?
                "THEN" :
                "VERIFY",
            this.function())
        .check(this.predicate());
  }

  /**
   * A synonym of `build()` method.
   *
   * @return A predicate of `AS` built from this object.
   */
  @SuppressWarnings("unchecked")
  default <AS> Predicate<AS> toPredicate() {
    return (Predicate<AS>) build();
  }

  default V create() {
    return create(this.transformerName(), this.function(), this.predicate(), this.originalInputValue());
  }

  V create(String transformerName, Function<? super OIN, ? extends T> function, Predicate<? super T> predicate, OIN originalInputValue);

  @SuppressWarnings("unchecked")
  default V verifyWith(Predicate<? super T> predicate) {
    @SuppressWarnings("unchecked") V ret = (V) this;
    if (isDummyFunction(this.function()))
      ret = (V) ret.asObject();
    return ret.predicate(predicate);
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  @Override
  default StringChecker<OIN> asString() {
    return stringChecker(this, Function.class.cast(Functions.cast(String.class)));
  }

  @Override
  default IntegerChecker<OIN> asInteger() {
    return integerChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Integer.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default LongChecker<OIN> asLong() {
    return longChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Long.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default ShortChecker<OIN> asShort() {
    return shortChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Short.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default DoubleChecker<OIN> asDouble() {
    return doubleChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Double.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default FloatChecker<OIN> asFloat() {
    return floatChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Float.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default BooleanChecker<OIN> asBoolean() {
    return booleanChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Boolean.class)), dummyPredicate(), this.originalInputValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  default <NOUT> ObjectChecker<OIN, NOUT> asValueOf(NOUT value) {
    return objectChecker(transformerName(), chainFunctions(this.function(), Printables.function("treatAsIs", v -> (NOUT) v)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> ListChecker<OIN, E> asListOf(E value) {
    return listChecker(transformerName(), chainFunctions(this.function(), Functions.castTo(Fluents.value())), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> StreamChecker<OIN, E> asStreamOf(E value) {
    return streamChecker(transformerName(), chainFunctions(this.function(), Functions.castTo(Fluents.value())), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default StringChecker<OIN> intoStringWith(Function<T, String> function) {
    return stringChecker(this, function);
  }

  @Override
  default IntegerChecker<OIN> intoIntegerWith(Function<T, Integer> function) {
    return integerChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default LongChecker<OIN> intoLongWith(Function<T, Long> function) {
    return longChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default ShortChecker<OIN> intoShortWith(Function<T, Short> function) {
    return shortChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default DoubleChecker<OIN> intoDoubleWith(Function<T, Double> function) {
    return doubleChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default FloatChecker<OIN> intoFloatWith(Function<T, Float> function) {
    return floatChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default BooleanChecker<OIN> intoBooleanWith(Function<T, Boolean> function) {
    return booleanChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <OUT> ObjectChecker<OIN, OUT> intoObjectWith(Function<T, OUT> function) {
    return objectChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> ListChecker<OIN, E> intoListWith(Function<T, List<E>> function) {
    return listChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> StreamChecker<OIN, E> intoStreamWith(Function<T, Stream<E>> function) {
    return streamChecker(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  default V isNotNull() {
    return this.predicate(Predicates.isNotNull());
  }

  default V isNull() {
    return this.predicate(Predicates.isNull());
  }

  /**
   * Checks the object with an argument if they are "equal", using `equalTo` method.
   *
   * @return the updated object.
   */
  default V isEqualTo(Object anotherObject) {
    return this.predicate(Predicates.isEqualTo(anotherObject));
  }

  default V isSameReferenceAs(Object anotherObject) {
    return this.predicate(Predicates.isSameReferenceAs(anotherObject));
  }

  default V isInstanceOf(Class<?> klass) {
    return this.predicate(Predicates.isInstanceOf(klass));
  }

  default V invoke(String methodName, Object... args) {
    return this.predicate(Predicates.callp(MethodQuery.instanceMethod(parameter(), methodName, args)));
  }

  default V invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.predicate(Predicates.callp(MethodQuery.classMethod(klass, methodName, args)));
  }

  enum Factory {
    ;

    public static <V extends Checker<V, OIN, T>, OIN, T> StringChecker<OIN> stringChecker(Checker<V, OIN, T> checker, Function<T, String> function) {
      return stringChecker(checker.transformerName(), chainFunctions(checker.function(), function), dummyPredicate(), checker.originalInputValue());
    }

    public static <OIN> StringChecker<OIN> stringChecker(
        String transformerName,
        Function<? super OIN, String> function,
        Predicate<? super String> predicate,
        OIN originalInputValue) {
      return new StringChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, OUT> ObjectChecker<OIN, OUT> objectChecker(ObjectTransformer<OIN, OUT> objectTransformer) {
      return objectChecker(objectTransformer.transformerName(), objectTransformer.function(), dummyPredicate(), objectTransformer.originalInputValue());
    }

    public static <OIN, OUT> ObjectChecker<OIN, OUT> objectChecker(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInptValue) {
      return new ObjectChecker.Impl<>(transformerName, function, predicate, originalInptValue);
    }

    public static <OIN, E> ListChecker<OIN, E> listChecker(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      return new ListChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> IntegerChecker<OIN> integerChecker(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return new IntegerChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> DoubleChecker<OIN> doubleChecker(String transformerName, Function<? super OIN, ? extends Double> function, Predicate<? super Double> predicate, OIN originalInputValue) {
      return new DoubleChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> LongChecker<OIN> longChecker(String transformerName, Function<? super OIN, ? extends Long> function, Predicate<? super Long> predicate, OIN originalInputValue) {
      return new LongChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> FloatChecker<OIN> floatChecker(String transformerName, Function<? super OIN, ? extends Float> function, Predicate<? super Float> predicate, OIN originalInputValue) {
      return new FloatChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> ShortChecker<OIN> shortChecker(String transformerName, Function<? super OIN, ? extends Short> function, Predicate<? super Short> predicate, OIN originalInputValue) {
      return new ShortChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, E> StreamChecker<OIN, E> streamChecker(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      return new StreamChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }


    public static <OIN> BooleanChecker<OIN> booleanChecker(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      return new BooleanChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, OUT extends Throwable> ThrowableChecker<OIN, OUT> throwableChecker(ThrowableTransformer<OIN, OUT> throwableTransformer) {
      return new ThrowableChecker.Impl<>(throwableTransformer.transformerName(), throwableTransformer.function(), dummyPredicate(), throwableTransformer.originalInputValue());
    }

    public static <OIN, OUT extends Throwable> ThrowableChecker<OIN, OUT> throwableChecker(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInputValue) {
      return new ThrowableChecker.Impl<>(transformerName, function, predicate, originalInputValue);
    }
  }

  abstract class Base<V extends Checker<V, OIN, T>, OIN, T>
      extends PrintablePredicateFactory.TransformingPredicate<OIN, T>
      implements Checker<V, OIN, T> {
    protected final String                             transformerName;
    private final   Function<? super OIN, ? extends T> function;
    private final   OIN                                originalInputValue;
    private         Predicate<? super T>               predicate;

    protected Base(String transformerName, Function<? super OIN, ? extends T> function, Predicate<? super T> predicate, OIN originalInputValue) {
      super(predicate, function);
      this.transformerName = transformerName;
      this.function = function;
      this.predicate = predicate; // this field can be null, when the first verifier starts building.
      this.originalInputValue = originalInputValue;
    }

    public String transformerName() {
      return this.transformerName;
    }

    public V predicate(Predicate<? super T> predicate) {
      if (isDummyPredicate(this.predicate))
        this.predicate = predicate;
      else
        this.predicate = Predicates.allOf(this.predicate, predicate);
      return this.create();
    }

    public Function<? super OIN, ? extends T> function() {
      return this.function;
    }

    public Predicate<? super T> predicate() {
      return this.predicate;
    }

    @Override
    public OIN originalInputValue() {
      return this.originalInputValue;
    }
  }
}
