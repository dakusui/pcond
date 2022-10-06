package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.ThrowableChecker;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

public interface ThrowableTransformer<OIN, OUT extends Throwable> extends Transformer<ThrowableTransformer<OIN, OUT>, OIN, OUT>, AbstractObjectTransformer<ThrowableTransformer<OIN, OUT>, OIN, OUT>, Matcher.ForObject<OIN, OUT> {
  @Override
  ThrowableChecker<OIN, OUT> then();

  default <OUT2 extends Throwable> ThrowableTransformer<OIN, OUT2> getCause() {
    return exercise(Printables.function("getCause", Throwable::getCause)).asThrowable();
  }

  default StringTransformer<OIN> getMessage() {
    return exercise(Printables.function("getMessage", Throwable::getMessage)).asString();
  }

  class Impl<OIN, OUT extends Throwable> extends AbstractObjectTransformer.Base<ThrowableTransformer<OIN, OUT>, OIN, OUT> implements ThrowableTransformer<OIN, OUT> {

    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public ThrowableChecker<OIN, OUT> then() {
      return Checker.Factory.throwableChecker(this);
    }
  }
}
