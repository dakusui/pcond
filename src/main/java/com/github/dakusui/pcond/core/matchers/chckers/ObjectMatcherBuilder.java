package com.github.dakusui.pcond.core.matchers.chckers;

import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.function.Function;

public class ObjectMatcherBuilder<OIN, IM> extends Matcher.Builder<ObjectMatcherBuilder<OIN, IM>, OIN, IM> {
  public ObjectMatcherBuilder(Function<? super OIN, ? extends IM> function) {
    super(function);
  }
}
