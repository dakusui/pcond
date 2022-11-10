package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.checkers.BooleanChecker;
import com.github.dakusui.pcond.core.fluent.Matcher;

import java.util.function.Function;

import static com.github.dakusui.pcond.core.fluent.Checker.Factory.booleanChecker;

public interface BooleanTransformer<OIN> extends Transformer<BooleanTransformer<OIN>, OIN, Boolean>, Matcher.ForBoolean<OIN> {
  @Override
  BooleanChecker<OIN> then();

  class Impl<OIN> extends Base<BooleanTransformer<OIN>, OIN, Boolean> implements BooleanTransformer<OIN> {

    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends Boolean> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public BooleanChecker<OIN> then() {
      return booleanChecker(this.transformerName(), this.function(), this.originalInputValue());
    }
  }
}
