package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;

/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 *
 * Instead, see {@link CustomTransformer}.
 */
public interface ObjectChecker<
    OIN,
    RX extends Matcher<RX, RX, OIN, OIN>> extends
    AbstractObjectChecker<
        ObjectChecker<OIN, RX>,
        RX,
        OIN,
        Object> {
  class Impl<
      OIN,
      RX extends Matcher<RX, RX, OIN, OIN>> extends
      Matcher.Base<
          ObjectChecker<OIN, RX>,
          RX,
          OIN,
          Object> implements
      ObjectChecker<OIN, RX> {
    public Impl(OIN rootValue, RX root) {
      super(rootValue, root);
    }
  }
}
