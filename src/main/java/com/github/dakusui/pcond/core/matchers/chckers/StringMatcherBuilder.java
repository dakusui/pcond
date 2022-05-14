package com.github.dakusui.pcond.core.matchers.chckers;

import com.github.dakusui.pcond.core.printable.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;

public class StringMatcherBuilder<OIN> extends Matcher.Builder<StringMatcherBuilder<OIN>, OIN, String> {
  public StringMatcherBuilder(Function<? super OIN, ? extends String> function) {
    super(function);
  }

  public StringMatcherBuilder<OIN> contains(String token) {
    return (StringMatcherBuilder<OIN>) this.predicate(Predicates.containsString(token));
  }
}
