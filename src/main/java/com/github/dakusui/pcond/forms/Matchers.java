package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.core.printable.Matcher;

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

public enum Matchers {
  ;


  public static <B extends Matcher.Builder.Builder0<B, OIN, OUT>, OIN, OUT> B when() {
    return matcher();
  }

  public static <B extends Matcher.Builder.Builder0<B, OIN, OUT>, OIN, OUT> B matcher() {
    return (B) new Matcher.Builder.Builder0<B, OIN, OUT>(null);
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
      final String originalString;
      int position;

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

    return matcher().valueIsString().chain(function("findTokens", CursoredString::new)).then()
        .allOf(
            Stream.concat(
                    Arrays.stream(tokens)
                        .map(each -> new Predicate<CursoredString>() {
                          @Override
                          public boolean test(CursoredString cursoredString) {
                            return cursoredString.position != cursoredString.findNext(each).position;
                          }

                          @Override
                          public String toString() {
                            return "findTokenBy[" + each + "]";
                          }
                        }),
                    Stream.of(predicate("(end)", v -> true)))
                .toArray(Predicate[]::new)
        );
  }

  public static Predicate<String> findSubstrings(String... tokens) {
    return Matchers.findTokens(token -> string -> new Cursor(string.indexOf(token), token.length()), tokens);
  }

  public static Predicate<String> findRegexPatterns(Pattern... patterns) {
    return Matchers.findTokens(token -> string -> {
      java.util.regex.Matcher m = token.matcher(string);
      if (m.find()) {
        return new Cursor(m.start(), m.end() - m.start());
      } else
        return new Cursor(-1, 0);

    }, patterns);
  }

  public static Predicate<String> findRegexes(String... regexes) {
    return findRegexPatterns(Arrays.stream(regexes).map(Pattern::compile).toArray(Pattern[]::new));
  }

  @SafeVarargs
  public static <E> Predicate<List<E>> findElements(Predicate<E>... predicates) {
    class CursoredList<EE> implements Evaluator.Snapshottable {
      int position;
      final List<E> originalList;

      CursoredList(List<E> originalList) {
        this.originalList = originalList;
      }

      List<E> currentList() {
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
    /*
    return when()
        .chain(new Function<Object, List<E>>() {
          @Override
          public List<E> apply(Object o) {
            return null;
          }
        })
        .then()
        .verifyWith(allOf(Stream.concat(
                Arrays.stream(predicates)
                    .map((Predicate<E> each) -> predicate("findElementBy[" + each + "]", predicatePredicateFunction.apply(each))),
                Stream.of(predicate("(end)", eCursoredList -> true)))
            .toArray(Predicate[]::new)));

     */
    return null;
  }

  /**
   * Returns a value that can be cast to any class, even if it has a generic type parameters.
   * Note that accessing any field or method of the returned value results in
   * `NullPointerException`.
   *
   * @param <T> A parameter type of class that the returned value represents.
   * @return A `null` value
   */
  public static <T> Class<T> anyClassValue() {
    return null;
  }
}
