package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.*;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Transformer.chainFunctions;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.internals.InternalUtils.isDummyPredicate;

public abstract class Verifier<V extends Verifier<V, OIN, T>, OIN, T>
    extends PrintablePredicateFactory.TransformingPredicate<T, OIN>
    implements IntoPhraseFactory.ForVerifier<OIN, T>,
    AsPhraseFactory.ForVerifier<OIN> {
  protected final String                             transformerName;
  private final   Function<? super OIN, ? extends T> function;
  private         Predicate<? super T>               predicate;

  protected Verifier(String transformerName, Function<? super OIN, ? extends T> function, Predicate<? super T> predicate) {
    super(predicate, function);
    this.transformerName = transformerName;
    this.function = function;
    this.predicate = predicate; // this field can be null, when the first verifier starts building.
  }

  protected V predicate(Predicate<? super T> predicate) {
    if (isDummyPredicate(this.predicate))
      this.predicate = predicate;
    else
      this.predicate = Predicates.and(this.predicate, predicate);
    return this.create();
  }

  protected Function<? super OIN, ? extends T> function() {
    return this.function;
  }

  protected Predicate<? super T> predicate() {
    return this.predicate;
  }


  public V testPredicate(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  public Predicate<? super OIN> build() {
    return PrintablePredicateFactory.TransformingPredicate.Factory
        .create(
            this.transformerName,
            this.transformerName != null ?
                "THEN" :
                "VERIFY",
            this.function)
        .check(this.predicate);
  }
  // BEGIN: ------------------------- High -level methods
  @SafeVarargs
  public final V allOf(Predicate<? super T>... predicates) {
    return (V) with(Predicates.allOf(predicates));
  }

  @SafeVarargs
  public final V anyOf(Predicate<? super T>... predicates) {
    return (V) with(Predicates.anyOf(predicates));
  }

  @SafeVarargs
  public final V and(Predicate<? super T>... predicates) {
    return (V) with(Predicates.and(predicates));
  }

  @SafeVarargs
  public final V or(Predicate<? super T>... predicates) {
    return with(Predicates.or(predicates));
  }
  /**
   * A synonym of `build()` method.
   *
   * @return A predicate of `AS` built from this object.
   */
  @SuppressWarnings("unchecked")
  public <AS> Predicate<AS> verify() {
    return (Predicate<AS>) build();
  }
  abstract protected V create();

  // BEGIN: ------------------------- High -level methods


  public V with(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  @Override
  public StringVerifier<OIN> asString() {
    return new StringVerifier<>(transformerName, chainFunctions(this.function, Functions.cast(String.class)), dummyPredicate());
  }

  @Override
  public IntegerVerifier<OIN> asInteger() {
    return new IntegerVerifier<>(transformerName, chainFunctions(this.function, Functions.cast(Integer.class)), dummyPredicate());
  }

  @Override
  public BooleanVerifier<OIN> asBoolean() {
    return new BooleanVerifier<>(transformerName, chainFunctions(this.function, Functions.cast(Boolean.class)), dummyPredicate());
  }

  @SuppressWarnings("unchecked")
  @Override
  public <NOUT> ObjectVerifier<OIN, NOUT> asValueOf(NOUT value) {
    return new ObjectVerifier<>(transformerName, chainFunctions(this.function, Printables.function("treatAs[NOUT]", v -> (NOUT)v)), dummyPredicate());
  }

  @Override
  public <E> ListVerifier<OIN, E> asListOf(E value) {
    return new ListVerifier<>(transformerName, chainFunctions(this.function, Functions.castTo(Functions.value())), dummyPredicate());
  }

  @Override
  public <E> StreamVerifier<OIN, E> asStreamOf(E value) {
    return new StreamVerifier<>(transformerName, chainFunctions(this.function, Functions.castTo(Functions.value())), dummyPredicate());
  }

  @Override
  public StringVerifier<OIN> intoStringWith(Function<T, String> function) {
    return new StringVerifier<>(transformerName, chainFunctions(this.function, function), dummyPredicate());
  }

  @Override
  public IntegerVerifier<OIN> intoIntegerWith(Function<T, Integer> function) {
    return new IntegerVerifier<>(transformerName, chainFunctions(this.function, function), dummyPredicate());
  }

  @Override
  public BooleanVerifier<OIN> intoBooleanWith(Function<T, Boolean> function) {
    return new BooleanVerifier<>(transformerName, chainFunctions(this.function, function), dummyPredicate());
  }

  @Override
  public <OUT> ObjectVerifier<OIN, OUT> intoObjectWith(Function<T, OUT> function) {
    return new ObjectVerifier<>(transformerName, chainFunctions(this.function, function), dummyPredicate());
  }

  ////
  // BEGIN: Methods for java.lang.Object come here.

  public V isNotNull() {
    return this.predicate(Predicates.isNotNull());
  }

  public V isNull() {
    return this.predicate(Predicates.isNull());
  }

  /**
   * Checks the object with an argument if they are "equal", using `equalTo` method.
   *
   * @return the updated object.
   */
  public V isEqualTo(Object anotherObject) {
    return this.predicate(Predicates.isEqualTo(anotherObject));
  }

  public V isSameReferenceAs(Object anotherObject) {
    return this.predicate(Predicates.isSameReferenceAs(anotherObject));
  }

  public V isInstanceOf(Class<?> klass) {
    return this.predicate(Predicates.isInstanceOf(klass));
  }

  public V invoke(String methodName, Object... args) {
    return this.predicate(Predicates.callp(MethodQuery.instanceMethod(parameter(), methodName, args)));
  }

  public V invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.predicate(Predicates.callp(MethodQuery.classMethod(klass, methodName, args)));
  }
  // END: Methods for java.lang.Object come here.
  ////
//--------------------------------
}
