package com.github.dakusui.crest.core;

import com.github.dakusui.crest.core.Call.Arg;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import static com.github.dakusui.crest.utils.InternalUtils.areArgsCompatible;
import static com.github.dakusui.crest.utils.InternalUtils.requireArgument;
import static com.github.dakusui.crest.utils.InternalUtils.withBoxingIsAssignableFrom;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

public interface MethodSelector extends BiFunction<List<Method>, Object[], List<Method>>, Formattable {
  default MethodSelector andThen(MethodSelector another) {
    return new MethodSelector() {
      @Override
      public List<Method> select(List<Method> methods, Object[] args) {
        return another.select(MethodSelector.this.apply(methods, args), args);
      }

      @Override
      public String describe() {
        return String.format("%s&&%s", MethodSelector.this.describe(), another.describe());
      }
    };
  }

  default List<Method> apply(List<Method> methods, Object[] args) {
    return this.select(methods, args);
  }

  @Override
  default void formatTo(Formatter formatter, int flags, int width, int precision) {
    formatter.format("%s", this.describe());
  }

  List<Method> select(List<Method> methods, Object[] args);

  String describe();

  class Default implements MethodSelector {
    @Override
    public List<Method> select(List<Method> methods, Object[] args) {
      return methods
          .stream()
          .filter(m -> areArgsCompatible(m.getParameterTypes(), args))
          .collect(toList());
    }

    @Override
    public String describe() {
      return "default";
    }
  }

  class PreferNarrower implements MethodSelector {
    @Override
    public List<Method> select(List<Method> methods, Object[] args) {
      if (methods.size() < 2)
        return methods;
      List<Method> ret = new LinkedList<>();
      for (Method i : methods) {
        if (methods.stream().filter(j -> j != i).noneMatch(j -> compareNarrowness(j, i) > 0))
          ret.add(i);
      }
      return ret;
    }

    @Override
    public String describe() {
      return "preferNarrower";
    }

    /**
     * If {@code a} is 'narrower' than {@code b}, positive integer will be returned.
     * If {@code b} is 'narrower' than {@code a}, negative integer will be returned.
     * Otherwise {@code zero}.
     * <p>
     * 'Narrower' means that every parameter of {@code a} is assignable to corresponding
     * one of {@code b}, but any of {@code b} cannot be assigned to {@code a}'s
     * corresponding parameter.
     *
     * @param a A method.
     * @param b A method to be compared with {@code a}.
     * @return a negative integer, zero, or a positive integer as method {@code a}
     * is less compatible than, as compatible as, or more compatible than
     * the method {@code b} object.
     */
    private static int compareNarrowness(Method a, Method b) {
      if (isCompatibleWith(a, b) && isCompatibleWith(b, a))
        return 0;
      if (!isCompatibleWith(a, b) && !isCompatibleWith(b, a))
        return 0;
      return isCompatibleWith(a, b)
          ? -1
          : 1;
    }

    private static boolean isCompatibleWith(Method a, Method b) {
      requireArgument(requireNonNull(a), (Method v) -> v.getParameterCount() == requireNonNull(b).getParameterCount());
      if (Objects.equals(a, b))
        return true;
      return IntStream.range(0, a.getParameterCount()).allMatch(
          i -> withBoxingIsAssignableFrom(a.getParameterTypes()[i], b.getParameterTypes()[i])
      );
    }
  }

  class PreferExact implements MethodSelector {
    @Override
    public List<Method> select(List<Method> methods, Object[] args) {
      if (methods.size() < 2)
        return methods;
      List<Method> work = methods;
      for (int i = 0; i < args.length; i++) {
        Object argObj = args[i];
        if (!(argObj instanceof Arg))
          continue;
        int ii = i;
        List<Method> tmp = work.stream().filter(m -> m.getParameterTypes()[ii].equals(((Arg) argObj).type())).collect(toList());
        if (!tmp.isEmpty())
          work = tmp;
      }
      return work;
    }

    @Override
    public String describe() {
      return "preferExact";
    }
  }
}
