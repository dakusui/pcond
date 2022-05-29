package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.BooleanVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;

public interface IBooleanTransformer<OIN> extends ITransformer<IBooleanTransformer<OIN>, OIN, Boolean>, Matcher.ForBoolean<OIN> {
  @Override
  BooleanVerifier<OIN> then();
}
