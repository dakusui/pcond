package com.github.dakusui.pcond.core.fluent.builtins;

import com.github.dakusui.pcond.core.fluent.AbstractObjectTransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;
import static java.lang.String.format;


/**
 * This interface is used for object whose type doesn't have an explicit support.
 * Do not try to extend/implement this class to support your own class.
 */
public interface ObjectTransformer<
    T,
    E
    > extends
    AbstractObjectTransformer<
        ObjectTransformer<T, E>,
        ObjectChecker<T, E>,
        T,
        E> {

  @SuppressWarnings("unchecked")
  default ObjectTransformer<T, E> transform(Function<ObjectTransformer<T, E>, Predicate<E>> clause) {
    return this.addTransformAndCheckClause(tx -> clause.apply((ObjectTransformer<T, E>) tx));
  }

  default StringTransformer<T> asString() {
    return toString(Functions.cast(String.class));
  }

  default IntegerTransformer<T> asInteger() {
    return toInteger(Functions.cast(Integer.class));
  }

  default LongTransformer<T> asLong() {
    return toLong(Functions.cast(Long.class));
  }

  default ShortTransformer<T> asShort() {
    return toShort(Functions.cast(Short.class));
  }

  default DoubleTransformer<T> asDouble() {
    return toDouble(Functions.cast(Double.class));
  }

  default FloatTransformer<T> asFloat() {
    return toFloat(Functions.cast(Float.class));
  }

  default BooleanTransformer<T> asBoolean() {
    return toBoolean(Functions.cast(Boolean.class));
  }
  
  @SuppressWarnings("unchecked")
  default <EE> ListTransformer<T, EE> asListOf(Class<EE> type) {
    return toList(Printables.function(format("castTo[List<%s>]", type.getSimpleName()), v -> (List<EE>)v));
  }
  
  @SuppressWarnings("unchecked")
  default <EE> ListTransformer<T, EE> asList() {
    return (ListTransformer<T, EE>) asListOf(Object.class);
  }
  
  static <E> ObjectTransformer<E, E> create(Supplier<E> value) {
    return new Impl<>(value, trivialIdentityFunction());
  }

  class Impl<
      T,
      E> extends
      Transformer.Base<
          ObjectTransformer<T, E>,
          ObjectChecker<T, E>,
          T,
          E> implements
      ObjectTransformer<T, E> {
    public Impl(Supplier<T> rootValue, Function<T, E> root) {
      super(rootValue, root);
    }

    @Override
    protected ObjectChecker<T, E> toChecker(Function<T, E> transformFunction) {
      return new ObjectChecker.Impl<>(this::baseValue, transformFunction);
    }

    @Override
    protected ObjectTransformer<E, E> rebase() {
      return new ObjectTransformer.Impl<>(this::value, trivialIdentityFunction());
    }

  }
}
