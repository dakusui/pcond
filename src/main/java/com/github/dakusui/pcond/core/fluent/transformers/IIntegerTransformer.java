package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IIntegerVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;

public interface IIntegerTransformer<OIN> extends ITransformer<IIntegerTransformer<OIN>, OIN, Integer>, Matcher.ForInteger<OIN> {
  @Override
  IIntegerVerifier<OIN> then();
}
