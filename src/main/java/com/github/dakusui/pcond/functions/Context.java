package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Formattable;
import java.util.Formatter;
import java.util.List;

import static java.util.Collections.singletonList;

public interface Context extends Formattable {
  default int size() {
    return values().size();
  }

  @SuppressWarnings("unchecked")
  default <T> T valueAt(int i) {
    return (T) values().get(i);
  }

  default Context append(Object o) {
    return () -> InternalUtils.append(values(), o);
  }

  @Override
  default void formatTo(Formatter formatter, int flags, int width, int precision) {
    formatter.format("context:%s", this.values());
  }

  List<Object> values();

  static Context from(Object o) {
    return () -> singletonList(o);
  }
}
