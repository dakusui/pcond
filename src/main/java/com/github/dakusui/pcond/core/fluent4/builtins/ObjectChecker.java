package com.github.dakusui.pcond.core.fluent4.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.CustomTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.function.Supplier;

/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 *
 * Instead, see {@link CustomTransformer}.
 */
public interface ObjectChecker<
    RX extends Matcher<RX, RX, OIN, OIN>, OIN,
    E> extends
    AbstractObjectChecker<
            ObjectChecker<RX, OIN, E>,
            RX,
            OIN,
            E> {
  public class Impl<
      OIN,
      RX extends Matcher<RX, RX, OIN, OIN>,
      E> extends
      Base<
          ObjectChecker<RX, OIN, E>,
          RX,
          OIN,
          E> implements
      ObjectChecker<RX, OIN, E> {
    public Impl(Supplier<OIN> rootValue, RX root) {
      super(rootValue, root);
    }
  }
}
