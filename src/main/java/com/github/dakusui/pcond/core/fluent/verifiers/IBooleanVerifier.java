package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface IBooleanVerifier<OIN> extends Identifiable, Predicate<OIN>, Evaluable<OIN>, Evaluable.Transformation<OIN, Boolean>, IVerifier<IBooleanVerifier<OIN>, OIN, Boolean>, Matcher.ForBoolean<OIN> {
  @Override
  IBooleanVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends Boolean> function, Predicate<? super Boolean> predicate, OIN originalInputValue);
}
