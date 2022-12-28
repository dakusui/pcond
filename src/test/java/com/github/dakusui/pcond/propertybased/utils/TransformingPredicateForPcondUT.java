package com.github.dakusui.pcond.propertybased.utils;

import com.github.dakusui.shared.ReportParser;
import org.junit.ComparisonFailure;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.makePrintableFunction;
import static com.github.dakusui.pcond.propertybased.utils.ReportCheckUtils.makePrintablePredicate;
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

  public static TransformingPredicateForPcondUT<ComparisonFailure, List<Integer>> numberOfSummaryRecordsForActualAndExpectedAreEqual() {
    return new TransformingPredicateForPcondUT<>(
        makePrintableFunction("comparisonFailureToSummaryRecordNumbersForExpectationAndActual", e -> asList(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size())),
        makePrintablePredicate("bothAreEqual", e -> Objects.equals(e.get(0), e.get(1)))
    );
  }

  public static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return makePrintablePredicate(
        "# of records (actual) = " + numberOfExpectedSummaryRecordsForActual,
        e -> Objects.equals(numberOfExpectedSummaryRecordsForActual, new ReportParser(e.getActual()).summary().records().size()));
  }
}
