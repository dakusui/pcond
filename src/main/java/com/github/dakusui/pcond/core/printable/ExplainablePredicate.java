package com.github.dakusui.pcond.core.printable;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static com.github.dakusui.pcond.internals.InternalUtils.toNonStringObject;
import static java.util.Collections.emptyList;

public abstract class ExplainablePredicate<V> extends PrintablePredicate<V>
    implements Predicate<V>, Evaluable.LeafPred<V>, Evaluator.Explainable {
  V actualInput;

  protected ExplainablePredicate(Supplier<String> formatter, Predicate<? super V> predicate) {
    super(new Object(), emptyList(), formatter, predicate);
  }

  @Override
  public boolean test(V value) {
    actualInput = value;
    return super.test(value);
  }

  protected V actualInput() {
    return this.actualInput;
  }

  @Override
  public Object explainActual(Object actualValue) {
    return actualInput();
  }

  @Override
  public Predicate<? super V> predicate() {
    return (Predicate<V>) v -> {
      actualInput = v;
      return super.predicate.test(v);
    };
  }

  public static Predicate<String> explainableStringIsEqualTo(String str) {
    return new ExplainablePredicate<String>(() -> "stringIsEqualTo[" + formatObject(toNonStringObject(str)) + "]", v -> Objects.equals(str, v)) {
      @Override
      public Object explainOutputExpectation() {
        return str;
      }
    };
  }
}
