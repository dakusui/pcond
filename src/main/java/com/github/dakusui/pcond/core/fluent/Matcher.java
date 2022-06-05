package com.github.dakusui.pcond.core.fluent;

import java.util.List;
import java.util.stream.Stream;

public interface Matcher<OIN, OUT> {
  interface ForString<OIN> extends Matcher<OIN, String> {
  }

  interface ForInteger<OIN> extends Matcher<OIN, Integer> {
  }

  interface ForDouble<OIN> extends Matcher<OIN, Double> {
  }

  interface ForLong<OIN> extends Matcher<OIN, Long> {
  }

  interface ForFloat<OIN> extends Matcher<OIN, Float> {
  }

  interface ForShort<OIN> extends Matcher<OIN, Short> {
  }

  interface ForBoolean<OIN> extends Matcher<OIN, Boolean> {
  }

  interface ForObject<OIN, E> extends Matcher<OIN, E> {
  }

  interface ForList<OIN, E> extends Matcher<OIN, List<E>> {
  }

  interface ForStream<OIN, E> extends Matcher<OIN, Stream<E>> {
  }

  interface ForNumber<OIN, N extends Number & Comparable<N>> extends Matcher<OIN, N> {
  }
}
