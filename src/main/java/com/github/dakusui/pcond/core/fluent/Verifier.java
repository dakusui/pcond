package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.isDummyPredicate;

public abstract class Verifier<V extends IVerifier<V, OIN, T>, OIN, T>
    extends PrintablePredicateFactory.TransformingPredicate<OIN, T>
    implements IVerifier<V, OIN, T> {
  protected final String                             transformerName;
  private final   Function<? super OIN, ? extends T> function;
  private final   OIN                                originalInputValue;
  private         Predicate<? super T>               predicate;

  protected Verifier(String transformerName, Function<? super OIN, ? extends T> function, Predicate<? super T> predicate, OIN originalInputValue) {
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

  // BEGIN: ------------------------- High -level methods

  ////
  // BEGIN: Methods for java.lang.Object come here.

  // END: Methods for java.lang.Object come here.
  ////
  //--------------------------------
}
