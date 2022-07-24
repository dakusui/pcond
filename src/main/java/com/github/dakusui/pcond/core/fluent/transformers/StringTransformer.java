package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.StringChecker;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;
import static java.util.Arrays.asList;

public interface StringTransformer<OIN> extends
    Transformer<StringTransformer<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default StringTransformer<OIN> substring(int begin) {
    return this.transformToString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  default StringTransformer<OIN> toUpperCase() {
    return this.transformToString(Printables.function("toUpperCase", String::toUpperCase));
  }

  default StringTransformer<OIN> toLowerCase() {
    return this.transformToString(Printables.function("toLowerCase", String::toLowerCase));
  }

  default ListTransformer<OIN, String> split(String regex) {
    return this.transformToList(Printables.function("split[" + regex + "]", (String s) -> asList((s.split(regex)))));
  }

  @SuppressWarnings("unchecked")
  @Override
  default StringChecker<OIN> then() {
    return Checker.Factory.stringChecker(
        this.transformerName(),
        (Function<? super OIN, String>) this.function(),
        dummyPredicate(),
        this.originalInputValue());
  }

  class Impl<OIN>
      extends Base<StringTransformer<OIN>, OIN, String>
      implements StringTransformer<OIN> {
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends String> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }
  }
}
