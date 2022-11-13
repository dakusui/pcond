package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.Transformer;

public interface ComparableNumberTransformer<
    TX extends ComparableNumberTransformer<TX, R, V, OIN, N>,
    R extends Matcher<R, R, OIN, OIN>,
    V extends ComparableNumberChecker<V, R, OIN, N>,
    OIN,
    N extends Number & Comparable<N>> extends
    Transformer<TX, R, V, OIN, N> {
}
