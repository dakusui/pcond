package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.checkers.ComparableNumberChecker;

public interface ComparableNumberTransformer<
    TX extends ComparableNumberTransformer<TX, V, OIN, N>,
    V extends ComparableNumberChecker<V, OIN, N>,
    OIN,
    N extends Number & Comparable<N>>
    extends
    Transformer<TX, OIN, N>,
    Matcher.ForComparableNumber<OIN, N> {
  @Override
  V then();
}
