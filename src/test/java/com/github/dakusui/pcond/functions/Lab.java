package com.github.dakusui.pcond.functions;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

enum Lab {
  ;

  @SuppressWarnings("unchecked")
  private static final PrintablePredicate.Factory<?, List<Object>> MATCHES_ALL_OF_FACTORY = Printables.predicateFactory(
      (List<Object> v) -> format("%s matchesAllOf(%s)", v.get(1), v.get(0)),
      (List<Object> v) -> p -> ((Collection<Object>) v.get(0)).stream().allMatch((Predicate<Object>) v.get(1)));
  @SuppressWarnings("unchecked")
  private static final PrintablePredicate.Factory<?, List<Object>> MATCHES_ANY_OF_FACTORY = Printables.predicateFactory(
      (List<Object> v) -> format("matchesAnyOf(%s,%s)", v.get(0), v.get(1)),
      (List<Object> v) -> p -> ((Collection<Object>) v.get(0)).stream().anyMatch((Predicate<Object>) v.get(1)));
  @SuppressWarnings("unchecked")
  private static final PrintablePredicate.Factory<?, List<Object>> MATCHES_NONE_OF_FACTORY = Printables.predicateFactory(
      (List<Object> v) -> format("matchesNoneOf(%s,%s)", v.get(0), v.get(1)),
      (List<Object> v) -> p -> ((Collection<Object>) v.get(0)).stream().noneMatch((Predicate<Object>) v.get(1)));

  public static Function<Collection<?>, Stream<List<?>>> cartesianWith(Collection<?>... inners) {
    return Printables.function(() -> "cartesianWith" + formatObject(inners), outer -> cartesian(outer, asList(inners)));
  }

  public static Predicate<List<?>> uncurry(Function<?, Predicate<Object>> curriedFunc) {
    return Printables.predicate(() -> "uncurried:" + curriedFunc, args -> applyValues(curriedFunc, args));
  }

  public static void main2(String... args) {
    System.out.println(isInstanceOf(Serializable.class));
    System.out.println(isInstanceOf(Serializable.class).test(null));

    System.out.println(applyValues(isInstanceOf(), asList("hello", String.class)) + "");
    System.out.println(applyValues(isInstanceOf(), asList("hello", Map.class)) + "");
  }

  public static <T> T applyValues(Function<?, ?> func, List<?> args) {
    requireArgument(requireNonNull(args), not(isEmpty()));
    Object ret = func;
    for (Object arg : args)
      ret = applyOrTest(ret, arg);
    return (T) ret;
  }

  private static Object applyOrTest(Object func, Object arg) {
    requireArgument(func, or(isInstanceOf(Function.class), isInstanceOf(Predicate.class)));
    if (func instanceof Predicate)
      return ((Predicate<Object>) func).test(arg);
    return ((Function<Object, Object>) func).apply(arg);
  }

  public static void main(String... args) {
    System.out.println(isInstanceOf());
    System.out.println(isInstanceOf().apply(String.class));
    System.out.println(isInstanceOf().apply(String.class).test("Hello"));
    System.out.println(isInstanceOf().apply(Map.class).test("Hello"));
    System.out.println(isInstanceOf().apply(Class.class).test("Hello"));
    System.out.println(">>" + applyValues(isInstanceOf(), asList("Hello", Class.class)));
    System.out.println(requireArgument(
        asList("hello", new HashMap<>(), Object.class),
        when(cartesianWith(asList(Map.class, List.class, String.class)))
            .then(noneMatch(uncurry(isInstanceOf())))
    ));
  }

  private static Stream<List<?>> cartesian(Collection<?> outer, List<Collection<?>> inners) {
    Stream<List<?>> ret = wrapWithList(outer.stream());
    for (Collection<?> i : inners)
      ret = cartesianPrivate(ret, i.stream());
    return ret;
  }

  private static Stream<List<?>> cartesianPrivate(Stream<List<?>> outer, Stream<?> inner) {
    return outer.flatMap(i -> inner.map(j -> new ArrayList<Object>(i) {{
      this.add(0, j);
    }}));
  }

  private static Stream<List<?>> wrapWithList(Stream<?> stream) {
    return stream.map(Collections::singletonList);
  }

  @SuppressWarnings("unchecked")
  public static <E> Predicate<E> matchesAllOf(Collection<? extends E> collection, Predicate<E> cond) {
    return (Predicate<E>) MATCHES_ALL_OF_FACTORY.create(asList(collection, cond));
  }

  @SuppressWarnings("unchecked")
  public static <E> Predicate<E> matchesAnyOf(Collection<? extends E> collection, Predicate<E> cond) {
    return (Predicate<E>) MATCHES_ANY_OF_FACTORY.create(asList(collection, cond));
  }

  @SuppressWarnings("unchecked")
  public static <E> Predicate<E> matchesNoneOf(Collection<? extends E> collection, Predicate<E> cond) {
    return (Predicate<E>) MATCHES_NONE_OF_FACTORY.create(asList(collection, cond));
  }

  public static <R> Function<ExtraFunctions.Context, R> apply(MultiParameterFunction<R> multiParameterFunction, int... orderArgs) {
    return context -> {
      IntStream orderStream = Arrays.stream(ExtraFunctions.normalizeOrderArgs(context, orderArgs));
      return multiParameterFunction.apply(orderStream.distinct().mapToObj(context::valueAt).collect(Collectors.toList()));
    };
  }
}
