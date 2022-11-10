package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.FloatChecker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.floatChecker;

/**
 * A transformer interface for `Integer`(`int`) type.
 *
 * @param <OIN> The type of original input value.
 */
public interface FloatTransformer<OIN> extends ComparableNumberTransformer<FloatTransformer<OIN>, FloatChecker<OIN>, OIN, Float>, Matcher.ForFloat<OIN> {
  /**
   * An implementation of {@link FloatTransformer} interface.
   *
   * @param <OIN> The type of original input value.
   */
  class Impl<OIN> extends Base<FloatTransformer<OIN>, OIN, Float> implements FloatTransformer<OIN> {
    /**
     * Constructs an instance of this class.
     *
     * @param transformerName    THe name of transformer. This can be {@code null}.
     * @param parent             The parent of the new transformer. {@code null} if it is a root.
     * @param function           A function with which a given value is converted.
     * @param originalInputValue An original input value, if available. Otherwise {@code null}.
     */
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Float> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public FloatChecker<OIN> then() {
      return floatChecker(this.transformerName(), this.function(), this.originalInputValue());
    }
  }
}
