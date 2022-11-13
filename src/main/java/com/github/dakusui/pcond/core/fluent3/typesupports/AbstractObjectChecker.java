package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface AbstractObjectChecker<
    V extends Checker<V, R, OIN, T>,
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    T> extends Checker<
    V, R, OIN, T
    > {
}
