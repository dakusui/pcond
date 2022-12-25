package com.github.dakusui.pcond.core;

import java.io.PrintStream;

import static com.github.dakusui.pcond.internals.InternalUtils.indent;

public enum DebuggingUtils {
  ;

  static <T, R> void printIo(String x, EvaluableIo<T, ? extends Evaluable<T>, R> io) {
    System.err.println(x + ":" + io.evaluableType() + ":" + io.evaluable() + "(" + io.input() + ")=" + io.output());
  }

  static <T> void printInput(String x, Evaluable<T> evaluable, ValueHolder<T> input) {
    System.err.println(x + ":" + evaluable + "(" + input + ")");
  }

  static <T, R> void printInputAndOutput(String x, Evaluable<T> evaluable, ValueHolder<T> input, ValueHolder<R> output) {
    System.err.println(x + ":" + evaluable + "(" + input + ")=" + output);
  }

  static <T> void printTo(EvaluationContext<T> evaluationContext, PrintStream ps, int indent) {
    ps.println(indent(indent) + "context=<" + evaluationContext + ">");
    for (Object each : evaluationContext.resultEntries()) {
      ps.println(indent(indent + 1) + each);
    }
  }
}
