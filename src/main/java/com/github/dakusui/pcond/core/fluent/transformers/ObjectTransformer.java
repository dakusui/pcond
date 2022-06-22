package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.transformers.extendable.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.checkers.ObjectChecker;
import com.github.dakusui.pcond.core.fluent.Matcher;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.objectChecker;

public interface ObjectTransformer<OIN, OUT> extends Transformer<ObjectTransformer<OIN, OUT>, OIN, OUT>, AbstractObjectTransformer<ObjectTransformer<OIN, OUT>, OIN, OUT>, Matcher.ForObject<OIN, OUT> {
  @Override
  ObjectChecker<OIN, OUT> then();

  class Impl<OIN, OUT> extends AbstractObjectTransformer.Base<ObjectTransformer<OIN, OUT>, OIN, OUT> implements ObjectTransformer<OIN, OUT> {

    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public ObjectChecker<OIN, OUT> then() {
      return objectChecker(this);
    }
  }
}
