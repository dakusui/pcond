package com.github.dakusui.pcond.core.matchers.chckers;

import com.github.dakusui.pcond.core.printable.Matcher;
import com.github.dakusui.pcond.forms.Printables;

public class StringMatcherBuilderBuilder0 extends Matcher.Builder.Builder0<StringMatcherBuilderBuilder0, String> {
  public Matcher.Builder<String, String> substring(int begin) {
    return this.<String>transformBy(Printables.function(() -> "substring[" + begin + "]", s -> substring(begin)));
  }
}
