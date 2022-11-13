package com.github.dakusui.pcond.ut.fluent3;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

public class PrintingEvaluator implements Evaluator {
  int level=0;
  private void enter() {
    this.level++;
  }
  private void leave() {
    this.level--;
  }
  @Override
  public <T> void evaluate(T value, Evaluable.Conjunction<T> conjunction) {
    enter();
    try {
      printItem(value, conjunction);
      conjunction.children().forEach(each -> each.accept(value, this));
    } finally {
      leave();
    }
  }

  private void printItem(Object value, Object predicate) {
    System.out.printf("%svalue: '%s', predicate: '%s'%n", InternalUtils.indent(level), value, predicate);
  }

  @Override
  public <T> void evaluate(T value, Evaluable.Disjunction<T> disjunction) {
    enter();
    try {
      printItem(value, disjunction);
      disjunction.children().forEach(each -> each.accept(value, this));
    } finally {
      leave();
    }
  }

  @Override
  public <T> void evaluate(T value, Evaluable.Negation<T> negation) {
    enter();
    try {
      printItem(value, negation);
      negation.target().accept(value, this);
    } finally {
      leave();
    }
  }

  @Override
  public <T> void evaluate(T value, Evaluable.LeafPred<T> leafPred) {
    enter();
    try {
      printItem(value, leafPred);
      leafPred.accept(value, this);
    } finally {
      leave();
    }
  }

  @Override
  public void evaluate(Context value, Evaluable.ContextPred contextPred) {

  }

  @Override
  public <T, R> void evaluate(T value, Evaluable.Transformation<T, R> transformation) {

  }

  @Override
  public <T> void evaluate(T value, Evaluable.Func<T> func) {

  }

  @Override
  public <E> void evaluate(Stream<? extends E> value, Evaluable.StreamPred<E> streamPred) {

  }

  @Override
  public <T> T resultValue() {
    return null;
  }

  @Override
  public List<Entry> resultEntries() {
    return null;
  }
}
