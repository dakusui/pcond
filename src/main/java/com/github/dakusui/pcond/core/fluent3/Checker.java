package com.github.dakusui.pcond.core.fluent3;

public interface Checker<
    V extends Checker<V, OIN, T>,
    OIN,
    T>
    extends Matcher<V, OIN, T> {
}
