package com.github.dakusui.pcond.core.fluent2;

import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface Transformer<TX extends Transformer<TX, OIN, T>, OIN, T> extends Matcher<TX, OIN, T> {
  interface StringTransformer<OIN> extends Transformer<StringTransformer<OIN>, OIN, String> {

    IntegerTransformer<OIN> length();


    class Impl<OIN> extends Matcher.Base<StringTransformer<OIN>, OIN, String> implements StringTransformer<OIN> {

      protected Impl(OIN originalInputValue, Function<OIN, String> base) {
        super(originalInputValue, base);
      }

      @Override
      public IntegerTransformer<OIN> length() {
        return new IntegerTransformer.Impl<>(originalInputValue(), this.transform().andThen(Functions.length()));
      }
    }

    static StringTransformer<String> create(String originalInputValue) {
      return create(originalInputValue, Functions.identity());
    }

    static <OIN> StringTransformer<OIN> create(OIN originalInputValue, Function<OIN, String> transform) {
      return new StringTransformer.Impl<>(originalInputValue, requireNonNull(transform));
    }
  }


  interface IntegerTransformer<OIN> extends Transformer<IntegerTransformer<OIN>, OIN, Integer> {
    class Impl<OIN> extends Matcher.Base<IntegerTransformer<OIN>, OIN, Integer> implements IntegerTransformer<OIN> {
      protected Impl(OIN originalInputValue, Function<OIN, Integer> base) {
        super(originalInputValue, base);
      }
    }
  }
}
