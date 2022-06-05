package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.LongVerifier;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Verifier.Factory.longVerifier;
import static com.github.dakusui.pcond.internals.InternalUtils.dummyPredicate;

public interface LongTransformer<OIN> extends ComparableNumberTransformer<LongTransformer<OIN>, LongVerifier<OIN>, OIN, Long>, Matcher.ForLong<OIN> {
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
    public LongVerifier<OIN> then() {
      return longVerifier(this.transformerName(), this.function(), dummyPredicate(), this.originalInputValue());
    }
  }
}
