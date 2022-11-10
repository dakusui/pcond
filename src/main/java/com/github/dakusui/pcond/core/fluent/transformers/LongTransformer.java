package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.LongChecker;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.longChecker;

public interface LongTransformer<OIN> extends ComparableNumberTransformer<LongTransformer<OIN>, LongChecker<OIN>, OIN, Long>, Matcher.ForLong<OIN> {
  class Impl<OIN> extends Base<LongTransformer<OIN>, OIN, Long> implements LongTransformer<OIN> {
    /**
     * Constructs an instance of this class.
     *
     * @param transformerName    THe name of transformer. This can be {@code null}.
     * @param parent             The parent of the new transformer. {@code null} if it is a root.
     * @param function           A function with which a given value is converted.
     * @param originalInputValue An original input value, if available. Otherwise {@code null}.
     */
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Long> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public LongChecker<OIN> then() {
      return longChecker(this.transformerName(), this.function(), this.originalInputValue());
    }
  }
}
