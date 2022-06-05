package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.verifiers.NumberVerifier;

public interface NumberTransformer<
    TX extends NumberTransformer<TX, V, OIN, N>,
    V extends NumberVerifier<V, OIN, N>,
    OIN,
    N extends Number & Comparable<N>>
    extends
    Transformer<TX, OIN, N>,
    Matcher.ForNumber<OIN, N> {
  @Override
  V then();
}
