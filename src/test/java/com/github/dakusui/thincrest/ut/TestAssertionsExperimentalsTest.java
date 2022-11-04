package com.github.dakusui.thincrest.ut;

import com.github.dakusui.pcond.core.context.Context;
import com.github.dakusui.pcond.core.printable.PrintableFunctionFactory;
import com.github.dakusui.pcond.forms.Experimentals;
import com.github.dakusui.pcond.utils.ut.TestBase;
import com.github.dakusui.thincrest.TestAssertions;
import org.junit.ComparisonFailure;
import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.forms.Experimentals.nest;
import static com.github.dakusui.pcond.forms.Experimentals.toContextPredicate;
import static com.github.dakusui.pcond.forms.Functions.curry;
import static com.github.dakusui.pcond.forms.Functions.stream;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.forms.Printables.predicate;
import static com.github.dakusui.shared.ExperimentalsUtils.stringEndsWith;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

public class TestAssertionsExperimentalsTest extends TestBase {

  @Test(expected = ComparisonFailure.class)
  public void helloError() {
    TestAssertions.assertThat(
        singletonList("hello"),
        transform(stream().andThen(nest(singletonList("o"))))
            .check(noneMatch(toContextPredicate(stringEndsWith()))));
  }

  @Test
  public void toContextPredicateTest() {
    assertFalse(toContextPredicate(isNotNull()).test(Context.from(null)));
    assertTrue(toContextPredicate(isNotNull()).test(Context.from(new Object())));
  }

  public static class IntentionalError extends Error {
  }

  @Test
  public void parameterizedPredicateTest() {
    Predicate<String> p = Experimentals.<String>parameterizedPredicate("containsStringIgnoreCase")
        .factory(args -> v -> v.toUpperCase().contains(args.get(0).toString().toUpperCase()))
        .create("hello");
    assertTrue(p.test("hello!"));
    assertTrue(p.test("Hello!"));
    assertFalse(p.test("World!"));
    assertEquals("containsStringIgnoreCase[hello]", p.toString());
  }

  @Test
  public void parameterizedPredicate_() {

    Predicate<String> p = Experimentals.<String>parameterizedPredicate("containsStringIgnoreCase")
        .factory((args) -> predicate(() -> "toUpperCase().contains(" + args.get(0) + ")", (String v) -> v.toUpperCase().contains(args.get(0).toString().toUpperCase())))
        .create("hello");
    System.out.println("p:<" + p + ">");
    assertTrue(p.test("hello!"));
    assertTrue(p.test("Hello!"));
    assertFalse(p.test("World!"));
    assertEquals("containsStringIgnoreCase[hello]", p.toString());

  }

  @Test
  public void parameterizedFunctionTest() {
    Function<Object[], Object> f = Experimentals.<Object[], Object>parameterizedFunction("arrayElementAt")
        .factory(args -> v -> v[(int) args.get(0)])
        .create(1);
    assertEquals("HELLO1", f.apply(new Object[] { 0, "HELLO1" }));
    assertEquals("HELLO2", f.apply(new Object[] { "hello", "HELLO2" }));
    assertEquals("arrayElementAt[1]", f.toString());
  }

  @Test
  public void usageExample() {
    Function<List<Object>, Function<String, String>> functionFactory = pathToUriFunctionFactory();
    Function<String, String> pathToUriOnLocalHost = functionFactory.apply(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost);
    System.out.println(pathToUriOnLocalHost.apply("path/to/resource"));
    System.out.println(pathToUriOnLocalHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnRemoteHost = functionFactory.apply(asList("https", "example.com", 8443));
    System.out.println(pathToUriOnRemoteHost);
    System.out.println(pathToUriOnRemoteHost.apply("path/to/resource"));
    System.out.println(pathToUriOnRemoteHost.apply("path/to/another/resource"));

    Function<String, String> pathToUriOnLocalHost_2 = functionFactory.apply(asList("http", "localhost", 80));
    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnLocalHost_2.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnLocalHost_2));

    System.out.println(pathToUriOnLocalHost.hashCode() == pathToUriOnRemoteHost.hashCode());
    System.out.println(pathToUriOnLocalHost.equals(pathToUriOnRemoteHost));
  }

  private static Function<List<Object>, Function<String, String>>
  pathToUriFunctionFactory() {
    return v -> PrintableFunctionFactory.create(
        (List<Object> args) -> () -> "buildUri" + args, (List<Object> args) -> (String path) -> String.format("%s://%s:%s/%s", args.get(0), args.get(1), args.get(2), path), v, TestAssertionsExperimentalsTest.class
    );
  }


}
