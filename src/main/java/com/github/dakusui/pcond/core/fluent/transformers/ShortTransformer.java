package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.ShortChecker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.shortChecker;

/**
 * A transformer for `short` type.
 *
 * @param <OIN> The type of original input value.
 */
public interface ShortTransformer<OIN> extends ComparableNumberTransformer<ShortTransformer<OIN>, ShortChecker<OIN>, OIN, Short>, Matcher.ForShort<OIN> {

  @Override
  ShortChecker<OIN> then();

  class Impl<OIN> extends Base<ShortTransformer<OIN>, OIN, Short> implements ShortTransformer<OIN> {

    /**
     * Constructs an instance of this class.
     *
     * @param transformerName    THe name of transformer. This can be {@code null}.
     * @param parent             The parent of the new transformer. {@code null} if it is a root.
     * @param function           A function with which a given value is converted.
     * @param originalInputValue An original input value, if available. Otherwise {@code null}.
     */
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Short> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public ShortChecker<OIN> then() {
      return shortChecker(this.transformerName(), this.function(), this.originalInputValue());
    }
  }
}
