package com.github.dakusui.pcond;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.function.Predicate;

public enum TestAssertions {

  ;

  public static <T> void assertThat(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.assertThat(value, predicate);
  }

  public static <T> void assumeThat(T value, Predicate<? super T> predicate) {
    AssertionProvider.INSTANCE.assumeThat(value, predicate);
  }
}
