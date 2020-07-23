package com.github.dakusui.pcond.core.context;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class PrintableContextPredicate extends PrintablePredicate<Context> implements Evaluable.ContextPred {
  final Evaluable<?> enclosed;
  final Object       identity;
  final int          argIndex;

  public <T> PrintableContextPredicate(Predicate<T> predicate_, int argIndex) {
    super(
        () -> format("contextPredicate[%s,%s]", predicate_, argIndex),
        context -> predicate_.test(context.<T>valueAt(argIndex)));
    this.enclosed = InternalUtils.toEvaluableIfNecessary(predicate_);
    this.argIndex = argIndex;
    this.identity = asList(predicate_, argIndex);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <TT> Evaluable<? super TT> enclosed() {
    return (Evaluable<? super TT>) enclosed;
  }

  @Override
  public int argIndex() {
    return argIndex;
  }

  @Override
  public int hashCode() {
    return this.identity.hashCode();
  }

  @Override
  public boolean equals(Object anotherObject) {
    if (!(anotherObject instanceof PrintableContextPredicate))
      return false;
    PrintableContextPredicate another = (PrintableContextPredicate) anotherObject;
    return this.identity.equals(another.identity);
  }
}
