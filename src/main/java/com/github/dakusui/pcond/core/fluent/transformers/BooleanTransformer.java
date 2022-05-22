package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.BooleanVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Function;

public class BooleanTransformer<OIN> extends Transformer<BooleanTransformer<OIN>, OIN, Boolean> implements Matcher.ForBoolean<OIN> {
  /**
   * @param transformerName
   * @param parent
   * @param function
   */
  public <IN> BooleanTransformer(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Boolean> function) {
    super(transformerName, parent, function);
  }

  @Override
  public Verifier<?, OIN, Boolean> then() {
    return new BooleanVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate());
  }
}
