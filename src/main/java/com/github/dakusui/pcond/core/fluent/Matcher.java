package com.github.dakusui.pcond.core.fluent;

import com.github.dakusui.pcond.forms.Predicates;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dakusui.pcond.internals.InternalChecks.requireState;
import static java.util.Objects.requireNonNull;

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
