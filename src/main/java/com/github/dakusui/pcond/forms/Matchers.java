package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.core.printable.Matcher;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.forms.Printables.predicate;

public enum Matchers {
  ;


  public static Matcher.Builder.Builder0<String> matcherForString() {
    return matcher().forType(String.class);
  }


  public static <IN> Matcher.Builder.Builder0<IN> matcher() {
    return new Matcher.Builder.Builder0<>();
  }


  public static <IN> Matcher.Builder.Builder0<IN> matcherFor(Class<IN> inType) {
    return Matchers.matcher().forType(inType);
  }

  public static <E> Matcher.Builder.Builder0<E[]> matcherForArrayOf(Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <E> Matcher.Builder.Builder0<List<E>> matcherForListOf(Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <E> Matcher.Builder.Builder0<Collection<E>> matcherForCollectionOf(
      @SuppressWarnings("unused") Class<E> elementType) {
    return Matchers.matcher();
  }

  public static <K, V> Matcher.Builder.Builder0<Map<K, V>> matcherForMapOf(Class<K> keyType, Class<V> valueType) {
    return Matchers.matcher();
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
  public static <T> Predicate<String> findTokens(Function<T, Function<String, Cursor>> locatorFactory, T... tokens) {
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

    return matcherForString()
        .transformBy(function("findTokens", CursoredString::new)).thenVerifyWith(
            allOf(
                Stream.concat(
                        Arrays.stream(tokens)
                            .map(each -> new Predicate<CursoredString>() {
                              @Override
                              public boolean test(CursoredString cursoredString) {
                                return cursoredString.position != cursoredString.findNext(each).position;
                              }

                              @Override
                              public String toString() {
                                return "find[" + each + "]";
                              }
                            }),
                        Stream.of(predicate("(leftover)", v -> true)))
                    .toArray(Predicate[]::new)
            ));
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
}
