package com.github.dakusui.pcond.fluent;

import static com.github.dakusui.pcond.forms.Predicates.transform;
import static com.github.dakusui.pcond.internals.InternalUtils.makeSquashable;

/**
 * An "entry-point" class to write a "fluent" style tests.
 * Since the overloaded methods `value` are important entry-points of this interface, in order to avoid confusing users by `valueOf` method in `Enum`,
 * this class is implemented as a conventional class, not an `enum`.
 */
public class Fluents {
  private Fluents() {
  }
}
