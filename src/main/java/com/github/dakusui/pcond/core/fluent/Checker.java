package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.checkers.*;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.transformers.ThrowableTransformer;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory.TransformingPredicate;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.fluent.Fluents;
import com.github.dakusui.pcond.fluent.Statement;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.*;
import static com.github.dakusui.pcond.fluent.FluentUtils.chainFunctions;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static com.github.dakusui.pcond.internals.InternalUtils.*;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * A verifier interface.
 *
 * @param <V>   The type of the {@link Checker} implementation.
 * @param <OIN> The type of the original input value.
 * @param <T>   The type of the value to be verified by this object.
 */
public interface Checker<V extends Checker<V, OIN, T>, OIN, T> extends
    Matcher<OIN>,
    toPhraseFactory.ForChecker<OIN, T>,
    AsPhraseFactory.ForChecker<OIN>,
    Statement<OIN>,
    Evaluable.Transformation<OIN, T>,
    Identifiable {
  String transformerName();

  V addPredicate(Predicate<? super T> predicate);

  Function<? super OIN, ? extends T> function();

  Predicate<? super T> predicate();

  V anyOf();

  V allOf();

  @Override
  default OIN statementValue() {
    return originalInputValue();
  }

  @Override
  default Predicate<OIN> statementPredicate() {
    return toPredicate();
  }

  /**
   * A synonym of `build()` method.
   *
   * @return A predicate of `AS` built from this object.
   */
  <AS extends OIN> Predicate<AS> toPredicate();

  @SuppressWarnings("unchecked")
  default V verify(Predicate<? super T> predicate) {
    @SuppressWarnings("unchecked") V ret = (V) this;
    if (isDummyFunction(this.function()))
      ret = (V) ret.asObject();
    return ret.addPredicate(predicate);
  }

  @SuppressWarnings("unchecked")
  default V verifyWith(Function<V, Predicate<? super T>> predicateFunction) {
    this.verify(predicateFunction.apply((V) this));
    return (V) this;
  }

  @Override
  default StringChecker<OIN> asString() {
    return stringChecker(transformerName(), chainFunctions(this.function(), Functions.cast(String.class)), this.originalInputValue());
  }

  @Override
  default IntegerChecker<OIN> asInteger() {
    return integerChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Integer.class)), this.originalInputValue());
  }

  @Override
  default LongChecker<OIN> asLong() {
    return longChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Long.class)), this.originalInputValue());
  }

  @Override
  default ShortChecker<OIN> asShort() {
    return shortChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Short.class)), this.originalInputValue());
  }

  @Override
  default DoubleChecker<OIN> asDouble() {
    return doubleChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Double.class)), this.originalInputValue());
  }

  @Override
  default FloatChecker<OIN> asFloat() {
    return floatChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Float.class)), this.originalInputValue());
  }

  @Override
  default BooleanChecker<OIN> asBoolean() {
    return booleanChecker(transformerName(), chainFunctions(this.function(), Functions.cast(Boolean.class)), this.originalInputValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  default <NOUT> ObjectChecker<OIN, NOUT> asValueOf(NOUT value) {
    return objectChecker(transformerName(), chainFunctions(this.function(), Printables.function("treatAsIs", v -> (NOUT) v)), this.originalInputValue());
  }

  @Override
  default <E> ListChecker<OIN, E> asListOf(E value) {
    return listChecker(transformerName(), chainFunctions(this.function(), Functions.castTo(Fluents.value())), this.originalInputValue());
  }

  @Override
  default <E> StreamChecker<OIN, E> asStreamOf(E value) {
    return streamChecker(transformerName(), chainFunctions(this.function(), Functions.castTo(Fluents.value())), this.originalInputValue());
  }

  @Override
  default StringChecker<OIN> toStringWith(Function<T, String> function) {
    return stringChecker(this, function);
  }

  @Override
  default IntegerChecker<OIN> toIntegerWith(Function<T, Integer> function) {
    return integerChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default LongChecker<OIN> toLongWith(Function<T, Long> function) {
    return longChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default ShortChecker<OIN> toShortWith(Function<T, Short> function) {
    return shortChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default DoubleChecker<OIN> toDoubleWith(Function<T, Double> function) {
    return doubleChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default FloatChecker<OIN> toFloatWith(Function<T, Float> function) {
    return floatChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default BooleanChecker<OIN> toBooleanWith(Function<T, Boolean> function) {
    return booleanChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default <OUT> ObjectChecker<OIN, OUT> toObjectWith(Function<T, OUT> function) {
    return objectChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default <E> ListChecker<OIN, E> toListWith(Function<T, List<E>> function) {
    return listChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  @Override
  default <E> StreamChecker<OIN, E> toStreamWith(Function<T, Stream<E>> function) {
    return streamChecker(transformerName(), chainFunctions(this.function(), function), this.originalInputValue());
  }

  default V isNotNull() {
    return this.addPredicate(Predicates.isNotNull());
  }

  default V isNull() {
    return this.addPredicate(Predicates.isNull());
  }

  /**
   * Checks the object with an argument if they are "equal", using `equalTo` method.
   *
   * @return the updated object.
   */
  default V isEqualTo(Object anotherObject) {
    return this.addPredicate(Predicates.isEqualTo(anotherObject));
  }

  default V isSameReferenceAs(Object anotherObject) {
    return this.addPredicate(Predicates.isSameReferenceAs(anotherObject));
  }

  default V isInstanceOf(Class<?> klass) {
    return this.addPredicate(Predicates.isInstanceOf(klass));
  }

  default V invoke(String methodName, Object... args) {
    return this.addPredicate(Predicates.callp(MethodQuery.instanceMethod(parameter(), methodName, args)));
  }

  default V invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.addPredicate(Predicates.callp(MethodQuery.classMethod(klass, methodName, args)));
  }

  enum Factory {
    ;

    public static <V extends Checker<V, OIN, T>, OIN, T> StringChecker<OIN> stringChecker(Checker<V, OIN, T> checker, Function<T, String> function) {
      return stringChecker(checker.transformerName(), chainFunctions(checker.function(), function), checker.originalInputValue());
    }

    public static <OIN> StringChecker<OIN> stringChecker(
        String transformerName,
        Function<? super OIN, String> function,
        OIN originalInputValue) {
      return new StringChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN, OUT> ObjectChecker<OIN, OUT> objectChecker(ObjectTransformer<OIN, OUT> objectTransformer) {
      return objectChecker(objectTransformer.transformerName(), objectTransformer.function(), objectTransformer.originalInputValue());
    }

    public static <OIN, OUT> ObjectChecker<OIN, OUT> objectChecker(String transformerName, Function<? super OIN, ? extends OUT> function, OIN originalInptValue) {
      return new ObjectChecker.Impl<>(transformerName, function, originalInptValue);
    }

    public static <OIN, E> ListChecker<OIN, E> listChecker(String transformerName, Function<? super OIN, ? extends List<E>> function, OIN originalInputValue) {
      return new ListChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN> IntegerChecker<OIN> integerChecker(String transformerName, Function<? super OIN, ? extends Integer> function, OIN originalInputValue) {
      return new IntegerChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN> DoubleChecker<OIN> doubleChecker(String transformerName, Function<? super OIN, ? extends Double> function, OIN originalInputValue) {
      return new DoubleChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN> LongChecker<OIN> longChecker(String transformerName, Function<? super OIN, ? extends Long> function, OIN originalInputValue) {
      return new LongChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN> FloatChecker<OIN> floatChecker(String transformerName, Function<? super OIN, ? extends Float> function, OIN originalInputValue) {
      return new FloatChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN> ShortChecker<OIN> shortChecker(String transformerName, Function<? super OIN, ? extends Short> function, OIN originalInputValue) {
      return new ShortChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN, E> StreamChecker<OIN, E> streamChecker(String transformerName, Function<? super OIN, ? extends Stream<E>> function, OIN originalInputValue) {
      return new StreamChecker.Impl<>(transformerName, function, originalInputValue);
    }


    public static <OIN> BooleanChecker<OIN> booleanChecker(String transformerName, Function<? super OIN, ? extends Boolean> function, OIN originalInputValue) {
      return new BooleanChecker.Impl<>(transformerName, function, originalInputValue);
    }

    public static <OIN, OUT extends Throwable> ThrowableChecker<OIN, OUT> throwableChecker(ThrowableTransformer<OIN, OUT> throwableTransformer) {
      return new ThrowableChecker.Impl<>(throwableTransformer.transformerName(), throwableTransformer.function(), throwableTransformer.originalInputValue());
    }

    public static <OIN, OUT extends Throwable> ThrowableChecker<OIN, OUT> throwableChecker(String transformerName, Function<? super OIN, ? extends OUT> function, OIN originalInputValue) {
      return new ThrowableChecker.Impl<>(transformerName, function, originalInputValue);
    }
  }

  abstract class Base<V extends Checker<V, OIN, T>, OIN, T>
      implements Checker<V, OIN, T> {

    private final OIN                                originalInputValue;
    private final String                             transformerName;
    private final Function<? super OIN, ? extends T> transformerFunction;

    private final List<Predicate<T>> childPredicates = new LinkedList<>();

    TransformingPredicate<OIN, T> transformingPredicate;
    private JunctionType junctionType;

    public Base(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends T> transformerFunction) {
      this.originalInputValue = originalInputValue;
      this.transformerName = transformerName;
      this.transformerFunction = transformerFunction;
    }

    @Override
    public Evaluable<? super OIN> mapper() {
      this.updateTransformingPredicate();
      return this.transformingPredicate.mapper();
    }

    @Override
    public Evaluable<? super T> checker() {
      this.updateTransformingPredicate();
      return this.transformingPredicate.checker();
    }

    @Override
    public Optional<String> mapperName() {
      this.updateTransformingPredicate();
      return this.transformingPredicate.mapperName();
    }

    @Override
    public Optional<String> checkerName() {
      this.updateTransformingPredicate();
      return this.transformingPredicate.checkerName();
    }

    @Override
    public String transformerName() {
      return this.transformerName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V addPredicate(Predicate<? super T> predicate) {
      this.transformingPredicate = null;
      this.childPredicates.add((Predicate<T>) predicate);
      return (V) this;
    }

    @Override
    public Function<? super OIN, ? extends T> function() {
      return this.transformerFunction;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Predicate<? super T> predicate() {
      this.updateTransformingPredicate();
      return (Predicate<? super T>) this.transformingPredicate;
    }

    @Override
    public V anyOf() {
      this.transformingPredicate = null;
      this.junctionType = JunctionType.CONJUNCTION;
      return (V) this.create(this.originalInputValue, this.transformerName, this.transformerFunction);
    }

    @Override
    public V allOf() {
      this.transformingPredicate = null;
      this.junctionType = JunctionType.CONJUNCTION;
      return (V) this.create(this.originalInputValue, this.transformerName, this.transformerFunction);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <AS extends OIN> Predicate<AS> toPredicate() {
      updateTransformingPredicate();
      return (Predicate<AS>) this.transformingPredicate;
    }

    @Override
    public OIN originalInputValue() {
      return originalInputValue;
    }

    @Override
    public Object identityObject() {
      return asList(creator(), args());
    }

    @Override
    public Object creator() {
      return this.getClass();
    }

    @Override
    public List<Object> args() {
      return asList(this.originalInputValue, this.transformerFunction, this.junctionType, this.childPredicates);
    }

    @Override
    public boolean test(OIN oin) {
      updateTransformingPredicate();
      return this.transformingPredicate.test(oin);
    }

    public abstract V create(OIN originalInputValue, String transformerName, Function<? super OIN, ? extends T> function);


    private void updateTransformingPredicate() {
      if (this.transformingPredicate == null)
        this.transformingPredicate = createTransformingPredicate();
    }

    @SuppressWarnings("unchecked")
    private TransformingPredicate<OIN, T> createTransformingPredicate() {
      return (TransformingPredicate<OIN, T>) Predicates.transform(toEvaluableFunctionIfNecessary())
          .check(junction(this.childPredicates, l -> this.junctionType.connect(l)));
    }

    @SuppressWarnings("unchecked")
    private Function<? super OIN, ? extends T> toEvaluableFunctionIfNecessary() {
      return (Function<? super OIN, ? extends T>) toEvaluableWithFormatterIfNecessary(this.transformerFunction, f -> this.transformerName);
    }

    private static <T> Predicate<T> junction(List<Predicate<T>> predicates, Function<List<Predicate<T>>, Predicate<T>> connector) {
      requireState(requireNonNull(predicates), (List<Predicate<T>> l) -> !l.isEmpty(), () -> "No predicate was specified, yet.");
      if (predicates.size() == 1)
        return predicates.get(0);
      return connector.apply(predicates);
    }

    enum JunctionType {
      CONJUNCTION {
        @SuppressWarnings("unchecked")
        @Override
        <T> Predicate<T> connect(List<Predicate<T>> predicates) {
          return Predicates.allOf(predicates.toArray(new Predicate[0]));
        }
      },

      DISJUNCTION {
        @SuppressWarnings("unchecked")
        @Override
        <T> Predicate<T> connect(List<Predicate<T>> predicates) {
          return Predicates.anyOf(predicates.toArray(new Predicate[0]));
        }
      };

      abstract <T> Predicate<T> connect(List<Predicate<T>> predicates);
    }
  }
}
