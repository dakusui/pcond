package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.fluent.verifiers.IntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ObjectVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.StringVerifier;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public class Verifier<V extends Verifier<V, OIN, T>, OIN, T> {
  private final Function<? super OIN, ? extends T> function;
  private       Predicate<T>                       predicate;

  public Verifier(Function<? super OIN, ? extends T> function) {
    this.function = function;
    this.predicate = null;
  }

  /**
   * Use this method, when compiler is not able to figure out intended intermediate type.
   *
   * @param <NEW_IM> An intermediate type explicitly specified.
   * @return This object cast by the intermediate type
   */
  @SuppressWarnings("unchecked")
  public <NEW_IM, BB extends Verifier<BB, OIN, NEW_IM>> BB into(Class<NEW_IM> klass) {
    return (BB) this;
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

  public V with(Predicate<? super T> predicate) {
    return predicate(predicate);
  }

  @SuppressWarnings("unchecked")
  protected V predicate(Predicate<? super T> predicate) {
    if (this.predicate == null)
      this.predicate = (Predicate<T>) predicate;
    else
      this.predicate = this.predicate.and(predicate);
    return (V) this;
  }

  /**
   * Use this method only when you are sure the type you are handling is of `AS`.
   *
   * @param valueType A class of the value you are verifying.
   * @param <AS>      Type to check with your verifier.
   * @return This object
   */
  public <AS>
  ObjectVerifier<OIN, AS> asObjectOf(Class<AS> valueType) {
    return new ObjectVerifier<>(Functions.cast(valueType));
  }

  public StringVerifier<OIN> asString() {
    return new StringVerifier<>(this.function.andThen(Functions.stringify()));
  }

  public IntegerVerifier<OIN> asInteger() {
    return new IntegerVerifier<>(this.function.andThen(Functions.cast(Integer.class)));
  }

  public StringVerifier<OIN> asString(Function<T, String> converter) {
    return new StringVerifier<>(this.function.andThen(converter));
  }

  public <E> ListVerifier<OIN, E> asListOf(Function<T, List<E>> converter) {
    return new ListVerifier<>(this.function.andThen(converter));
  }

  public Predicate<? super OIN> build() {
    return PrintablePredicateFactory.TransformingPredicate.Factory.create(this.function).check(this.predicate);
  }

  /**
   * A synonym of {@link this#build()} method.
   *
   * @return A predicate of `AS` built from this object.
   */
  @SuppressWarnings("unchecked")
  public <AS> Predicate<AS> verify() {
    return (Predicate<AS>) build();
  }
}
