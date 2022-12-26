package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.validator.Validator;

import java.io.PrintStream;

import static com.github.dakusui.pcond.internals.InternalUtils.indent;

public enum DebuggingUtils {
  ;

  static <T, R> void printIo(String x, EvaluableIo<T, ? extends Evaluable<T>, R> io) {
    if (isDebugEnabled())
      System.err.println(x + ":" + io.evaluableType() + ":" + io.evaluable() + "(" + io.input() + ")=" + io.output());
  }

  static <T> void printInput(String x, Evaluable<T> evaluable, ValueHolder<T> input) {
    if (isDebugEnabled())
      System.err.println(x + ":" + evaluable + "(" + input + ")");
  }

  static <T, R> void printInputAndOutput(String x, Evaluable<T> evaluable, ValueHolder<T> input, ValueHolder<R> output) {
    if (isDebugEnabled())
      System.err.println(x + ":" + evaluable + "(" + input + ")=" + output);
  }

  static <T> void printTo(EvaluationContext<T> evaluationContext, PrintStream ps, int indent) {
    if (isDebugEnabled()) {
      ps.println(indent(indent) + "context=<" + evaluationContext + ">");
      for (Object each : evaluationContext.resultEntries()) {
        ps.println(indent(indent + 1) + each);
      }
    }
  }

  public static boolean isDebugEnabled() {
    return Validator.INSTANCE.configuration().isDebugModeEnabled();
  }
}
