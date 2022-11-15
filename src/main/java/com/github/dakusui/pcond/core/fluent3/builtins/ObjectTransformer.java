package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent3.CustomTransformer;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Functions;

import java.util.function.Supplier;


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
  default StringTransformer<RX, OIN> asString() {
    return toString(Functions.cast(String.class));
  }

  default IntegerTransformer<RX, OIN> asInteger() {
    return toInteger(Functions.cast(Integer.class));
  }

  default LongTransformer<RX, OIN> asLong() {
    return toLong(Functions.cast(Long.class));
  }

  default ShortTransformer<RX, OIN> asShort() {
    return toShort(Functions.cast(Short.class));
  }

  default DoubleTransformer<RX, OIN> asDouble() {
    return toDouble(Functions.cast(Double.class));
  }

  default FloatTransformer<RX, OIN> asFloat() {
    return toFloat(Functions.cast(Float.class));
  }

  static
  <R extends Matcher<R, R, E, E>, E> ObjectTransformer<R, E, E> create(Supplier<E> value) {
    return new Impl<>(value, null);
  }
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
    public Impl(Supplier<OIN> rootValue, RX root) {
      super(rootValue, root);
    }

    @Override
    public ObjectChecker<RX, OIN, E> createCorrespondingChecker(RX root) {
      return new ObjectChecker.Impl<>(this::rootValue, root);
    }
  }
}
