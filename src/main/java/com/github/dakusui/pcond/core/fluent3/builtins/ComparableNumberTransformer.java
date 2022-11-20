package com.github.dakusui.pcond.core.fluent3.builtins;


import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;

public interface ComparableNumberTransformer<
    TX extends ComparableNumberTransformer<TX, V, T, N>,
    V extends ComparableNumberChecker<V, T, N>,
    T,
    N extends Number & Comparable<N>> extends
    AbstractObjectTransformer<TX, V, T, N> {
}
