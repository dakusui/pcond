package com.github.dakusui.pcond.core.fluent.transformers.extendable;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;

import java.util.function.Function;

public abstract class AbstractObjectTransformer<TX extends IAbstractObjectTransformer<TX, OIN, OUT>, OIN, OUT>
    extends Transformer<TX, OIN, OUT> implements IAbstractObjectTransformer<TX, OIN, OUT> {

  public <IN> AbstractObjectTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  ////
  // BEGIN: Methods for java.lang.Object come here.

  // END: Methods for java.lang.Object come here.
  ////


}
