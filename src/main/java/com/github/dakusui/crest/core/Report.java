package com.github.dakusui.crest.core;

import java.util.List;
import java.util.function.Consumer;

public interface Report {

  String expectation();

  String mismatch();

  /**
   * Returns a list of exceptions thrown during {@code matches} method's
   * execution of a {@code Session} object by which this object is created.
   *
   * @return An optional of exception.
   * @see Session#matches(Matcher.Leaf, Object, Consumer)
   */
  List<Throwable> exceptions();

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  boolean isSuccessful();

}
