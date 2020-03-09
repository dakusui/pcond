package com.github.dakusui.pcond.functions;

import java.util.function.BiFunction;
import java.util.function.Predicate;

@FunctionalInterface
public interface MessageComposer<T> extends BiFunction<T, Predicate<? super T>, String> {
}
