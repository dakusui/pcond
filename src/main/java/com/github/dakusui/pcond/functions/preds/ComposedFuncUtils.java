package com.github.dakusui.pcond.functions.preds;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.functions.PrintableFunction;

import java.util.List;
import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.toEvaluableIfNecessary;

public enum ComposedFuncUtils {
  ;

  public static <T, R> Factory<T, R> factory(
      Function<List<Function<Object, Object>>, String> nameComposer, Function<List<Function<Object, Object>>, Function<T, R>> ff) {
    return new ComposedFuncUtils.Factory<T, R>(nameComposer, ff) {
      public PrintableFunction<T, R> create(List<Function<Object, Object>> arg) {
        final Lambda.Spec<List<Function<Object, Object>>> spec = new Lambda.Spec<>(this, arg, PrintableFunctionFromFactory.class);
        final Function<? super T, ? extends R> function = createFunction(arg);
        return new PrintableFunctionFromFactory<T, R, List<Function<Object, Object>>>(
            () -> this.nameComposer().apply(arg), function, createHead(arg), createTail(arg)) {
          @Override
          public Spec<List<Function<Object, Object>>> spec() {
            return spec;
          }
        };
      }
    };
  }

  public static abstract class Factory<T, R> extends BaseFuncUtils.Factory<T, R, List<Function<Object, Object>>> {
    private final Function<List<Function<Object, Object>>, Function<T, R>> ff;

    protected Factory(Function<List<Function<Object, Object>>, String> s, Function<List<Function<Object, Object>>, Function<T, R>> ff) {
      super(s);
      this.ff = ff;
    }

    public Function<T, R> createFunction(List<Function<Object, Object>> arg) {
      return ff.apply(arg);
    }

    @SuppressWarnings("unchecked")
    Function<? super T, ? extends R> createHead(List<Function<Object, Object>> arg) {
      return (Function<? super T, ? extends R>) arg.get(0);
    }

    Evaluable<T> createTail(List<Function<Object, Object>> arg) {
      return toEvaluableIfNecessary(arg.get(1));
    }
  }
}
