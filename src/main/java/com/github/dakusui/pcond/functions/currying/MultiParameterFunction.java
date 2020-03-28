package com.github.dakusui.pcond.functions.currying;

import java.util.List;
import java.util.function.Function;

public interface MultiParameterFunction<R> extends Function<List<? super Object>, R> {
  int arity();

  Class<?> parameterType(int i);

  Class<? extends R> returnType();
}
