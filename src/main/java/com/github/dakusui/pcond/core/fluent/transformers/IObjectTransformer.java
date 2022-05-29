package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.IAbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IObjectVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;

public interface IObjectTransformer<OIN, OUT> extends ITransformer<IObjectTransformer<OIN, OUT>, OIN, OUT>, IAbstractObjectTransformer<IObjectTransformer<OIN, OUT>, OIN, OUT>, Matcher.ForObject<OIN, OUT> {
  @Override
  IObjectVerifier<OIN, OUT> then();
}
