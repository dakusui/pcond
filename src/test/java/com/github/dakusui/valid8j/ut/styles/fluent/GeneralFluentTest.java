package com.github.dakusui.valid8j.ut.styles.fluent;

import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.propertybased.utils.TestCase;
import com.github.dakusui.pcond.propertybased.utils.TestCaseUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.fluent.Fluents.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

@RunWith(Parameterized.class)
public class GeneralFluentTest {
  public GeneralFluentTest(TestCase<?, ?> testCase) {
    this.testCase = testCase;
  }

  static class TestSuite<T> {
    final List<Function<T, Predicate<T>>> statementFactories;
    final List<T>                         passingValues;
    final List<T>                         failingValues;

    TestSuite(Function<T, Predicate<T>> statementFactory, List<T> passingValues, List<T> failingValues) {
      this(singletonList(statementFactory), passingValues, failingValues);
    }

    TestSuite(List<Function<T, Predicate<T>>> statementFactories, List<T> passingValues, List<T> failingValues) {
      this.statementFactories = statementFactories;
      this.passingValues = passingValues;
      this.failingValues = failingValues;
    }

    List<TestCase<T, ?>> createTestCases() {
      return Stream.concat(
              this.passingValues.stream()
                  .flatMap(v -> statementFactories.stream()
                      .map(statementFactory -> TestCaseUtils.testCaseExpectingPass(v, statementFactory.apply(v)).build())),
              this.failingValues.stream()
                  .flatMap(v -> statementFactories.stream()
                      .map(statementFactory -> TestCaseUtils.testCaseExpectingComparisonFailure(v, statementFactory.apply(v)).build())))
          .collect(toList());
    }
  }

  private final TestCase<?, ?> testCase;

  @Test
  public void exerciseTestCase() throws Throwable {
    TestCaseUtils.exerciseTestCase(this.testCase);
  }

  @Parameterized.Parameters
  public static List<TestCase<?, ?>> toTestCases() {
    return Stream.of(
            booleanTestSuite_1(),
            listTestSuite_1(),
            listTestSuite_2(),
            stringTestSuite_1(),
            stringTestSuite_2(),
            stringTestSuite_3())
        .flatMap(each -> each.createTestCases().stream())
        .collect(toList());
  }

  private static TestSuite<Boolean> booleanTestSuite_1() {
    return new TestSuite<>(
        v -> booleanValue(v).then().isTrue().done(),
        asList(true, Boolean.TRUE),
        asList(false, Boolean.FALSE, null));
  }

  private static TestSuite<List<String>> listTestSuite_1() {
    return new TestSuite<>(
        asList(
            (List<String> v) -> listValue(v).subList(1).then().isNotEmpty().done(),
            (List<String> v) -> listValue(v).subList(1, v.size()).then().isNotEmpty().done()),
        asList(
            asList("A", "B", "C"),
            asList("A", "B")),
        asList(
            singletonList("X"),
            Collections.emptyList()));
  }
  private static TestSuite<List<String>> listTestSuite_2() {
    return new TestSuite<>(
        singletonList(
            (List<String> v) -> listValue(v).then().isEmpty().done()),
        singletonList(
            emptyList()),
        singletonList(
            singletonList("X")));
  }

  private static TestSuite<String> stringTestSuite_1() {
    return new TestSuite<>(
        asList(
            (String v) -> stringValue(v).substring(1).parseShort().then().isInstanceOf(Short.class).isEqualTo((short) 23).done(),
            (String v) -> stringValue(v).parseShort().then().isInstanceOf(Short.class).isEqualTo((short) 123).done(),
            (String v) -> stringValue(v).parseLong().then().isInstanceOf(Long.class).isEqualTo((long) 123).done(),
            (String v) -> stringValue(v).parseFloat().then().isInstanceOf(Float.class).isEqualTo((float) 123).done(),
            (String v) -> stringValue(v).parseDouble().then().isInstanceOf(Double.class).isEqualTo((double) 123).done()),
        singletonList("123"),
        asList("456", "A", null));
  }

  private static TestSuite<String> stringTestSuite_2() {
    return new TestSuite<>(
        singletonList(
            (String v) -> stringValue(v).parseBoolean().then().isInstanceOf(Boolean.class).isTrue().done()),
        singletonList("true"),
        asList("false", "XYZ", null));
  }

  private static TestSuite<String> stringTestSuite_3() {
    return new TestSuite<>(
        asList(
            (String v) -> stringValue(v).split(":").then().isNotEmpty().contains("A").contains("B").contains("C").done(),
            (String v) -> stringValue(v).split(":").then().findElementsInOrder("A", "B", "C").done(),
            (String v) -> stringValue(v).split(":").then().findElementsInOrderBy(asList(Predicates.isEqualTo("A"), Predicates.isEqualTo("B"), Predicates.isEqualTo("C"))).done()),
        singletonList("A:B:C"),
        asList("A:B", null));
  }
}
