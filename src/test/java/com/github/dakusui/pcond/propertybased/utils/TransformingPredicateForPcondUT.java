package com.github.dakusui.pcond.propertybased.utils;

import com.github.dakusui.shared.ReportParser;
import org.junit.ComparisonFailure;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.*;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public class TransformingPredicateForPcondUT<T, R> {
  final Function<T, R> transform;
  final Predicate<R>   check;

  TransformingPredicateForPcondUT(Function<T, R> transform, Predicate<R> check) {
    this.transform = requireNonNull(transform);
    this.check = requireNonNull(check);
  }

  static <T> TransformingPredicateForPcondUT<T, T> createFromSimplePredicate(Predicate<T> predicate) {
    return new TransformingPredicateForPcondUT<>(makePrintableFunction("identity", Function.identity()), predicate);
  }

  public static TransformingPredicateForPcondUT<ComparisonFailure, List<Integer>> numberOfExpectAndActualSummariesAreEqual() {
    return new TransformingPredicateForPcondUT<>(
        makePrintableFunction("comparisonFailureToExpectAndActualSummaryNumbers", e -> asList(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size())),
        makePrintablePredicate("bothAreEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static TransformingPredicateForPcondUT<ComparisonFailure, Integer> numberOfActualSummariesIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return new TransformingPredicateForPcondUT<>(
        makePrintableFunction(
            "numberOfActualSummaries",
            e -> new ReportParser(e.getActual()).summary().records().size()),
        equalsPredicate(numberOfExpectedSummaryRecordsForActual));
  }

  public static TransformingPredicateForPcondUT<ComparisonFailure, List<Long>> numberOfExpectAndActualSummariesWithDetailsAreEqual() {
    return new TransformingPredicateForPcondUT<>(
        makePrintableFunction("numberOfExpectAndActualSummariesWithDetailsAreEqual",
            e -> asList(
                new ReportParser(e.getActual()).summary().records().stream().filter(s -> s.detailIndex().isPresent()).count(),
                new ReportParser(e.getExpected()).summary().records().stream().filter(s -> s. detailIndex().isPresent()).count())),
        makePrintablePredicate("bothAreEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static TransformingPredicateForPcondUT<ComparisonFailure, Long> numberOfExpectSummariesWithDetailsIsEqualTo(long numberOfSummariesWithDetails) {
    return new TransformingPredicateForPcondUT<>(
        makePrintableFunction("numberOfExpectSummariesWithDetail", comparisonFailure -> new ReportParser(comparisonFailure.getExpected()).summary().records().stream().filter(e -> e.detailIndex().isPresent()).count()),
        equalsPredicate(numberOfSummariesWithDetails));
  }
}
