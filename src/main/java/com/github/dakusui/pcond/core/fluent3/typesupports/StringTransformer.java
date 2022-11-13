package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import static java.util.Arrays.asList;

public interface StringTransformer<
    OIN,
    R extends Matcher<R, R, OIN, OIN>
    > extends
    AbstractObjectTransformer<StringTransformer<OIN, R>, R, StringChecker<R, OIN>, OIN, String> {
  static <R extends Matcher<R, R, String, String>> StringTransformer<String, R> create(String value) {
    return new Impl<>(value, null);
  }

  class Impl<
      OIN,
      R extends Matcher<R, R, OIN, OIN>> extends
      Matcher.Base<
          StringTransformer<OIN, R>,
          R,
          OIN,
          String> implements
      StringTransformer<OIN, R> {

    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public StringChecker<R, OIN> createCorrespondingChecker(R root) {
      return new StringChecker.Impl<>(this.rootValue(), this.root());
    }
  }

  default StringTransformer<OIN, R> substring(int begin) {
    return this.toString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  default StringTransformer<OIN, R> toUpperCase() {
    return this.toString(Printables.function("toUpperCase", String::toUpperCase));
  }

  default StringTransformer<OIN, R> toLowerCase() {
    return this.toString(Printables.function("toLowerCase", String::toLowerCase));
  }

  default ListTransformer<R, OIN, String> split(String regex) {
    return this.toList(Printables.function("split[" + regex + "]", (String s) -> asList((s.split(regex)))));
  }

  default IntegerTransformer<R, OIN> length() {
    return toInteger(Functions.length());
  }
}
