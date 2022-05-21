package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Functions;

import java.util.List;
import java.util.function.Function;

public class ListTransformer<OIN, E>
    extends Transformer<ListTransformer<OIN, E>, OIN, List<E>>
    implements Matcher.ForList<OIN, E> {
  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> ListTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends List<E>> function) {
    super(transformerName, parent, function);
  }

  @Override
  public ListVerifier<OIN, E> then() {
    return new ListVerifier<>(this.transformerName(), this.function(), dummyPredicate());
  }

  public ObjectTransformer<OIN, E> elementAt(int i) {
    return this.transformToObject(Functions.elementAt(i));
  }

  public IntegerTransformer<OIN> size() {
    return this.transformToInteger(Functions.size());
  }
}
