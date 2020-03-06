package com.github.dakusui.crest.utils.ut;

import com.github.dakusui.crest.utils.TestUtils;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.functions.Printable;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.dakusui.crest.utils.ut.ParameterizedPredicatesTest.TestItem.$;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParameterizedPredicatesTest {
  @BeforeClass
  public static void suppressStdOutErrIfRunUnderSurefire() {
  }

  @Parameters
  public static List<Object[]> parameters() {
    Object x = new Object() {
      @Override
      public String toString() {
        return "x";
      }
    };
    return asList(
        new Object[] { new Object[] {
            Predicates.alwaysTrue(),
            "alwaysTrue",
            $("hello", true),
            $(100, true),
        } },
        new Object[] { new Object[] {
            Predicates.isTrue(),
            "isTrue",
            $(true, true),
            $(false, false),
        } },
        new Object[] { new Object[] {
            Predicates.isFalse(),
            "isFalse",
            $(false, true),
            $(true, false),
        } },
        new Object[] { new Object[] {
            Predicates.equalTo("X"),
            "equalTo[\"X\"]",
            $("X", true),
            $("Y", false),
        } },
        new Object[] { new Object[] {
            Predicates.isSameAs(x),
            "==[x]",
            $(x, true),
            $("x", false),
        } },
        new Object[] { new Object[] {
            Predicates.isInstanceOf(List.class),
            "isInstanceOf[java.util.List]",
            $(new LinkedList<>(), true),
            $(new HashSet<>(), false),
        } },
        new Object[] { new Object[] {
            Predicates.startsWith("hello"),
            "startsWith[\"hello\"]",
            $("hello world", true),
            $("world hello", false),
        } },
        new Object[] { new Object[] {
            Predicates.endsWith("hello"),
            "endsWith[\"hello\"]",
            $("world hello", true),
            $("hello world", false),
        } },
        new Object[] { new Object[] {
            Predicates.equalsIgnoreCase("hello"),
            "equalsIgnoreCase[\"hello\"]",
            $("HELLO", true),
            $("hello", true),
            $("HELLO!", false),
        } },
        new Object[] { new Object[] {
            Predicates.isEmptyOrNullString(),
            "isEmptyOrNullString",
            $("", true),
            $(null, true),
            $("HELLO", false),
        } },
        new Object[] { new Object[] {
            Predicates.contains("Hello"),
            "contains[\"Hello\"]",
            $(Collections.singletonList("Hello"), true),
            $(Collections.singleton("Hello"), true),
            $(Collections.emptySet(), false),
        } },
        new Object[][] { {
            Predicates.isEmpty(),
            "isEmpty",
            $(Collections.singletonList("Hello"), false),
            $(Collections.emptyList(), true),
            $(Collections.emptySet(), true),
        } },
        new Object[][] { {
            Predicates.alwaysTrue().and(Predicates.equalTo("X")),
            "(alwaysTrue&&equalTo[\"X\"])",
            $("X", true),
            $("Y", false)
        } },
        new Object[][] { {
            Predicates.equalTo("Z").or(Predicates.equalTo("X")),
            "(equalTo[\"Z\"]||equalTo[\"X\"])",
            $("X", true),
            $("Y", false)
        } },
        new Object[][] { {
            Predicates.equalTo("Y").negate().or(Predicates.equalTo("X").and(Predicates.isInstanceOf(String.class))),
            "(!equalTo[\"Y\"]||(equalTo[\"X\"]&&isInstanceOf[java.lang.String]))",
            $("Z", true),
            $("X", true),
            $("Y", false)
        } },
        new Object[][] { {
            Predicates.alwaysTrue().negate(),
            "!alwaysTrue",
            $("X", false),
        } },
        new Object[][] { {
            Predicates.invoke("startsWith", "hello"),
            ".startsWith(\"hello\")",
            $("helloWorld", true),
            $("worldHello", false),
        } },
        new Object[][] { {
            Printable.predicate("alwaysTrue", s -> true)
                .negate()
                .and(Printable.predicate("alwaysFalse", s -> false)
                .or(Printable.predicate("hello", s -> false).and(Printable.predicate("bye", s -> true)))),
            "(!alwaysTrue&&(alwaysFalse||(hello&&bye)))",
            $("ANYTHING", false),
        } }

    );
  }

  final private Predicate<Object>      predicate;
  final private String         expectationForToString;
  final private List<TestItem> testItems;

  @Before
  public void before() {
    TestUtils.suppressStdOutErrIfRunUnderSurefire();
    System.out.printf("predicate:%s%n", this.predicate);
    System.out.printf("expectationForToString:%s%n", this.expectationForToString);
    System.out.printf("testItems:%s%n", this.testItems);
  }

  @Test
  public void exerciseToString() {
    assertEquals(expectationForToString, predicate.toString());
  }

  @Test
  public void exercisePredicate() {
    for (TestItem testItem : testItems) {
      assertEquals(String.format("testItem:%s", testItem), testItem.expectation, predicate.test(testItem.data));
    }
  }

  @After
  public void restoreStdOutErr() {
    TestUtils.restoreStdOutErr();
  }

  @SuppressWarnings("unchecked")
  public ParameterizedPredicatesTest(
      Object[] args
  ) {
    this.predicate = (Predicate<Object>) args[0];
    this.expectationForToString = (String) args[1];
    this.testItems = new LinkedList<TestItem>() {{
      for (int i = 2; i < args.length; i++) {
        add((TestItem) args[i]);
      }
    }};
  }

  static class TestItem {
    final Object  data;
    final boolean expectation;

    TestItem(Object data, boolean expectation) {
      this.data = data;
      this.expectation = expectation;
    }

    @Override
    public String toString() {
      return String.format("data:%s; matcher:%s", data, expectation);
    }

    static TestItem $(Object data, boolean expectation) {
      return new TestItem(data, expectation);
    }
  }
}
