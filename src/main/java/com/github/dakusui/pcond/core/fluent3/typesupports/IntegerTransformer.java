package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.Transformer;

public interface IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, IntegerChecker<OIN>, OIN, Integer> {
  class Impl<OIN> extends Matcher.Base<IntegerTransformer<OIN>, OIN, Integer> implements IntegerTransformer<OIN> {
    public Impl(OIN rootValue, Matcher<?, OIN, OIN> root) {
      super(rootValue, root);
    }

    @Override
    public IntegerChecker<OIN> createCorrespondingChecker(Matcher<?, OIN, OIN> root) {
      return new IntegerChecker.Impl<>(rootValue(), root);
    }
  }
}
