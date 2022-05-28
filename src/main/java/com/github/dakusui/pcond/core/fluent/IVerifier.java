package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.verifiers.*;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.ITransformer.chainFunctions;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;

public interface IVerifier<V extends IVerifier<V, OIN, T>, OIN, T>
    extends
    Identifiable,
    Predicate<OIN>,
    Evaluable<OIN>,
    Evaluable.Transformation<OIN, T>,
    IntoPhraseFactory.ForVerifier<OIN, T>,
    AsPhraseFactory.ForVerifier<OIN> {

  String transformerName();

  V predicate(Predicate<? super T> predicate);

  Function<? super OIN, ? extends T> function();

  Predicate<? super T> predicate();

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

  V create();

  default V with(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  @Override
  default StringVerifier<OIN> asString() {
    return new StringVerifier<>(transformerName(), chainFunctions(this.function(), Functions.cast(String.class)), dummyPredicate());
  }

  @Override
  default IntegerVerifier<OIN> asInteger() {
    return new IntegerVerifier<>(transformerName(), chainFunctions(this.function(), Functions.cast(Integer.class)), dummyPredicate());
  }

  @Override
  default BooleanVerifier<OIN> asBoolean() {
    return new BooleanVerifier<>(transformerName(), chainFunctions(this.function(), Functions.cast(Boolean.class)), dummyPredicate());
  }

  @SuppressWarnings("unchecked")
  @Override
  default <NOUT> ObjectVerifier<OIN, NOUT> asValueOf(NOUT value) {
    return new ObjectVerifier<>(transformerName(), chainFunctions(this.function(), Printables.function("treatAs[NOUT]", v -> (NOUT) v)), dummyPredicate());
  }

  @Override
  default <E> ListVerifier<OIN, E> asListOf(E value) {
    return new ListVerifier<>(transformerName(), chainFunctions(this.function(), Functions.castTo(Functions.value())), dummyPredicate());
  }

  @Override
  default <E> StreamVerifier<OIN, E> asStreamOf(E value) {
    return new StreamVerifier<>(transformerName(), chainFunctions(this.function(), Functions.castTo(Functions.value())), dummyPredicate());
  }

  @Override
  default StringVerifier<OIN> intoStringWith(Function<T, String> function) {
    return new StringVerifier<>(transformerName(), chainFunctions(this.function(), function), dummyPredicate());
  }

  @Override
  default IntegerVerifier<OIN> intoIntegerWith(Function<T, Integer> function) {
    return new IntegerVerifier<>(transformerName(), chainFunctions(this.function(), function), dummyPredicate());
  }

  @Override
  default BooleanVerifier<OIN> intoBooleanWith(Function<T, Boolean> function) {
    return new BooleanVerifier<>(transformerName(), chainFunctions(this.function(), function), dummyPredicate());
  }

  @Override
  default <OUT> ObjectVerifier<OIN, OUT> intoObjectWith(Function<T, OUT> function) {
    return new ObjectVerifier<>(transformerName(), chainFunctions(this.function(), function), dummyPredicate());
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
}
