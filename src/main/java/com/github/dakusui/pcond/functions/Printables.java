package com.github.dakusui.pcond.functions;


import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public enum Printables {
  ;

  public static <T> Predicate<T> predicate(Supplier<String> s, Predicate<T> predicate) {
    return Printables.printablePredicate(s.get(), predicate);
  }

  public static <T> Predicate<T> predicate(String s, Predicate<T> predicate) {
    return predicate(() -> s, predicate);
  }

  public static <T, R> Function<T, R> function(Supplier<String> s, Function<T, R> function) {
    return Printables.printableFunction(s.get(), function);
  }

  public static <T, R> Function<T, R> function(String s, Function<T, R> function) {
    return function(() -> s, function);
  }

  public static <T> Predicate<T> printablePredicate(String s, Predicate<T> predicate) {
    return new PrintablePredicate.Leaf<>(() -> s, predicate);
  }

  public static <T, R> Function<T, R> printableFunction(String s, Function<? super T, ? extends R> function) {
    return PrintableFunction.create(s, function);
  }

  /**
   * //@formatter:off
   * Returns a factory object with which you can create a new "printable" function.
   * The returned factory has a method `PrintableFunction.Factory<T,R,E>#create(E)`.
   * `E` is the type of a parameter from which a desired function is created.
   * The returned factory creates a function object, which uses the argument passed as the parameter to compute values to be returned from `hashCode()` and `equals(Object)` methods.
   *
   * `E` can be anything, however, it is useful to use `List<Object>`, which contains elements necessary to build your own function.
   *
   * Following is an example to illustrate how to use this method.
   *
   * [source,java]
   * ----
   * class Example {
   *   public void example() {
   *     PrintableFunction.Factory<String, String, List<Object>> functionFactory = pathToUriFunctionFactory();
   *     Function<String, String> pathToUriOnLocalHost = functionFactory.create(asList("http", "localhost", 80));
   *     System.out.println(pathToUriOnLocalHost);
   *     System.out.println(pathToUriOnLocalHost.apply("path/to/resource"));
   *     System.out.println(pathToUriOnLocalHost.apply("path/to/another/resource"));
   *
   *     Function<String, String> pathToUriOnRemoteHost = functionFactory.create(asList("https", "example.com", 8443));
   *     System.out.println(pathToUriOnRemoteHost);
   *     System.out.println(pathToUriOnRemoteHost.apply("path/to/resource"));
   *     System.out.println(pathToUriOnRemoteHost.apply("path/to/another/resource"));
   *
   *     Function<String, String> pathToUriOnLocalHost_2 = functionFactory.create(asList("http", "localhost", 80));
   *     System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnLocalHost_2.hashCode()); //<1>
   *     System.out.println(pathToUriOnLocalHost.equals(pathToUriOnLocalHost_2)); //<2>
   *
   *     System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnRemoteHost.hashCode()); //<1>
   *     System.out.println(pathToUriOnLocalHost.equals(pathToUriOnRemoteHost)); //<2>
   *   }
   *
   *   private PrintableFunction.Factory<String, String, List<Object>> pathToUriFunctionFactory() {
   *     return Printables.functionFactory(
   *         (List<Object> args) -> "buildUri" + args,
   *         (List<Object> args) -> (String path) -> String.format("%s://%s:%s/%s", args.get(0), args.get(1), args.get(2), path));
   *   }
   * }
   * ----
   * <1>Note that the functions created by the same factory object with the `equal` value given to the `create(E)` method return the same `hashCode()` but not vice versa.
   * <2>Also note that the function create by the same factory object with the `equal` value given to the `create(E) method result in `true` on `equals(Object)` method.
   *
   * And this results in a following output.
   * [source, console]
   * ----
   * buildUri[http, localhost, 80]
   * http://localhost:80/path/to/resource
   * http://localhost:80/path/to/another/resource
   * buildUri[https, example.com, 8443]
   * https://example.com:8443/path/to/resource
   * https://example.com:8443/path/to/another/resource
   * true<1>
   * true<2>
   * false<1>
   * false<2>
   * ----
   *
   * //@formatter:on
   *
   * @param nameComposer A function that creates a descriptive string from the argument passed to the returned factory
   * @param ff           A function that creates a function to be made printable from a value of {@code E}.
   * @param <T>          Type of the parameter of the function created by the returned factory.
   * @param <R>          Type of the returned value of the function create by the returned factory.
   * @param <E>          Type of the parameter with which the returned factory create a function
   * @return A factory to create a printable function.
   */
  public static <T, R, E> PrintableFunction.Factory<T, R, E> functionFactory(
      final Function<E, String> nameComposer,
      final Function<E, Function<T, R>> ff) {
    return PrintableFunction.factory(nameComposer, ff);
  }

  /**
   * @param nameComposer A function that creates a descriptive string from the argument passed to the returned factory
   * @param ff           A function that creates a predicate to be made printable.
   * @param <T>          Type of the parameter of the function created by the returned factory.
   * @param <E>          Type of the parameter with which the returned factory create a predicate
   * @return A factory to create a printable predicate.
   */
  public static <T, E> PrintablePredicate.Factory<T, E> predicateFactory(
      final Function<E, String> nameComposer,
      final Function<E, Predicate<T>> ff) {
    return PrintablePredicate.factory(nameComposer, ff);
  }
}