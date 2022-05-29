package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.BooleanVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.booleanVerifier;

public interface BooleanTransformer<OIN> extends Transformer<BooleanTransformer<OIN>, OIN, Boolean>, Matcher.ForBoolean<OIN> {
  @Override
  BooleanVerifier<OIN> then();

  class Impl<OIN> extends Base<BooleanTransformer<OIN>, OIN, Boolean> implements BooleanTransformer<OIN> {

    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Boolean> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public BooleanVerifier<OIN> then() {
      return booleanVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }
}
