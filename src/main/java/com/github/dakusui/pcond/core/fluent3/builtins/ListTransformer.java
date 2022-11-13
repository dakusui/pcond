package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.List;

public interface ListTransformer<
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    E
    > extends
    AbstractObjectTransformer<ListTransformer<R, OIN, E>, R, ListChecker<R, OIN, E>, OIN, List<E>> {
  class Impl<
      R  extends Matcher<R, R, OIN, OIN>, OIN,
      E> extends Base<
      ListTransformer<R, OIN, E>,
      R,
      OIN,
      List<E>>
      implements ListTransformer<R, OIN, E> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public ListChecker<R, OIN, E> createCorrespondingChecker(R root) {
      return new ListChecker.Impl<>(rootValue(), root);
    }
  }
}
