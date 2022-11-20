package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent4.AbstractObjectChecker;

import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 */
public interface ObjectChecker<
    OIN,
    E> extends
    AbstractObjectChecker<
        ObjectChecker<OIN, E>,
        OIN,
        E> {
  class Impl<
      OIN,
      E> extends
      Base<
          ObjectChecker<OIN, E>,
          OIN,
          E> implements
      ObjectChecker<OIN, E> {
    public Impl(Supplier<OIN> baseValue, Function<OIN, E> root) {
      super(baseValue, root);
    }

    @Override
    protected ObjectChecker<E, E> rebase() {
      return new ObjectChecker.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
