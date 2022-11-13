package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.core.fluent3.Transformer;
import com.github.dakusui.pcond.forms.Functions;

public interface StringTransformer<
    OIN,
    R extends Matcher<R, R, OIN, OIN>
    > extends
    Transformer<StringTransformer<OIN, R>, R, StringChecker<OIN, R>, OIN, String> {
  static <R extends Matcher<R, R, String, String>> StringTransformer<String, R> create(String value) {
    return new Impl<>(value, null);
  }

  class Impl<OIN, R extends Matcher<R, R, OIN, OIN>> extends Matcher.Base<StringTransformer<OIN, R>, R, OIN, String> implements StringTransformer<OIN, R> {

    protected Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public StringChecker<OIN, R> createCorrespondingChecker(R root) {
      return new StringChecker.Impl<>(this.rootValue(), this.root());
    }
  }


  default IntegerTransformer<OIN, R> length() {
    return toInteger(Functions.length());
  }
}
