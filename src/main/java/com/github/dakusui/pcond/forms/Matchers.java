package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.core.printable.Matcher;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.forms.Predicates.allOf;
import static com.github.dakusui.pcond.forms.Predicates.alwaysTrue;
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

  @SuppressWarnings("unchecked")
  public static Predicate<String> findTokens(String... tokens) {
    class CursoredString implements Evaluator.Snapshottable {
      final String originalString;
      int cursor;

      CursoredString(String originalString) {
        this.originalString = originalString;
        this.cursor = 0;
      }

      CursoredString findToken(String token) {
        int pos = originalString.indexOf(token, this.cursor);
        if (pos >= 0)
          this.cursor = pos + token.length();
        return this;
      }

      String currentContent() {
        return this.originalString.substring(this.cursor);
      }

      public String toString() {
        return "CursoredString[" + originalString + "]";
      }

      @Override
      public Object snapshot() {
        return originalString.substring(cursor);
      }
    }

    //noinspection SuspiciousToArrayCall
    return matcherForString()
        .transformBy(function("findTokens", CursoredString::new)).thenVerifyWith(
            allOf(
                Stream.concat(
                        Arrays.stream(tokens)
                            .map(each -> new Predicate<CursoredString>() {
                              @Override
                              public boolean test(CursoredString cursoredString) {
                                return cursoredString.cursor != cursoredString.findToken(each).cursor;
                              }

                              @Override
                              public String toString() {
                                return "find[" + each + "]";
                              }
                            }),
                        Stream.of(predicate("(leftover)", v -> true)))
                    .toArray(i -> (Predicate<CursoredString>[]) new Predicate[tokens.length + 1])
            ));
  }
}
