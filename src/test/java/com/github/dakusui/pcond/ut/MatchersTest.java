package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.utils.ut.TestBase;

public class MatchersTest extends TestBase {
  /*
  @Test
  public void whenPassingValidation_thenPasses$1() {
    validate(
        new Parent(),
        matcherFor(Parent.class)
            .transformBy(Parent::parentMethod1)
            .verifyWith(isEqualTo("returnValueFromParentMethod")),
        TestException::new);
  }

  @Test(expected = TestException.class)
  public void whenValidationWithIntentionallyFailingPredicate_thenExceptionThrown$2() {
    try {
      validate(
          new Parent(),
          allOf(
              matcherFor(Parent.class)
                  .name("lambda:Parent::parentMethod1--by name() method")
                  .transformBy(Parent::parentMethod1)
                  .verifyWith(isEqualTo("returnValueFromParentMethod")),
              matcherFor(Parent.class).name("parentMethod2")
                  .transformBy(Parent::parentMethod2)
                  .into(Child.class)
                  .verifyWith(
                      matcherFor(Child.class)
                          .transformBy(function("lambda:Child::childMethod--by Printables.function()", Child::childMethod))
                          // 'not(...)' is added to make the matcher fail.
                          .verifyWith(not(isEqualTo("returnedStringFromChildMethod")))
                  )),
          TestException::new);
    } catch (TestException e) {
      e.printStackTrace();
      MatcherAssert.assertThat(
          e.getMessage(),
          CoreMatchers.allOf(
              CoreMatchers.containsString("lambda:Parent::parentMethod1--by name()"),
              CoreMatchers.containsString("lambda:Child::childMethod--by Printables.function()")));
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void example() {
    try {
      assertThat(
          new Parent(),
          allOf(
              matcherFor(Parent.class)
                  .transformBy(function("lambda:Parent::parentMethod1--by name() method", Parent::parentMethod1))
                  .verifyWith(isEqualTo("returnValueFromParentMethod")),
              matcherFor(Parent.class).name("parentMethod2")
                  .transformBy(function("Parent::parentMethod2", Parent::parentMethod2))
                  .into(Child.class)
                  .verifyWith(
                      matcherFor(Child.class)
                          .transformBy(function("lambda:Child::childMethod--by Printables.function() method", Child::childMethod))
                          // 'not(...)' is added to make the matcher fail.
                          .verifyWith(not(isEqualTo("returnedStringFromChildMethod")))
                  )));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void givenRawLambdas$whenFailingAssertionPerformed$thenComparisonFailureThrown() {
    assertThat(
        new Parent(),
        matcherFor(Parent.class)
            .transformBy(Parent::parentMethod2).into(Child.class)
            .andThen(Child::childMethod)
            .verifyWith(equalTo("hello")));

  }

  @Test
  public void givenList_whenPassingMatcherForListOf_thenPasses() {
    validate(
        asList("Hello", "World"),
        and(matcherForListOf(String.class).transformBy(elementAt(0)).verifyWith(isEqualTo("Hello")),
            matcherForListOf(String.class).transformBy(l -> l.get(1).toLowerCase()).verifyWith(isEqualTo("world"))
        ));
  }

  @Test
  public void givenCollection_whenPassingMatcherForListOf_thenPasses() {
    validate(
        unmodifiableCollection(asList("Hello", "World")),
        matcherForCollectionOf(String.class).transformBy(size()).verifyWith(isEqualTo(2))
    );
  }

  @Test
  public void givenArray_whenPassingMatcher_thenPasses() {
    validate(new String[] { "Hello", "World" },
        allOf(
            matcherForArrayOf(String.class).transformBy(v -> v[0]).into(String.class).verifyWith(equalTo("Hello")),
            matcherForArrayOf(String.class).transformBy(v -> v[1]).into(String.class).verifyWith(equalTo("World"))
        ));
  }

  @Test
  public void givenString_whenPassingStringMatcher_thenPasses() {
    validate("Hello, world",
        matcherForString().transformBy(v -> v).verifyWith(equalTo("Hello, world")));
  }

  @Test//(expected = ComparisonFailure.class)
  public void findSubstringsTest() {
    String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur";
    try {
      assertThat(text, Matchers.findSubstrings("Gallia", "quarum", "Belgium", "nostra"));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Test(expected = ComparisonFailure.class)
  public void findRegexesTest() {
    String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur";
    try {
      assertThat(text, Matchers.findRegexes("Gall.a", "quar.m", "Belgium", "nostr(um|a)"));
    } catch (ComparisonFailure e) {
      e.printStackTrace();
      throw e;
    }
  }


  @Test(expected = ComparisonFailure.class)
  public void findElementTest() {
    List<String> list = asList("Hello", "world", "", "everyone", "quick", "brown", "fox", "runs", "forever");
    assertThat(list, findElements(
        isEqualTo("world"),
        isEqualTo("cat"), isEqualTo("organization"), isNotNull(), isEqualTo("fox"), isEqualTo("world")));
  }


  @Test
  public void matcherForStringWorksFine() {
    assertThat("Hello, world", matcherForString().substring(2).toUpperCase().then().verifyWith(containsString("Hello")));
  }

  @Test
  public void matcherForStringWorksFine2() {
    assertThat("Hello, world", when().valueIsString().substring(2).toUpperCase().then().verifyWith(containsString("Hello")));
  }

  static class Parent {
    public String parentMethod1() {
      return "returnValueFromParentMethod";
    }

    public Child parentMethod2() {
      return new Child();
    }
  }

  static class Child {
    public String childMethod() {
      return "returnedStringFromChildMethod";
    }
  }

  static class TestException extends RuntimeException {
    TestException(String message) {
      super(message);
    }
  }
  */

}
