package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.CustomTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;


/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 *
 * Instead, see {@link CustomTransformer}.
 */
public interface ObjectTransformer<
    RX extends Matcher<RX, RX, OIN, OIN>,
    OIN,
    E
    > extends
    AbstractObjectTransformer<
            ObjectTransformer<RX, OIN, E>,
            RX,
            ObjectChecker<RX, OIN, E>,
            OIN,
            E> {
  class Impl<
      RX extends Matcher<RX, RX, OIN, OIN>,
      OIN,
      E> extends
      Matcher.Base<
          ObjectTransformer<RX, OIN, E>,
          RX,
          OIN,
          E> implements
      ObjectTransformer<RX, OIN, E> {
    public Impl(OIN rootValue, RX root) {
      super(rootValue, root);
    }

    @Override
    public ObjectChecker<RX, OIN, E> createCorrespondingChecker(RX root) {
      return new ObjectChecker.Impl<>(this.rootValue(), root);
    }
  }
}
