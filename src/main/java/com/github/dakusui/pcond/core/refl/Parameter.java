package com.github.dakusui.pcond.core.refl;

import static com.github.dakusui.pcond.internals.InternalChecks.requireArgument;
import static java.lang.String.format;

public interface Parameter {
  static Parameter create(int i) {
    requireArgument(i, v -> v >= 0, () -> "parameter index must not be negative, but " + i + " was given.");
    return new Parameter() {
      @Override
      public int index() {
        return i;
      }

      @Override
      public String toString() {
        return format("p_%s", i);
      }

      @Override
      public int hashCode() {
        return i;
      }

      @Override
      public boolean equals(Object anotherObject) {
        if (!(anotherObject instanceof Parameter))
          return false;
        Parameter another = (Parameter) anotherObject;
        return this.index() == another.index();
      }
    };
  }

  int index();
}
