package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.shared.ReportParser;
import org.junit.ComparisonFailure;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ReportCheckUtils {
  ;

  static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return makePrintablePredicate(
        "# of records (actual) = " + numberOfExpectedSummaryRecordsForActual,
        e -> Objects.equals(numberOfExpectedSummaryRecordsForActual, new ReportParser(e.getActual()).summary().records().size()));
  }

  static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualAndExpectedAreEqual() {
    return makePrintablePredicate("# of records (actual) = # of records (expected)", e -> Objects.equals(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size()));
  }

  public static <T> Predicate<T> makePrintablePredicate(String s, Predicate<T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T t) {
        return predicate.test(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  public static <T, R> Function<T, R> makePrintableFunction(String s, Function<T, R> function) {
    return new Function<T, R>() {
      @Override
      public R apply(T t) {
        return function.apply(t);
      }

      @Override
      public String toString() {
        return s;
      }
    };
  }

  static <T> Predicate<T> equalsPredicate(T w) {
    return makePrintablePredicate("equals(" + w + ")", v -> Objects.equals(v, w));
  }
}
