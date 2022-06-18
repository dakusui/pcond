package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.ValueChecker;

import java.util.function.Predicate;

public enum TestAssertions {

  ;

  public static <T> void assertThat(T value, Predicate<? super T> predicate) {
    ValueChecker.INSTANCE.assertThat(value, predicate);
  }

  public static <T> void assumeThat(T value, Predicate<? super T> predicate) {
    ValueChecker.INSTANCE.assumeThat(value, predicate);
  }
}
