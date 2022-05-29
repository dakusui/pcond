package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.BooleanVerifier;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.booleanVerifier;

public class BooleanTransformer<OIN> extends Transformer<IBooleanTransformer<OIN>, OIN, Boolean> implements IBooleanTransformer<OIN> {

  public <IN> BooleanTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Boolean> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public BooleanVerifier<OIN> then() {
    return booleanVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
  }
}
