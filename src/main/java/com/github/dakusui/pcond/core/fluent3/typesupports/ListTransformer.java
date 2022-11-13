package com.github.dakusui.pcond.core.fluent3.typesupports;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;

import java.util.List;

public interface ListTransformer<
    OIN,
    R extends Matcher<R, R, OIN, OIN>,
    E
    > extends
    AbstractObjectTransformer<ListTransformer<OIN, R, E>, R, ListChecker<OIN, R, E>, OIN, List<E>> {
  class Impl<
      OIN,
      R  extends Matcher<R, R, OIN, OIN>,
      E> extends Base<
      ListTransformer<OIN, R, E>,
      R,
      OIN,
      List<E>>
      implements ListTransformer<OIN, R, E> {
    public Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public ListChecker<OIN, R, E> createCorrespondingChecker(R root) {
      return new ListChecker.Impl<>(rootValue(), root);
    }
  }
}
