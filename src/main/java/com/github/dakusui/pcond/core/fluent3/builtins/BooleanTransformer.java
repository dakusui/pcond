package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

public interface BooleanTransformer<
    RX extends Matcher<RX, RX, OIN, OIN>,
    OIN
    > extends
    AbstractObjectTransformer<
        BooleanTransformer<RX, OIN>,
        RX,
        BooleanChecker<RX, OIN>,
        OIN,
        Boolean
        > {
  static <R extends Matcher<R, R, Boolean, Boolean>> BooleanTransformer<R, Boolean> create(Boolean value) {
    return new Impl<>(value, null);
  }
  class Impl<
      RX extends Matcher<RX, RX, OIN, OIN>,
      OIN
      > extends
      Matcher.Base<
          BooleanTransformer<RX, OIN>,
          RX,
          OIN,
          Boolean
          > implements
      BooleanTransformer<RX, OIN> {
    public Impl(OIN rootValue, RX root) {
      super(rootValue, root);
    }

    @Override
    public BooleanChecker<RX, OIN> createCorrespondingChecker(RX root) {
      return new BooleanChecker.Impl<>(this.rootValue(), root);
    }
  }
}
