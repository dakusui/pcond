package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.core.*;
import com.github.dakusui.crest.utils.TestBase;
import com.github.dakusui.pcond.functions.Predicates;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.opentest4j.AssertionFailedError;
import org.opentest4j.TestAbortedException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.pcond.functions.Functions.*;
import static com.github.dakusui.pcond.functions.Predicates.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class CrestTest {
  static class Description {
    private final String content;

    Description(String s) {
      this.content = s;
    }

    @Override
    public String toString() {
      return this.content;
    }
  }

  private static final Predicate<Integer> FAILING_CHECK = predicate("failingCheck", v -> {
    throw new RuntimeException("FAILED");
  });

  private static final Function<List<String>, Integer> FAILING_TRANSFORM = function("failingTransform", v -> {
    throw new RuntimeException("FAILED");
  });

  /**
   * <pre>
   *   Conj
   *   (1): P -> P      : pass
   *   (2): P -> F      : fail
   *   (3): E -> P      : fail
   *   (4): F -> F      : fail
   * </pre>
   * <pre>
   *   TestData: ["Hello", "world", "!"]
   * </pre>
   */
  public static class ConjTest extends TestBase {
    /**
     * <pre>
     *   Conj
     *   (1): P -> P      : pass
     * </pre>
     */
    @Test
    public void whenPassingAndThenPassing$thenPasses() {
      List<String> aList = composeTestData();

      Optional<Description> description = CrestTest.describeFailure(
          aList,
          allOf(
              Crest.asObject(
                  elementAt(0)
              ).check(
                  equalTo("Hello")).all()
              ,
              Crest.asObject(
                  size()
              ).check(
                  equalTo(3)
              ).all()
          ));

      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }

    @Test
    public void makeSureCalledOnlyOnce() {
      List<String> aList = composeTestData();

      Optional<Description> description = CrestTest.describeFailure(
          aList,
          allOf(
              Crest.asObject(
                  new Function<List<?>, String>() {
                    boolean firstTime = true;

                    @Override
                    public String apply(List<?> objects) {
                      try {
                        if (firstTime)
                          return (String) elementAt(0).apply(objects);
                        else
                          throw new Error();
                      } finally {
                        firstTime = false;
                      }
                    }
                  }
              ).check(
                  new Predicate<String>() {
                    boolean firstTime = true;

                    @Override
                    public boolean test(String s) {
                      try {
                        if (firstTime)
                          return equalTo("Hello").test(s);
                        else
                          throw new Error();
                      } finally {
                        firstTime = false;
                      }
                    }
                  }
              ).all()
              ,
              Crest.asObject(
                  size()
              ).check(
                  equalTo(3)
              ).all()
          ));

      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }


    /**
     * <pre>
     *   Conj
     *   (2): P -> F      : fail
     * </pre>
     */
    @Test
    public void whenPassingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size())
                  .check(equalTo(3))
                  .all(),
              Crest.asObject(elementAt(0))
                  .check(equalTo("hello"))
                  .all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n"
              + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
              + "and:[\n"
              + "  x->size equalTo[3]\n"
              + "  x->at[0] equalTo[\"hello\"]\n"
              + "]\n"
              + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
              + "and:[\n"
              + "  x->size equalTo[3]\n"
              + "  x->at[0] equalTo[\"hello\"]: NOT MET\n"
              + "    x->at[0]=\"Hello\"\n"
              + "]: NOT MET",
          description.orElseThrow(AssertionError::new).toString()
      );
    }

    /**
     * <pre>
     *   Conj
     *   (3): E -> P      : error
     * </pre>
     */
    @Test
    public void whenErrorOnCheckAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size()).check(FAILING_CHECK).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith(
              "\n"
                  + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
                  + "and:[\n"
                  + "  x->size failingCheck\n"
                  + "  x->at[0] equalTo[\"Hello\"]\n"
                  + "]\n"
                  + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
                  + "and:[\n"
                  + "  x->size failingCheck failed with java.lang.RuntimeException(FAILED)\n"
                  + "    x->size=<3>:Integer\n"
                  + "  x->at[0] equalTo[\"Hello\"]\n"
                  + "]: NOT MET\n"
                  + "FAILED"
          ));
    }

    /**
     * <pre>
     *   Conj
     *   (3): E -> P      : error
     * </pre>
     */
    @Test
    public void whenErrorOnTransformAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(FAILING_TRANSFORM).check(Predicates.alwaysTrue()).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith(
              "\n"
                  + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
                  + "and:[\n"
                  + "  x->failingTransform alwaysTrue\n"
                  + "  x->at[0] equalTo[\"Hello\"]\n"
                  + "]\n"
                  + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
                  + "and:[\n"
                  + "  x->failingTransform alwaysTrue failed with java.lang.RuntimeException(FAILED)\n"
                  + "    x->failingTransform=java.lang.RuntimeException(FAILED)\n"
                  + "  x->at[0] equalTo[\"Hello\"]\n"
                  + "]: NOT MET\n"
                  + "FAILED"
          ));
    }

    /**
     * <pre>
     *   Conj
     *   (4): F -> F      : fail
     * </pre>
     */
    @Test
    public void whenFailingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size()).check(equalTo(2)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n"
              + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
              + "and:[\n"
              + "  x->size equalTo[2]\n"
              + "  x->at[0] equalTo[\"hello\"]\n"
              + "]\n"
              + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
              + "and:[\n"
              + "  x->size equalTo[2]: NOT MET\n"
              + "    x->size=<3>:Integer\n"
              + "  x->at[0] equalTo[\"hello\"]: NOT MET\n"
              + "    x->at[0]=\"Hello\"\n"
              + "]: NOT MET",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  /**
   * <pre>
   *   Disj
   *   (1): P -> P      : pass
   *   (2): P -> F      : pass
   *   (3): E -> P      : fail
   *   (4): F -> F      : fail
   * </pre>
   */
  public static class DisjTest extends TestBase {

    /**
     * <pre>
     *   Disj
     *   (1): P -> P      : pass
     * </pre>
     */
    @Test
    public void whenPassingAndThen$thenPasses() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(3)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }


    /**
     * <pre>
     *   Disj
     *   (2): P -> F      : fail
     * </pre>
     */
    @Test
    public void whenDisjPassingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(3)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).all()
          ));

      System.out.println(description.orElse(null));
      System.out.println(description.orElse(null));
      assertFalse(description.isPresent());
    }

    /**
     * <pre>
     *   Disj
     *   (3): E -> P      : error
     * </pre>
     * In case an error is thrown, the assertion should fail even if all the other matchers are passing.
     */
    @Test
    public void whenErrorAndThenPassing$thenErrorThrownAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(FAILING_CHECK).all(),
              Crest.asObject(elementAt(0)).check(equalTo("Hello")).all()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.startsWith("\n"
              + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
              + "or:[\n"
              + "  x->size failingCheck\n"
              + "  x->at[0] equalTo[\"Hello\"]\n"
              + "]\n"
              + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
              + "or:[\n"
              + "  x->size failingCheck failed with java.lang.RuntimeException(FAILED)\n"
              + "    x->size=<3>:Integer\n"
              + "  x->at[0] equalTo[\"Hello\"]\n"
              + "]: NOT MET\n"
              + "FAILED"
          )
      );
    }

    /**
     * <pre>
     *   Disj
     *   (4): F -> F      : fail
     * </pre>
     */
    @Test
    public void whenFailingAndThenFailing$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(2)).matcher(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).matcher()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n"
              + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
              + "or:[\n"
              + "  x->size equalTo[2]\n"
              + "  x->at[0] equalTo[\"hello\"]\n"
              + "]\n"
              + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
              + "or:[\n"
              + "  x->size equalTo[2]: NOT MET\n"
              + "    x->size=<3>:Integer\n"
              + "  x->at[0] equalTo[\"hello\"]: NOT MET\n"
              + "    x->at[0]=\"Hello\"\n"
              + "]: NOT MET",
          description.orElseThrow(AssertionError::new).toString()
      );
    }
  }

  public static class NestedTest extends TestBase {
    /**
     * <pre>
     *   Disj
     *     ->Conj
     * </pre>
     */
    @Test
    public void whenConjUnderDisj$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          anyOf(
              Crest.asObject(size()).check(equalTo(2)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).check(equalTo("HELLO")).all()
          ));

      System.out.println(description.orElse(null));
      assertEquals(
          "\n"
              + "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n"
              + "or:[\n"
              + "  x->size equalTo[2]\n"
              + "  and:[\n"
              + "    x->at[0] equalTo[\"hello\"]\n"
              + "    x->at[0] equalTo[\"HELLO\"]\n"
              + "  ]\n"
              + "]\n"
              + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
              + "or:[\n"
              + "  x->size equalTo[2]: NOT MET\n"
              + "    x->size=<3>:Integer\n"
              + "  and:[\n"
              + "    x->at[0] equalTo[\"hello\"]: NOT MET\n"
              + "      x->at[0]=\"Hello\"\n"
              + "    x->at[0] equalTo[\"HELLO\"]: NOT MET\n"
              + "      x->at[0]=\"Hello\"\n"
              + "  ]: NOT MET\n"
              + "]: NOT MET",
          description.orElseThrow(AssertionError::new).toString()
      );
    }

    /**
     * <pre>
     *   Conj
     *     ->Disj
     * </pre>
     */
    @Test
    public void whenDisjUnderConj$thenFailsAndMessageAppropriate() {
      List<String> aList = composeTestData();

      Optional<Description> description = describeFailure(
          aList,
          allOf(
              Crest.asObject(size()).check(equalTo(2)).all(),
              Crest.asObject(elementAt(0)).check(equalTo("hello")).check(equalTo("HELLO")).any()
          ));

      System.out.println(description.orElse(null));
      assertThat(
          description.orElseThrow(AssertionError::new).toString(),
          CoreMatchers.containsString(
              "Expected: x=<(\"Hello\",\"world\",\"!\")> satisfies\n" +
              "and:[\n"
                  + "  x->size equalTo[2]\n"
                  + "  or:[\n"
                  + "    x->at[0] equalTo[\"hello\"]\n"
                  + "    x->at[0] equalTo[\"HELLO\"]\n"
                  + "  ]\n"
                  + "]\n"
                  + "     but: x=<(\"Hello\",\"world\",\"!\")> did not satisfy\n"
                  + "and:[\n"
                  + "  x->size equalTo[2]: NOT MET\n"
                  + "    x->size=<3>:Integer\n"
                  + "  or:[\n"
                  + "    x->at[0] equalTo[\"hello\"]: NOT MET\n"
                  + "      x->at[0]=\"Hello\"\n"
                  + "    x->at[0] equalTo[\"HELLO\"]: NOT MET\n"
                  + "      x->at[0]=\"Hello\"\n"
                  + "  ]: NOT MET\n"
                  + "]: NOT MET"
          ));
    }
  }

  private static <T> Optional<Description> describeFailure(T actual, Matcher<T> matcher) {
    //    Assertion<T> assertion = create(null, matcher);
    //    if (!matcher.matches(actual, assertion)) {
    //      String description = "\nExpected: " +
    //          String.join("\n", matcher.describeExpectation(assertion)) +
    //          "\n     but: " +
    //          String.join("\n", matcher.describeMismatch(actual, assertion));
    //
    //      return Optional.of(new Description(description));
    //    }

    Session<T> session = Session.create();
    Report report = Session.perform(actual, matcher, session);
    if (!report.isSuccessful()) {
      String description = "\nExpected: " +
          String.join("\n", report.expectation()) +
          "\n     but: " +
          String.join("\n", report.mismatch());
      return Optional.of(new Description(description));
    }
    return Optional.empty();
  }

  private static List<String> composeTestData() {
    return new LinkedList<String>() {{
      add("Hello");
      add("world");
      add("!");
    }};
  }

  public static class NegativesTest extends TestBase {
    @Test
    public void given_NotMatcher_$whenFailingTestPerformed$thenMessageCorrect() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.not(
              asString().containsString("HELLO").$()
          )
      );
      System.out.println(description.orElseThrow(RuntimeException::new));
      assertThat(
          description.get().content,
          Matchers.containsString(
              "Expected: x=\"HELLO\" satisfies\n"
              + "not:[\n"
              + "  x containsString[\"HELLO\"]\n"
              + "]\n"
              + "     but: x=\"HELLO\" did not satisfy\n"
              + "not:[\n"
              + "  x containsString[\"HELLO\"]\n"
              + "]")
      );
    }


    @Test
    public void given_NotMatcher_$whenPassingTestPerformed$thenPassed() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.not(
              asString().containsString("WORLD").$()
          )
      );
      description.ifPresent(desc -> fail("Should have been passed but failed with a following message:" + desc.content));
    }


    @Test
    public void given_NoneOfMatcher_$whenFailingTestPerformed$thenMessageCorrect() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.noneOf(
              asString().eq("WORLD").$(),
              asString().containsString("HELLO").$()
          )
      );
      System.out.println(description.orElseThrow(RuntimeException::new));
      assertThat(
          description.get().content,
          Matchers.containsString("\n"
              + "Expected: x=\"HELLO\" satisfies\n"
              + "noneOf:[\n"
              + "  x ~[\"WORLD\"]\n"
              + "  x containsString[\"HELLO\"]\n"
              + "]\n"
              + "     but: x=\"HELLO\" did not satisfy\n"
              + "noneOf:[\n"
              + "  x ~[\"WORLD\"]: NOT MET\n"
              + "  x containsString[\"HELLO\"]\n"
              + "]")
      );
    }

    @Test
    public void given_NoneOfMatcher_$whenPassingTestPerformed$thenPasses() {
      Optional<Description> description = describeFailure(
          "HELLO",
          Crest.noneOf(
              asString().eq("WORLD").$(),
              asString().containsString("hellox").$()
          )
      );

      description.ifPresent(desc -> fail("Should have been passed but failed with a following message:" + desc.content));
    }
  }

  public static class AssertAssumeRequireTest {
    @Test
    public void givenAssertThat$whenPasses$thenOk() {
      Crest.assertThat(
          "hello",
          asString().equalTo("hello").$()
      );
    }

    @Test(expected = AssertionFailedError.class)
    public void givenAssertThat$whenFailOnComparison$thenComparisonFailureThrown() {
      Crest.assertThat(
          "Check 'hello'",
          "hello",
          asString().equalTo("HELLO").$()
      );
    }

    @Test(expected = ExecutionFailure.class)
    public void givenAssertThat$whenFailOnExercise$thenExecutionFalureThrown() {
      Crest.assertThat(
          "Check 'hello'",
          "hello",
          asString("xyz").equalTo("HELLO").$()
      );
    }

    @Test
    public void givenRequireThat$whenPasses$thenOk() {
      Crest.requireThat(
          "hello",
          asString().equalTo("hello").$()
      );
    }

    @Test(expected = ExecutionFailure.class)
    public void givenRequireThat$whenFailOnComparison$thenExecutionFalureThrown() {
      Crest.requireThat(
          "Check 'hello'",
          "hello",
          asString().equalTo("HELLO").$()
      );
    }

    @Test(expected = ExecutionFailure.class)
    public void givenRequireThat$whenFailOnExercise$thenExecutionFalureThrown() {
      Crest.requireThat(
          "Check 'hello'",
          "hello",
          asString("xyz").equalTo("HELLO").$()
      );
    }

    @Test
    public void givenAssumeThat$whenPasses$thenOk() {
      Crest.assumeThat(
          "hello",
          asString().equalTo("hello").$()
      );
    }

    @Test(expected = IOException.class)
    public void givenAssumeThat$whenFailOnComparison$thenExecutionFalureThrown() throws IOException {
      try {
        Crest.assumeThat(
            "Check 'hello'",
            "hello",
            asString().equalTo("HELLO").$()
        );
      } catch (TestAbortedException e) {
        // Wrap with IOException, which cannot happen in this test procedure to
        // make sure intended exception (AssumptionViolatedException) is really
        // thrown.
        throw new IOException(e);
      }
    }

    @Test(expected = ExecutionFailure.class)
    public void givenAssumeThat$whenFailOnExercise$thenExecutionFalureThrown() {
      Crest.assumeThat(
          "Check 'hello'",
          "hello",
          asString("xyz").equalTo("HELLO").$()
      );
    }
  }

  public static class CallMechanismTest extends TestBase {
    @Test
    public void givenStaticCall$whenToString$thenWorksRight() {
      Function<Object, String> func = call(String.class, "format", "<me=%s, %s>", args(THIS, "hello")).$();

      System.out.println(func.toString());
      System.out.println(func.apply("world"));
      Crest.assertThat(
          func,
          allOf(
              Crest.asString("toString").equalTo("->String.format(\"<me=%s, %s>\",Object[] [(THIS), hello])").$(),
              Crest.asString(call("apply", "world").$()).equalTo("<me=world, hello>").$()
          )
      );
    }

    @Test
    public void givenStaticCallOnOverloadedMethod$whenToString$thenWorksRight() {
      Object func = call(Stream.class, "of", args(Integer.class, 1, 2, 3)).andThen("collect", Collectors.toList()).$();
      System.out.println(func.toString());
      try {
        Crest.assertThat(
            func,
            allOf(
                Crest.asString("toString")
                    .startsWith("->Stream.of(Integer[] [1, 2, 3]).collect(CollectorImpl@").$(),
                Crest.asInteger(call("apply", "NOTHING").andThen("size").$()).equalTo(3).$()
            ));
      } catch (ExecutionFailure e) {
        System.err.println(e.getMessage());
        throw e;
      }
    }

    @Test
    public void givenInstanceCall$whenToString$thenWorksRight() {
      Function<Object, String> func = callOn("Hello world", "indexOf", THIS).andThen("toString").$();

      System.out.println(func.toString());
      System.out.println(func.apply("world"));
      Crest.assertThat(
          func,
          allOf(
              Crest.asString("toString").equalTo("->Hello world.indexOf((THIS)).toString()").$(),
              Crest.asString(call("apply", "world").$()).equalTo("0").$()
          )
      );
    }

    @Test
    public void printExample() {
      Function<?, ?> func = Call.create("append", "hello").andThen("append", 1).andThen("append", "everyone").andThen("toString").$();
      System.out.println(func.toString());
      Function<StringBuilder, String> func2 = (StringBuilder b) -> b.append("hello").append(1).append("world").append("everyone").toString();
      System.out.println(func2.toString());
    }
  }
}
