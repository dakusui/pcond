package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.TestAssertions;
import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.transformers.IObjectTransformer;
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

import static com.github.dakusui.pcond.core.fluent.ITransformer.chainFunctions;
import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.*;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;

public interface IVerifier<V extends IVerifier<V, OIN, T>, OIN, T>
    extends
    Identifiable,
    TestAssertions.Statement<OIN>,
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

  // BEGIN: ------------------------- High -level methods
  @SuppressWarnings("unchecked")
  default V allOf(Predicate<? super T>... predicates) {
    return with(Predicates.allOf(predicates));
  }

  @SuppressWarnings("unchecked")
  default V anyOf(Predicate<? super T>... predicates) {
    return with(Predicates.anyOf(predicates));
  }

  @SuppressWarnings("unchecked")
  default V and(Predicate<? super T>... predicates) {
    return with(Predicates.and(predicates));
  }

  @SuppressWarnings("unchecked")
  default V or(Predicate<? super T>... predicates) {
    return with(Predicates.or(predicates));
  }

  /**
   * A synonym of `build()` method.
   *
   * @return A predicate of `AS` built from this object.
   */
  @SuppressWarnings("unchecked")
  default <AS> Predicate<AS> verify() {
    return (Predicate<AS>) build();
  }

  default V create() {
    return create(this.transformerName(), this.function(), this.predicate(), this.originalInputValue());
  }


  V create(String transformerName, Function<? super OIN, ? extends T> function, Predicate<? super T> predicate, OIN originalInputValue);

  default V with(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  @Override
  default IStringVerifier<OIN> asString() {
    return stringVerifier(this, Function.class.cast(Functions.cast(String.class)));
  }

  @Override
  default IIntegerVerifier.Impl<OIN> asInteger() {
    return integerVerifier(transformerName(), chainFunctions(this.function(), Functions.cast(Integer.class)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default IBooleanVerifier.Impl<OIN> asBoolean() {
    return booleanVerifier(transformerName(), chainFunctions(this.function(), Functions.cast(Boolean.class)), dummyPredicate(), this.originalInputValue());
  }

  @SuppressWarnings("unchecked")
  @Override
  default <NOUT> IObjectVerifier<OIN, NOUT> asValueOf(NOUT value) {
    return objectVerifier(transformerName(), chainFunctions(this.function(), Printables.function("treatAs[NOUT]", v -> (NOUT) v)), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> IListVerifier<OIN, E> asListOf(E value) {
    return listVerifier(transformerName(), chainFunctions(this.function(), Functions.castTo(Functions.value())), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <E> IStreamVerifier<OIN, E> asStreamOf(E value) {
    return streamVerifier(transformerName(), chainFunctions(this.function(), Functions.castTo(Functions.value())), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default IStringVerifier<OIN> intoStringWith(Function<T, String> function) {
    return stringVerifier(this, function);
  }

  @Override
  default IIntegerVerifier.Impl<OIN> intoIntegerWith(Function<T, Integer> function) {
    return integerVerifier(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default IBooleanVerifier.Impl<OIN> intoBooleanWith(Function<T, Boolean> function) {
    return booleanVerifier(transformerName(), chainFunctions(this.function(), function), dummyPredicate(), this.originalInputValue());
  }

  @Override
  default <OUT> IObjectVerifier.Impl<OIN, OUT> intoObjectWith(Function<T, OUT> function) {
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

    public static <V extends IVerifier<V, OIN, T>, OIN, T> IStringVerifier<OIN> stringVerifier(IVerifier<V, OIN, T> verifier, Function<T, String> function) {
      return stringVerifier(verifier.transformerName(), chainFunctions(verifier.function(), function), dummyPredicate(), verifier.originalInputValue());
    }

    public static <OIN> IStringVerifier<OIN> stringVerifier(
        String transformerName,
        Function<? super OIN, String> function,
        Predicate<? super String> predicate,
        OIN originalInputValue) {
      return new IStringVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, OUT> IObjectVerifier.Impl<OIN, OUT> objectVerifier(IObjectTransformer.Impl<OIN, OUT> objectTransformer) {
      return objectVerifier(objectTransformer.transformerName(), objectTransformer.function(), dummyPredicate(), objectTransformer.originalInputValue());
    }

    public static <OIN, OUT> IObjectVerifier.Impl<OIN, OUT> objectVerifier(String transformerName, Function<? super OIN, ? extends OUT> function, Predicate<? super OUT> predicate, OIN originalInptValue) {
      return new IObjectVerifier.Impl<>(transformerName, function, predicate, originalInptValue);
    }

    public static <OIN, E> IListVerifier.Impl<OIN, E> listVerifier(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      return new IListVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> IIntegerVerifier.Impl<OIN> integerVerifier(String transformerName, Function<? super OIN, ? extends Integer> function, Predicate<? super Integer> predicate, OIN originalInputValue) {
      return new IIntegerVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN, E> IStreamVerifier.Impl<OIN, E> streamVerifier(String transformerName, Function<? super OIN, ? extends Stream<E>> function, Predicate<? super Stream<E>> predicate, OIN originalInputValue) {
      return new IStreamVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }

    public static <OIN> IBooleanVerifier.Impl<OIN> booleanVerifier(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue) {
      return new IBooleanVerifier.Impl<>(transformerName, function, predicate, originalInputValue);
    }
  }
}
