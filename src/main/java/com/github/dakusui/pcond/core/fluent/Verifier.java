package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.core.fluent.Transformer.dummyPredicate;
import static com.github.dakusui.pcond.forms.Functions.parameter;

public abstract class Verifier<V extends Verifier<V, OIN, T>, OIN, T>
    extends PrintablePredicateFactory.TransformingPredicate<T, OIN> {
  protected final String                             transformerName;
  private final   Function<? super OIN, ? extends T> function;
  private         Predicate<? super T>               predicate;

  protected Verifier(String transformerName, Function<? super OIN, ? extends T> function, Predicate<? super T> predicate) {
    super(predicate, function);
    this.transformerName = transformerName;
    this.function = function;
    this.predicate = predicate; // this field can be null, when the first verifier starts building.
  }

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

  public V testPredicate(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  public V with(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  protected V predicate(Predicate<? super T> predicate) {
    if (this.predicate == null)
      this.predicate = predicate;
    else
      this.predicate = Predicates.and(this.predicate, predicate);
    return this.create();
  }

  abstract protected V create();

  /**
   * Use this method only when you are sure the type you are handling is of `AS`.
   *
   * @param valueType A class of the value you are verifying.
   * @param <AS>      Type to check with your verifier.
   * @return This object
   */
  public <AS>
  ObjectVerifier<OIN, AS> asInstanceOf(Class<AS> valueType) {
    return new ObjectVerifier<>(transformerName, Functions.cast(valueType), dummyPredicate());
  }

  public StringVerifier<OIN> asString() {
    return new StringVerifier<>(transformerName, this.function.andThen(Functions.stringify()), dummyPredicate());
  }

  public IntegerVerifier<OIN> asInteger() {
    return new IntegerVerifier<>(transformerName, this.function.andThen(Functions.cast(Integer.class)), dummyPredicate());
  }

  public StringVerifier<OIN> asString(Function<T, String> converter) {
    return new StringVerifier<>(transformerName, this.function.andThen(converter), dummyPredicate());
  }

  public <E> ListVerifier<OIN, E> asListOf(Function<T, List<E>> converter) {
    return new ListVerifier<>(transformerName, this.function.andThen(converter), dummyPredicate());
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

  /**
   * A synonym of `build()` method.
   *
   * @return A predicate of `AS` built from this object.
   */
  @SuppressWarnings("unchecked")
  public <AS> Predicate<AS> verify() {
    return (Predicate<AS>) build();
  }

  ////
  // BEGIN: Methods for java.lang.Object come here.
  void method() {
    /*
    Predicates.isNotNull();
    Predicates.alwaysTrue();
    Predicates.isNull();
    Predicates.isEqualTo(null);
    Predicates.isSameReferenceAs(null);
    //Predicates.isInstanceOf(null);
    Predicates.callp(null);

     */
  }

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

  protected Function<? super OIN, ? extends T> function() {
    return this.function;
  }

  protected Predicate<? super T> predicate() {
    return this.predicate;
  }

  // END: Methods for java.lang.Object come here.
  ////
}
