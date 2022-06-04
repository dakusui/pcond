package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.MoreFluents;
import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.transformers.ObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.*;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.fluent.Transformer.chainFunctions;
import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.*;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.internals.InternalUtils.*;

public interface Verifier<V extends Verifier<V, OIN, T>, OIN, T>
    extends
    Identifiable,
    MoreFluents.Statement<OIN>,
    Evaluable.Transformation<OIN, T>,
    IntoPhraseFactory.ForVerifier<OIN, T>,
    AsPhraseFactory.ForVerifier<OIN> {

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
  default StringVerifier<OIN> asString() {
    return stringVerifier(this, Function.class.cast(Functions.cast(String.class)));
  }

  @Override
  default IntegerVerifier<OIN> asInteger() {
    return integerVerifier(transformerName(), chainFunctions(this.function(), Functions.cast(Integer.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default BooleanVerifier<OIN> asBoolean() {
    return booleanVerifier(transformerName(), chainFunctions(this.function(), Functions.cast(Boolean.class)), dummyPredicate(), this.originalInputValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  default <NOUT> ObjectVerifier<OIN, NOUT> asValueOf(NOUT value) {
    return objectVerifier(transformerName(), chainFunctions(this.function(), Printables.function("treatAs[NOUT]", v -> (NOUT) v)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> ListVerifier<OIN, E> asListOf(E value) {
    return listVerifier(transformerName(), chainFunctions(this.function(), Functions.castTo(Functions.value())), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> StreamVerifier<OIN, E> asStreamOf(E value) {
    return streamVerifier(transformerName(), chainFunctions(this.function(), Functions.castTo(Functions.value())), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default StringVerifier<OIN> intoStringWith(Function<T, String> function) {
    return stringVerifier(this, function);
  }

  @Override
  default IntegerVerifier<OIN> intoIntegerWith(Function<T, Integer> function) {
    return integerVerifier(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default BooleanVerifier<OIN> intoBooleanWith(Function<T, Boolean> function) {
    return booleanVerifier(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <OUT> ObjectVerifier<OIN, OUT> intoObjectWith(Function<T, OUT> function) {
    return objectVerifier(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
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

    public static <V extends Verifier<V, OIN, T>, OIN, T> StringVerifier<OIN> stringVerifier(Verifier<V, OIN, T> verifier, Function<T, String> function) {
      return stringVerifier(verifier.transformerName(), chainFunctions(verifier.function(), function), dummyPredicate(), verifier.originalInputValue());
    }

    public static <OIN> StringVerifier<OIN> stringVerifier(
        String transformerName,
        Function<? super OIN, String> function,
        Predicate<? super String> predicate,
        OIN originalInputValue) {
      return new StringVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, OUT> ObjectVerifier<OIN, OUT> objectVerifier(ObjectTransformer.Impl<OIN, OUT> objectTransformer) {
      return objectVerifier(objectTransformer.transformerName(), objectTransformer.function(), dummyPredicate(), objectTransformer.originalInputValue());
    }

    public static <OIN, OUT> ObjectVerifier<OIN, OUT> objectVerifier(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInptValue) {
      return new ObjectVerifier.Impl<>(transformerName, function, predicate, originalInptValue);
    }

    public static <OIN, E> ListVerifier<OIN, E> listVerifier(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      return new ListVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> IntegerVerifier<OIN> integerVerifier(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return new IntegerVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, E> StreamVerifier<OIN, E> streamVerifier(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      return new StreamVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> BooleanVerifier<OIN> booleanVerifier(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      return new BooleanVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }
  }

  abstract class Base<V extends Verifier<V, OIN, T>, OIN, T>
      extends PrintablePredicateFactory.TransformingPredicate<OIN, T>
      implements Verifier<V, OIN, T> {
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
        this.predicate = Predicates.and(this.predicate, predicate);
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
