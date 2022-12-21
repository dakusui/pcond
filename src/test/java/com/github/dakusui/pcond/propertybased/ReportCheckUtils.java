package com.github.dakusui.pcond.propertybased;

import com.github.dakusui.shared.ReportParser;
import org.junit.ComparisonFailure;

import java.util.Objects;
import java.util.function.Predicate;

public enum ReportCheckUtils {
  ;

  static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualIsEqualTo(@SuppressWarnings("SameParameterValue") int numberOfExpectedSummaryRecordsForActual) {
    return makePrintable("# of records (actual) = " + numberOfExpectedSummaryRecordsForActual, e -> Objects.equals(numberOfExpectedSummaryRecordsForActual, new ReportParser(e.getActual()).summary().records().size()));
  }

  static Predicate<ComparisonFailure> numberOfSummaryRecordsForActualAndExpectedAreEqual() {
    return makePrintable("# of records (actual) = # of records (expected)", e -> Objects.equals(new ReportParser(e.getActual()).summary().records().size(), new ReportParser(e.getExpected()).summary().records().size()));
  }

  public static <T> Predicate<T> makePrintable(String s, Predicate<T> predicate) {
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

  static <T> Predicate<T> equalsPredicate(T w) {
    return makePrintable("equals(" + w + ")", v -> Objects.equals(v, w));
  }
}
