package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface Transformer<TX extends Transformer<TX, V, OIN, T>, V extends Checker<V, OIN, T>, OIN, T> extends Matcher<TX, OIN, T> {
  V then();

}
