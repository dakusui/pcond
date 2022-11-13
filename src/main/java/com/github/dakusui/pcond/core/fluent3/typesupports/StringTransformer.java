package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.Transformer;
import com.github.dakusui.pcond.forms.Functions;

public interface StringTransformer<OIN> extends Transformer<StringTransformer<OIN>, StringChecker<OIN>, OIN, String> {
  static StringTransformer<String> create(String value) {
    return new Impl<>(value, null);
  }

  class Impl<OIN> extends Matcher.Base<StringTransformer<OIN>, OIN, String> implements StringTransformer<OIN> {

    protected Impl(OIN rootValue, Matcher<?, OIN, OIN> root) {
      super(rootValue, root);
    }

    @Override
    public StringChecker<OIN> createCorrespondingChecker(Matcher<?, OIN, OIN> root) {
      return new StringChecker.Impl<>(this.rootValue(), this.root());
    }
  }


  default IntegerTransformer<OIN> length() {
    return toInteger(Functions.length());
  }
}
