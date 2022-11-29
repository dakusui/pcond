package com.github.dakusui.pcond.core.context;

import com.github.dakusui.pcond.core.currying.CurriedFunction;
import com.github.dakusui.pcond.core.currying.CurryingUtils;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

/**
 * A utility class that collects helper methods for the "Context" mechanism.
 */
public enum VariableBundleUtils {
  ;

  public static Stream<VariableBundle> nest(Stream<?> stream, Collection<?> inner) {
    System.out.println("stream:" + stream);
    Stream<VariableBundle> ret = toVariableBundleStream(stream).flatMap((VariableBundle variableBundle) -> inner.stream().map((Function<Object, VariableBundle>) variableBundle::append));
    System.out.println("ret:" + ret);
    return ret;
  }

  public static Stream<VariableBundle> toVariableBundleStream(Stream<?> stream) {
    return stream.map(VariableBundleUtils::toVariableBundle);
  }

  public static <T> VariableBundle toVariableBundle(T t) {
    return t instanceof VariableBundle ? (VariableBundle) t : VariableBundle.from(t);
  }

  public static <R> Predicate<VariableBundle> applyAndTest(CurriedFunction<Object, Object> curriedFunction, Predicate<? super R> pred, @SuppressWarnings("SameParameterValue") Class<? extends R> type, int... orderArgs) {
    List<Object> spec = asList(curriedFunction, pred, asList(Arrays.stream(orderArgs).boxed().toArray()));
    return PrintablePredicateFactory.parameterizedLeaf(
        args -> () -> String.format("%s(%s%s)", args.get(1), args.get(0), args.get(2)), args -> (VariableBundle variableBundle) -> (pred).test(type.cast(CurryingUtils.applyCurriedFunction(curriedFunction, orderArgs).apply(variableBundle))), spec, VariableBundleUtils.class
    );
  }

  public static Predicate<VariableBundle> toContextPredicate(CurriedFunction<Object, Object> curriedFunction, int... orderArgs) {
    return VariableBundleUtils.applyAndTest(curriedFunction, createPredicate("contextPredicate", Predicates.isTrue()), Boolean.class, orderArgs);
  }

  public static <T> Predicate<T> createPredicate(String s, Predicate<T> predicate) {
    requireNonNull(s);
    return PrintablePredicateFactory.leaf(() -> s, predicate, VariableBundleUtils.class);
  }
}
