package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.preds.ContextUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;

public enum Experimentals {
  ;

  public static Function<Stream<?>, Stream<Context>> nest(Collection<?> inner) {
    return Printables.function(() -> "nest" + formatObject(inner), (Stream<?> stream) -> ContextUtils.nest(stream, inner));
  }

  public static Function<Stream<?>, Stream<Context>> toContextStream() {
    return Printables.function(() -> "toContextStream", ContextUtils::toContextStream);
  }

  public static <T> Function<T, Context> toContext() {
    return Printables.function(() -> "toContext", ContextUtils::toContext);
  }

}
