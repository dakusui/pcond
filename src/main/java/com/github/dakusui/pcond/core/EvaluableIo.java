package com.github.dakusui.pcond.core;

import static java.util.Objects.requireNonNull;

public class EvaluableIo<I, E extends Evaluable<I>, O> {
  final         ValueHolder<I>       input;
  private final E                    evaluable;
  private final EvaluationEntry.Type evaluableType;
  private       ValueHolder<O>       output;

  public EvaluableIo(ValueHolder<I> input, EvaluationEntry.Type evaluableType, E evaluable) {
    this.input = requireNonNull(input);
    this.evaluableType = requireNonNull(evaluableType);
    this.evaluable = requireNonNull(evaluable);
    this.output = ValueHolder.create();
  }

  public EvaluableIo<I, E, O> valueReturned(O value) {
    this.output = this.output.valueReturned(value);
    return this;
  }

  public EvaluableIo<I, E, O> exceptionThrown(Throwable throwable) {
    this.output = this.output.exceptionThrown(throwable);
    return this;
  }

  public EvaluableIo<I, E, O> evaluationSkipped() {
    this.output = this.output.evaluationSkipped();
    return this;
  }

  public ValueHolder<I> input() {
    return this.input;
  }

  public EvaluationEntry.Type evaluableType() {
    return this.evaluableType;
  }

  public E evaluable() {
    return this.evaluable;
  }


  public ValueHolder<O> output() {
    return this.output;
  }
}
