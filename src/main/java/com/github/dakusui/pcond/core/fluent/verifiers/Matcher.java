package com.github.dakusui.pcond.core.fluent.verifiers;

import java.util.List;
import java.util.stream.Stream;

public interface Matcher<OIN, OUT> {
  interface ForString<OIN> extends Matcher<OIN, String> {
  }

  interface ForInteger<OIN> extends Matcher<OIN, Integer> {
  }

  interface ForBoolean<OIN> extends Matcher<OIN, Boolean> {
  }

  interface ForObject<OIN, E> extends Matcher<OIN, E> {
  }

  interface ForList<OIN, E> extends Matcher<OIN, List<E>> {
  }

  interface ForStream<OIN, E> extends Matcher<OIN, Stream<E>> {
  }
}
