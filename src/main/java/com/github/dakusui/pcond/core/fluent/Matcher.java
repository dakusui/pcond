package com.github.dakusui.pcond.core.fluent;

public interface Matcher<OIN> {
  OIN originalInputValue();

  interface ForString<OIN> extends Matcher<OIN> {
  }

  interface ForComparableNumber<OIN, N extends Number & Comparable<N>> extends Matcher<OIN> {
  }

  interface ForInteger<OIN> extends ForComparableNumber<OIN, Integer> {
  }

  interface ForDouble<OIN> extends ForComparableNumber<OIN, Double> {
  }

  interface ForLong<OIN> extends ForComparableNumber<OIN, Long> {
  }

  interface ForFloat<OIN> extends ForComparableNumber<OIN, Float> {
  }

  interface ForShort<OIN> extends ForComparableNumber<OIN, Short> {
  }

  interface ForBoolean<OIN> extends Matcher<OIN> {
  }

  interface ForObject<OIN, E> extends Matcher<OIN> {
  }

  interface ForList<OIN, E> extends Matcher<OIN> {
  }

  interface ForStream<OIN, E> extends Matcher<OIN> {
  }
}
