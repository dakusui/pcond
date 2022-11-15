package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface StreamChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    E> extends
    AbstractObjectChecker<
        StreamChecker<R, OIN, E>,
        R,
        OIN,
        Stream<E>> {
  default StreamChecker<R, OIN, E> noneMatch(Predicate<E> p) {
    return this.appendPredicateAsChild(Predicates.noneMatch(p));
  }

  default StreamChecker<R, OIN, E> anyMatch(Predicate<E> p) {
    return this.appendPredicateAsChild(Predicates.anyMatch(p));
  }

  default StreamChecker<R, OIN, E> allMatch(Predicate<E> p) {
    return this.appendPredicateAsChild(Predicates.allMatch(p));
  }

  class Impl<R extends Matcher<R, R, OIN, OIN>,
      OIN,
      E> extends
      Matcher.Base<
          StreamChecker<R, OIN, E>,
          R,
          OIN,
          Stream<E>
          >

      implements StreamChecker<R, OIN, E> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
