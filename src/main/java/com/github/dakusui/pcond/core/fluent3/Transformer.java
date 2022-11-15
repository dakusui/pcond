package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.fluent3.builtins.*;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public interface Transformer<
    TX extends Transformer<TX, RX, V, OIN, T>,
    RX extends Matcher<RX, RX, OIN, OIN>,
    V extends Checker<V, RX, OIN, T>,
    OIN,
    T>
    extends Matcher<TX, RX, OIN, T> {
  default V then() {
    V ret = createCorrespondingChecker(this.root());
    this.appendChild(tx -> ret.toPredicate());
    return ret;
  }

  default <E> ObjectTransformer<RX, OIN, E> toObject(Function<? super T, E> func) {
    requireNonNull(func);
    ObjectTransformer<RX, OIN, E> ret = new ObjectTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default BooleanTransformer<RX, OIN> toBoolean(Function<? super T, Boolean> func) {
    requireNonNull(func);
    BooleanTransformer<RX, OIN> ret = new BooleanTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default IntegerTransformer<RX, OIN> toInteger(Function<? super T, Integer> func) {
    requireNonNull(func);
    IntegerTransformer<RX, OIN> ret = new IntegerTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default LongTransformer<RX, OIN> toLong(Function<? super T, Long> func) {
    requireNonNull(func);
    LongTransformer<RX, OIN> ret = new LongTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default ShortTransformer<RX, OIN> toShort(Function<? super T, Short> func) {
    requireNonNull(func);
    ShortTransformer<RX, OIN> ret = new ShortTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }
  default DoubleTransformer<RX, OIN> toDouble(Function<? super T, Double> func) {
    requireNonNull(func);
    DoubleTransformer<RX, OIN> ret = new DoubleTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }
  default FloatTransformer<RX, OIN> toFloat(Function<? super T, Float> func) {
    requireNonNull(func);
    FloatTransformer<RX, OIN> ret = new FloatTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }
  default StringTransformer<RX, OIN> toString(Function<? super T, String> func) {
    requireNonNull(func);
    StringTransformer<RX, OIN> ret = new StringTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default <E> ListTransformer<RX, OIN, E> toList(Function<? super T, List<E>> func) {
    requireNonNull(func);
    ListTransformer<RX, OIN, E> ret = new ListTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default <E> StreamTransformer<RX, OIN, E> toStream(Function<? super T, Stream<E>> func) {
    requireNonNull(func);
    StreamTransformer<RX, OIN, E> ret = new StreamTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  default <E extends Throwable> ThrowableTransformer<RX, OIN, E> toThrowable(Function<? super T, E> func) {
    requireNonNull(func);
    ThrowableTransformer<RX, OIN, E> ret = new ThrowableTransformer.Impl<>(this::rootValue, this.root());
    this.appendChild(tx -> Predicates.transform(func).check(ret.toPredicate()));
    return ret;
  }

  V createCorrespondingChecker(RX root);
}
