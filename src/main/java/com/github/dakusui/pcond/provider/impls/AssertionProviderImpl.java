package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.provider.*;

import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.internals.InternalUtils.executionFailure;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

public class AssertionProviderImpl implements AssertionProvider {

  private final Configuration configuration;

  public AssertionProviderImpl(Properties properties) {
    this.configuration = new Configuration.Builder(properties)
        .assertionProviderClass(this.getClass())
        .useEvaluator(true)
        .build();
  }

  @Override
  public Configuration configuration() {
    return this.configuration;
  }

  public static class Result {
    final        boolean               result;
    public final List<Evaluator.Entry> entries;

    public Result(boolean result, List<Evaluator.Entry> entries) {
      this.result = result;
      this.entries = entries;
    }

    public boolean result() {
      return this.result;
    }
  }
}
