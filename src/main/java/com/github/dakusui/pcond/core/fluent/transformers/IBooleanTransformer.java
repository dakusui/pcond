package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IBooleanVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.IVerifier.Factory.booleanVerifier;

public interface IBooleanTransformer<OIN> extends ITransformer<IBooleanTransformer<OIN>, OIN, Boolean>, Matcher.ForBoolean<OIN> {
  @Override
  IBooleanVerifier<OIN> then();

  class Impl<OIN> extends Transformer<IBooleanTransformer<OIN>, OIN, Boolean> implements IBooleanTransformer<OIN> {

    public <IN> Impl(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends Boolean> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public IBooleanVerifier<OIN> then() {
      return booleanVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }
}
