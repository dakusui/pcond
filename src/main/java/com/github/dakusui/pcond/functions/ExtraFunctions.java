package com.github.dakusui.pcond.functions;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Predicates.*;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.util.Arrays.asList;

public class ExtraFunctions {
  public static Function<Collection<?>, Stream<List<?>>> cartesianWith(Collection<?>... inners) {
    return Printables.function(() -> "cartesianWith" + formatObject(inners), outer -> Def.cartesian(outer, asList(inners)));
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

  enum Def {
    ;

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

  }
}
