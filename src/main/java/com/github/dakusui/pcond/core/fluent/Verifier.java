package com.github.dakusui.pcond.core.fluent;

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

public class Verifier<B extends Verifier<B, OIN, IM>, OIN, IM> {
  private final Function<? super OIN, ? extends IM> function;
  private       Predicate<IM>                       predicate;

  public Verifier(Function<? super OIN, ? extends IM> function) {
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
  public final B allOf(Predicate<? super IM>... predicates) {
    return (B) verifyWith(Predicates.allOf(predicates));
  }

  @SafeVarargs
  public final B anyOf(Predicate<? super IM>... predicates) {
    return (B) verifyWith(Predicates.anyOf(predicates));
  }

  @SafeVarargs
  public final B and(Predicate<? super IM>... predicates) {
    return (B) verifyWith(Predicates.and(predicates));
  }

  @SafeVarargs
  public final B or(Predicate<? super IM>... predicates) {
    return verifyWith(Predicates.or(predicates));
  }

  public B verifyWith(Predicate<? super IM> predicate) {
    return predicate(predicate);
  }

  @SuppressWarnings("unchecked")
  protected B predicate(Predicate<? super IM> predicate) {
    if (this.predicate == null)
      this.predicate = (Predicate<IM>) predicate;
    else
      this.predicate = this.predicate.and(predicate);
    return (B) this;
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

  public StringVerifier<OIN> asString(Function<IM, String> converter) {
    return new StringVerifier<>(this.function.andThen(converter));
  }

  public <E> ListVerifier<OIN, E> asListOf(Function<IM, List<E>> converter) {
    return new ListVerifier<>(this.function.andThen(converter));
  }

  @SuppressWarnings("unchecked")
  public <AS> Predicate<AS> build() {
    return (Predicate<AS>) PrintablePredicateFactory.TransformingPredicate.Factory.create(this.function).check(this.predicate);
  }

}
