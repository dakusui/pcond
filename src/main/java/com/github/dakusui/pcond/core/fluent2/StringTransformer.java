package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Predicate;
import java.util.function.Supplier;

public interface StringTransformer<OIN> extends
    Transformer<StringTransformer<OIN>, StringChecker<OIN>, OIN, String> {

  IntegerTransformer<OIN> length();


  class Impl<OIN> extends Base<StringTransformer<OIN>, OIN, String> implements StringTransformer<OIN> {

    protected Impl(OIN originalInputValue, Supplier<Predicate<OIN>> root) {
      super(originalInputValue, root);
    }

    @Override
    public IntegerTransformer<OIN> length() {
      IntegerTransformer.Impl<OIN> ret = new IntegerTransformer.Impl<>(originalInputValue(), rootPredicateSupplier());
      this.appendChild((StringTransformer<OIN> tx) -> Predicates.transform(Functions.length()).check(ret.connectChildPredicates()));

      return ret;
    }

    @Override
    public StringChecker<OIN> then() {
      StringChecker<OIN> ret = new StringChecker.Impl<>(this.originalInputValue(), rootPredicateSupplier());
      this.appendChild(tx -> ret.connectChildPredicates());
      return ret;
    }
  }

  static StringTransformer<String> create(String originalInputValue) {
    return create(originalInputValue, null);
  }

  static <OIN> StringTransformer<OIN> create(OIN originalInputValue, Supplier<Predicate<OIN>> root) {
    return new Impl<>(originalInputValue, root);
  }
}
