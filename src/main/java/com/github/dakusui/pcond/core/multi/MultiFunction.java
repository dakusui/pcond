package com.github.dakusui.pcond.core.multi;

import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.currying.CurryingUtils;
import com.github.dakusui.pcond.core.currying.FormattingUtils;
import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.preds.ContextUtils;
import com.github.dakusui.pcond.core.printable.PrintableFunction;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.core.currying.Checks.validateParamOrderList;
import static com.github.dakusui.pcond.core.currying.FormattingUtils.formatMethodName;
import static com.github.dakusui.pcond.core.currying.ReflectionsUtils.invokeStaticMethod;
import static com.github.dakusui.pcond.internals.InternalChecks.requireArgumentListSize;
import static com.github.dakusui.pcond.internals.InternalChecks.requireStaticMethod;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public interface MultiFunction<R> extends Function<List<? super Object>, R> {
  @SuppressWarnings("unchecked")
  static <R> MultiFunction<R> createFromStaticMethod(Method method, List<Integer> paramOrder) {
    validateParamOrderList(paramOrder, method.getParameterCount());
    requireStaticMethod(method);
    return (MultiFunction<R>) new Builder<>(args -> invokeStaticMethod(method, (paramOrder).stream().map(args::get).toArray()))
        .name(method.getName())
        .formatter(() -> formatMethodName(method) + FormattingUtils.formatParameterOrder(paramOrder))
        .addParameters(paramOrder.stream().map(i -> method.getParameterTypes()[i]).collect(toList()))
        .identity(asList(method, validateParamOrderList(paramOrder, method.getParameterCount())))
        .$();
  }

  @SuppressWarnings("unchecked")
  static <T, R> MultiFunction<R> toMulti(Function<T, R> func) {
    requireNonNull(func);
    return new Builder<>(args -> func.apply((T) args.get(0)))
        .addParameter(Object.class)
        .name(func.toString())
        .$();
  }

  @SuppressWarnings("unchecked")
  static <T> MultiFunction<Boolean> toMulti(Predicate<T> pred) {
    requireNonNull(pred);
    return new Builder<>(args -> pred.test((T) args.get(0)))
        .addParameter(Object.class)
        .name(pred.toString())
        .formatter(pred::toString)
        .$();
  }

  String name();

  int arity();

  Class<?> parameterType(int i);

  Class<? extends R> returnType();

  @SuppressWarnings("unchecked")
  default CurriedFunction<Object, Object> curry() {
    return CurryingUtils.curry((MultiFunction<Object>) this);
  }

  default Predicate<Context> toContextPredicate() {
    return ContextUtils.toContextPredicate(this.curry());
  }

  abstract class Base<RR> extends PrintableFunction<List<? super Object>, RR> implements MultiFunction<RR> {
    final Object identity;

    protected Base(Supplier<String> s, Function<? super List<? super Object>, ? extends RR> function, Object identity) {
      super(s, function);
      this.identity = identity;
    }

    @Override
    public int hashCode() {
      return identity.hashCode();
    }

    @Override
    public boolean equals(Object anotherObject) {
      if (anotherObject == this)
        return true;
      if (anotherObject instanceof MultiFunction.Base) {
        Base<?> another = (Base<?>) anotherObject;
        return this.identity.equals(another.identity);
      }
      return false;
    }
  }

  class Builder<R> {
    final Function<List<Object>, R> body;
    final List<Class<?>>            parameterTypes = new LinkedList<>();
    Object           identity  = new Object();
    String           name      = "(anonymous)";
    Supplier<String> formatter = () -> this.name + "(" + parameterTypes.stream().map(Class::getSimpleName).collect(joining(",")) + ")";

    public Builder(Function<List<Object>, R> body) {
      requireNonNull(body);
      this.body = args -> body.apply(requireArgumentListSize(requireNonNull(args), parameterTypes.size()));
    }

    public Builder<R> name(String name) {
      this.name = name;
      return this;
    }

    public Builder<R> addParameters(List<Class<?>> parameterTypes) {
      requireNonNull(parameterTypes).stream().map(this::addParameter).forEach(Objects::requireNonNull);
      return this;
    }

    public Builder<R> addParameter(Class<?> parameterType) {
      this.parameterTypes.add(requireNonNull(parameterType));
      return this;
    }

    public Builder<R> formatter(Supplier<String> formatter) {
      this.formatter = requireNonNull(formatter);
      return this;
    }

    public Builder<R> identity(Object identity) {
      this.identity = requireNonNull(identity);
      return this;
    }

    public MultiFunction<R> $() {
      return new Base<R>(this.formatter, this.body, this.identity) {
        @Override
        public String name() {
          return name;
        }

        @Override
        public int arity() {
          return parameterTypes.size();
        }

        @Override
        public Class<?> parameterType(int i) {
          return parameterTypes.get(i);
        }
      };
    }
  }
}
