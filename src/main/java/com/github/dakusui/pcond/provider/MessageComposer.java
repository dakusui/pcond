package com.github.dakusui.pcond.provider;

import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;

public interface MessageComposer {
  <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate);

  <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate);

  <T> String composeMessageForValidation(T t, Predicate<? super T> predicate);

  class Default implements MessageComposer {
    @Override
    public <T> String composeMessageForPrecondition(T value, Predicate<? super T> predicate) {
      return format("value:<%s> violated precondition:value %s", formatObject(value), predicate);
    }

    @Override
    public <T> String composeMessageForPostcondition(T value, Predicate<? super T> predicate) {
      return format("value:<%s> violated postcondition:value %s", formatObject(value), predicate);
    }

    @Override
    public <T> String composeMessageForAssertion(T t, Predicate<? super T> predicate) {
      return "Value:" + formatObject(t) + " violated: " + predicate.toString();
    }

    @Override
    public <T> String composeMessageForValidation(T t, Predicate<? super T> predicate) {
      return "Value:" + formatObject(t) + " violated: " + predicate.toString();
    }
  }
}
