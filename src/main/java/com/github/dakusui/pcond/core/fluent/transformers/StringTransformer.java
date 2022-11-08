package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.StringChecker;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;
import static java.util.Arrays.asList;

public interface StringTransformer<OIN> extends
    Transformer<StringTransformer<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default StringTransformer<OIN> substring(int begin) {
    return this.toString(Printables.function(() -> "substring[" + begin + "]", s -> s.substring(begin)));
  }

  default StringTransformer<OIN> toUpperCase() {
    return this.toString(Printables.function("toUpperCase", String::toUpperCase));
  }

  default StringTransformer<OIN> toLowerCase() {
    return this.toString(Printables.function("toLowerCase", String::toLowerCase));
  }

  default ListTransformer<OIN, String> split(String regex) {
    return this.toList(Printables.function("split[" + regex + "]", (String s) -> asList((s.split(regex)))));
  }

  default IntegerTransformer<OIN> length() {
    return this.toInteger(Functions.length());
  }

  default ShortTransformer<OIN> toShort() {
    return this.toShort(Printables.function("toShort", Short::parseShort));
  }

  default IntegerTransformer<OIN> toInteger() {
    return this.toInteger(Printables.function("toIntegr", Integer::parseInt));
  }

  default LongTransformer<OIN> toLong() {
    return this.toLong(Printables.function("toLong", Long::parseLong));
  }

  default DoubleTransformer<OIN> toDouble() {
    return this.toDouble(Printables.function("toDouble", Double::parseDouble));
  }

  default FloatTransformer<OIN> toFloat() {
    return this.toFloat(Printables.function("toFloat", Float::parseFloat));
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
