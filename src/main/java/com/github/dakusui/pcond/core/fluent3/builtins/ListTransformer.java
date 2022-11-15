package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public interface ListTransformer<
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    E
    > extends
    AbstractObjectTransformer<ListTransformer<R, OIN, E>, R, ListChecker<R, OIN, E>, OIN, List<E>> {
  static <
      R extends Matcher<R, R, List<E>, List<E>>,
      E>
  ListTransformer<R, List<E>, E> create(Supplier<List<E>> value) {
    return new Impl<>(value, null);
  }
  default ObjectTransformer<R,OIN, E> elementAt(int i) {
    return this.toObject(Functions.elementAt(i));
  }

  default IntegerTransformer<R, OIN> size() {
    return this.toInteger(Functions.size());
  }

  default ListTransformer<R, OIN, E> subList(int begin, int end) {
    return this.toList(Printables.function("subList[" + begin + "," + end + "]", v -> v.subList(begin, end)));
  }

  default ListTransformer<R, OIN, E> subList(int begin) {
    return this.toList(Printables.function("subList[" + begin + "]", v -> v.subList(begin, v.size())));
  }

  default StreamTransformer<R, OIN, E> stream() {
    return this.toStream(Printables.function("listStream", Collection::stream));
  }

  default BooleanTransformer<R, OIN> isEmpty() {
    return this.toBoolean(Printables.function("listIsEmpty", List::isEmpty));
  }

  class Impl<
      R  extends Matcher<R, R, OIN, OIN>, OIN,
      E> extends Base<
      ListTransformer<R, OIN, E>,
      R,
      OIN,
      List<E>>
      implements ListTransformer<R, OIN, E> {
    public Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }

    @Override
    public ListChecker<R, OIN, E> createCorrespondingChecker(R root) {
      return new ListChecker.Impl<>(this::rootValue, root);
    }
  }
}
