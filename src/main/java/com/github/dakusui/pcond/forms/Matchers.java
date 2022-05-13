package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.core.printable.Matcher;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.forms.Printables.predicate;
import static java.util.Collections.emptyList;

public enum Matchers {
  ;


  private static <IN> Matcher.Builder.Builder0.Builder1<IN> when() {
    return new Matcher.Builder.Builder0.Builder1<>();
  }

  public static <IN> Matcher.Builder.Builder0.Builder1<IN> matcher() {
    return new Matcher.Builder.Builder0.Builder1<>();
  }

  static class Cursor {
    final int position;
    final int length;

    Cursor(int position, int length) {
      this.position = position;
      this.length = length;
    }
  }

  @SuppressWarnings("unchecked")
  static <T> Predicate<String> findTokens(Function<T, Function<String, Cursor>> locatorFactory, T... tokens) {
    class CursoredString implements Evaluator.Snapshottable {
      String originalString;
      int    position;

      CursoredString(String originalString) {
        this.originalString = originalString;
        this.position = 0;
      }

      CursoredString findNext(T token) {
        Function<String, Cursor> locator = locatorFactory.apply(token);
        Cursor cursor = locator.apply(originalString.substring(this.position));
        if (cursor.position >= 0)
          this.position += cursor.position + cursor.length;
        return this;
      }

      public String toString() {
        return "CursoredString[" + originalString + "]";
      }

      @Override
      public Object snapshot() {
        return originalString.substring(position);
      }
    }
    CursoredString cursoredStringForSnapshotting = new CursoredString(null);
    class CursoredStringPredicate
        extends PrintablePredicate<CursoredString>
        implements Predicate<CursoredString>, Evaluable.LeafPred<CursoredString>, Evaluator.Explainable {
      final T each;

      CursoredStringPredicate(T each) {
        super(new Object(), emptyList(), () -> "findTokenBy[" + locatorFactory + "[" + each + "]]", cursoredString -> {
          cursoredStringForSnapshotting.position = cursoredString.position;
          cursoredStringForSnapshotting.originalString = cursoredString.originalString;
          return cursoredString.position != cursoredString.findNext(each).position;
        });
        this.each = each;
      }

      @Override
      public String toString() {
        return "findTokenBy[" + locatorFactoryName() + "]";
      }

      private String locatorFactoryName() {
        return locatorFactory + "[" + each + "]";
      }

      @Override
      public Predicate<? super CursoredString> predicate() {
        return this;
      }

      @Override
      public Object explainExpectation() {
        return cursoredStringForSnapshotting.originalString.substring(0, cursoredStringForSnapshotting.position) +
            String.format("%n") +
            "<<NOTFOUND:" + this.locatorFactoryName() + ">>" +
            String.format("%n") +
            cursoredStringForSnapshotting.originalString.substring(cursoredStringForSnapshotting.position);
      }

      @Override
      public Object explainActualInput() {
        return cursoredStringForSnapshotting.originalString.substring(0, cursoredStringForSnapshotting.position) +
            String.format("%n") +
            "<<>>" +
            String.format("%n") +
            cursoredStringForSnapshotting.originalString.substring(cursoredStringForSnapshotting.position);
      }
    }
    return when_().stringValue()
        .chain(function("findTokens", CursoredString::new)).then()
        .allOf(
            Stream.concat(
                    Arrays.stream(tokens).map(CursoredStringPredicate::new),
                    Stream.of(predicate("(end)", v -> true)))
                .toArray(Predicate[]::new))
        .build();
  }

  public static Predicate<String> findSubstrings(String... tokens) {
    return Matchers.findTokens(Printables.function("substring", token -> string -> new Cursor(string.indexOf(token), token.length())), tokens);
  }

  public static Predicate<String> findRegexPatterns(Pattern... patterns) {
    return Matchers.findTokens(function("matchesRegex", token -> string -> {
      java.util.regex.Matcher m = token.matcher(string);
      if (m.find()) {
        return new Cursor(m.start(), m.end() - m.start());
      } else
        return new Cursor(-1, 0);

    }), patterns);
  }

  public static Predicate<String> findRegexes(String... regexes) {
    return findRegexPatterns(Arrays.stream(regexes).map(Pattern::compile).toArray(Pattern[]::new));
  }

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E> Predicate<List<E>> findElements(Predicate<E>... predicates) {
    class CursoredList<EE> implements Evaluator.Snapshottable {
      int position;
      final List<EE> originalList;

      CursoredList(List<EE> originalList) {
        this.originalList = originalList;
      }

      List<EE> currentList() {
        return this.originalList.subList(position, this.originalList.size());
      }

      @Override
      public Object snapshot() {
        return this.currentList();
      }
    }
    Function<Predicate<E>, Predicate<CursoredList<E>>> predicatePredicateFunction = (Predicate<E> p) -> (Predicate<CursoredList<E>>) cursoredList -> {
      AtomicInteger j = new AtomicInteger(0);
      if (cursoredList.currentList().stream()
          .peek((E each) -> j.getAndIncrement())
          .anyMatch(p)) {
        cursoredList.position += j.get();
        return true;
      }
      return false;
    };

    return Matchers.when().listValueOf((Class<E>)value())
        .chain(CursoredList::new)
        .then()
        .verifyWith(allOf(Stream.concat(
                Arrays.stream(predicates)
                    .map((Predicate<E> each) -> predicate("findElementBy[" + each + "]", predicatePredicateFunction.apply(each))),
                Stream.of(predicate("(end)", eCursoredList -> true)))
            .toArray(Predicate[]::new)))
        .build();
  }

  /**
   * Returns a value that can be cast to any class, even if it has a generic type parameters.
   * Note that accessing any field or method of the returned value results in
   * `NullPointerException`.
   *
   * @param <T> A parameter type of class that the returned value represents.
   * @return A `null` value
   */
  public static <T> T value() {
    return null;
  }

}
