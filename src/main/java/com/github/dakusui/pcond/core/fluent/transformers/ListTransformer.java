package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IListVerifier;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.List;
import java.util.function.Function;

public class ListTransformer<OIN, E>
    extends Transformer<IListTransformer<OIN, E>, OIN, List<E>>
    implements IListTransformer<OIN, E> {
  /**
   * Constructs an object of this class.
   *
   * @param transformerName    A name of transformer printed in a failure report.
   * @param parent             A parent transformer
   * @param function           A function that transforms an input value in to the target value of this transformer.
   * @param originalInputValue An original input value.
   */
  public <IN> ListTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends List<E>> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public IListVerifier<OIN, E> then() {
    return IVerifier.Factory.listVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
  }
}
