package com.github.dakusui.pcond.propertybased.utils;

import com.github.dakusui.shared.ReportParser;
import org.junit.ComparisonFailure;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.*;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class TestCheck<T, R> {
  final Function<T, R> transform;
  final Predicate<R>   check;

  TestCheck(Function<T, R> transform, Predicate<R> check) {
    this.transform = requireNonNull(transform);
    this.check = requireNonNull(check);
  }

  static <T> TestCheck<T, T> createFromSimplePredicate(Predicate<T> predicate) {
    return new TestCheck<>(makePrintableFunction("identity", Function.identity()), predicate);
  }

  public static TestCheck<ComparisonFailure, List<Integer>> numberOfExpectAndActualSummariesAreEqual() {
    return new TestCheck<>(
        makePrintableFunction("comparisonFailureToExpectAndActualSummaryNumbers", e -> asList(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size())),
        makePrintablePredicate("bothAreEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfActualSummariesIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        makePrintableFunction(
            "numberOfActualSummaries",
            e -> new ReportParser(e.getActual()).summary().records().size()),
        equalsPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfActualSummariesIsGreaterThanOrEqualTo(int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        makePrintableFunction(
            "numberOfActualSummaries",
            e -> new ReportParser(e.getActual()).summary().records().size()),
        greaterThanOrEqualToPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfExpectSummariesWithDetailsIsGreaterThanOrEqualTo(int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        makePrintableFunction(
            "numberOfActualSummaries",
            e -> (int)new ReportParser(e.getActual()).summary().records().stream().filter(r -> r.detailIndex().isPresent()).count()),
        greaterThanOrEqualToPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, Integer> numberOfExpectSummariesWithDetailsIsEqualTo(int numberOfExpectedSummaryRecordsForActual) {
    return new TestCheck<>(
        makePrintableFunction(
            "numberOfActualSummaries",
            e -> (int)new ReportParser(e.getActual()).summary().records().stream().filter(r -> r.detailIndex().isPresent()).count()),
        greaterThanOrEqualToPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TestCheck<ComparisonFailure, List<Long>> numberOfExpectAndActualSummariesWithDetailsAreEqual() {
    return new TestCheck<>(
        makePrintableFunction("numberOfExpectAndActualSummariesWithDetailsAreEqual",
            e -> asList(
                new ReportParser(e.getActual()).summary().records().stream().filter(s -> s.detailIndex().isPresent()).count(),
                new ReportParser(e.getExpected()).summary().records().stream().filter(s -> s.detailIndex().isPresent()).count())),
        makePrintablePredicate("bothAreEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static TestCheck<ComparisonFailure, Long> numberOfExpectSummariesWithDetailsIsEqualTo(long numberOfSummariesWithDetails) {
    return new TestCheck<>(
        makePrintableFunction("numberOfExpectSummariesWithDetail", comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).summary().records().stream().filter(e -> e.detailIndex().isPresent()).count()),
        equalsPredicate(numberOfSummariesWithDetails));
  }

  public static TestCheck<ComparisonFailure, List<String>> formNamesContainAllOf(List<String> tokens, Function<ComparisonFailure, String> function) {
    return new TestCheck<>(
        functionToFindTokensInColumnByCondition(
            tokens,
            function,
            makePrintableFunction("formName", ReportParser.Summary.Record::op),
            makePrintableBiPredicate("notFoundInColumn", TestCheck::notFoundInColumn)),
        makePrintablePredicate("isEmpty", List::isEmpty));
  }


  public static TestCheck<ComparisonFailure, List<String>> inputValuesContainAllOf(List<String> tokens, Function<ComparisonFailure, String> function) {
    return new TestCheck<>(
        functionToFindTokensInColumnByCondition(
            tokens,
            function,
            makePrintableFunction("input", (ReportParser.Summary.Record r) -> r.in().orElse("")),
            makePrintableBiPredicate("notFoundInColumn", TestCheck::notFoundInColumn)),
        makePrintablePredicate("isEmpty", List::isEmpty));
  }

  private static Function<ComparisonFailure, List<String>> functionToFindTokensInColumnByCondition(
      List<String> tokens,
      Function<ComparisonFailure, String> reportSelector,
      Function<ReportParser.Summary.Record, String> columnSelector,
      BiPredicate<String, Collection<String>> condition) {
    return makePrintableFunction("among[" + tokens + "].notFoundIn[" + reportSelector + "." + columnSelector +"]",
        comparisonFailure -> {
          Collection<String> column = new ReportParser(reportSelector.apply(comparisonFailure))
              .summary()
              .records()
              .stream()
              .map(columnSelector)
              .collect(Collectors.toList());
          return tokens.stream()
              .filter(t -> condition.test(t, column))
              .collect(Collectors.toList());
        });
  }

  private static boolean notFoundInColumn(String token, Collection<String> column) {
    return column.stream().noneMatch(x -> x.contains(token));
  }

  public static Function<ComparisonFailure, String> comparisonFailureToExpected() {
    return makePrintableFunction("expected", ComparisonFailure::getExpected);
  }
  public static Function<ComparisonFailure, String> comparisonFailureToActual() {
    return makePrintableFunction("actual", ComparisonFailure::getActual);
  }
}
