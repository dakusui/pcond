package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.forms.Functions;

import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class ListTransformer<OIN, E> extends Transformer<ListTransformer<OIN, E>, OIN, List<E>> {

  /**
   * @param function A function that transforms
   */
  public ListTransformer(Function<? super OIN, ? extends List<E>> function) {
    super(function);
  }

  @Override
  public ListVerifier<OIN, E> then() {
    return then(Functions.identity());
  }

  @Override
  public ListVerifier<OIN, E> then(Function<List<E>, List<E>> converter) {
    return thenAsList(converter);
  }

  public ObjectTransformer<OIN, E> elementAt(int i) {
    return this.transformToObject(Functions.elementAt(i));
  }

  public IntegerTransformer<OIN> size() {
    return this.transformToInteger(Functions.size());
  }


  void method() {
    Functions.stream();
    Functions.elementAt(0);
    Functions.size();
  }
}
