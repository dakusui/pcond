package com.github.dakusui.pcond.core.fluent3;

public interface Checker<
    V extends Checker<V, R, OIN, T>,
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    T>
    extends Matcher<V, R, OIN, T> {
}
