package com.github.dakusui.pcond.core;

import static java.util.Objects.requireNonNull;

public class EvaluableIo<I, E extends Evaluable<I>, O> {
  final         EvaluationResultHolder<I> input;
  private final E                         evaluable;
  private       EvaluationResultHolder<O> output;

  public EvaluableIo(EvaluationResultHolder<I> input, E evaluable) {
    this.input = requireNonNull(input);
    this.evaluable = requireNonNull(evaluable);
    this.output = EvaluationResultHolder.create();
  }

  public EvaluableIo<I, E, O> valueReturned(O value) {
    this.output = this.output.valueReturned(value);
    return this;
  }

  public EvaluableIo<I, E, O> exceptionThrown(Throwable throwable) {
    this.output = this.output.exceptionThrown(throwable);
    return this;
  }

  public EvaluationResultHolder<I> input() {
    return this.input;
  }

  public E evaluable() {
    return this.evaluable;
  }

  public EvaluationResultHolder<O> output() {
    return this.output;
  }
}
