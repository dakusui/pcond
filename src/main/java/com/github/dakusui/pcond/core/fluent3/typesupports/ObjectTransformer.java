package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.Matcher;


/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 *
 * Instead, see {@link CustomTransformer}.
 */
public interface ObjectTransformer<
    OIN,
    RX extends Matcher<RX, RX, OIN, OIN>> extends
    AbstractObjectTransformer<
        ObjectTransformer<OIN, RX>,
        RX,
        ObjectChecker<OIN, RX>,
        OIN,
        Object> {
  class Impl<
      OIN,
      RX extends Matcher<RX, RX, OIN, OIN>> extends
      Matcher.Base<
          ObjectTransformer<OIN, RX>,
          RX,
          OIN,
          Object> implements
      ObjectTransformer<OIN, RX> {
    public Impl(OIN rootValue, RX root) {
      super(rootValue, root);
    }

    @Override
    public ObjectChecker<OIN, RX> createCorrespondingChecker(RX root) {
      return new ObjectChecker.Impl<>(this.rootValue(), root);
    }
  }
}
