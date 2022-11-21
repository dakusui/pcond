package com.github.dakusui.pcond.forms;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.Evaluator;
import com.github.dakusui.pcond.core.printable.PrintablePredicate;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory.Leaf;
import com.github.dakusui.pcond.core.printable.PrintablePredicateFactory.ParameterizedLeafFactory;
import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.core.refl.Parameter;
import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.core.refl.ReflUtils.invokeMethod;
import static com.github.dakusui.pcond.forms.Printables.function;
import static com.github.dakusui.pcond.forms.Printables.predicate;
import static com.github.dakusui.pcond.internals.InternalUtils.formatObject;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * An entry point for acquiring predicate objects.
 * Predicates retrieved by methods in this class are all "printable".
 */
public class Predicates {
  private Predicates() {
  }

  public static <T> Predicate<T> alwaysTrue() {
    return Leaf.ALWAYS_TRUE.instance();
  }

  public static Predicate<Boolean> isTrue() {
    return Leaf.IS_TRUE.instance();
  }

  public static Predicate<Boolean> isFalse() {
    return Leaf.IS_FALSE.instance();
  }

  public static <T> Predicate<T> isNull() {
    return Leaf.IS_NULL.instance();
  }

  public static <T> Predicate<T> isNotNull() {
    return Leaf.IS_NOT_NULL.instance();
  }

  public static <T> Predicate<T> isEqualTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.IS_EQUAL_TO, singletonList(value));
  }

  public static <T> Predicate<T> isSameReferenceAs(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.OBJECT_IS_SAME_AS, singletonList(value));
  }

  @SuppressWarnings({ "unchecked", "RedundantClassCall" })
  public static <T> Function<Class<?>, Predicate<T>> isInstanceOf() {
    return Function.class.cast(Def.IS_INSTANCE_OF$2);
  }

  public static <T> Predicate<? super T> isInstanceOf(Class<?> value) {
    return applyOnceExpectingPredicate(requireNonNull(value), isInstanceOf());
  }

  private static <T, R> Predicate<R> applyOnceExpectingPredicate(T value, Function<T, Predicate<R>> p) {
    return predicate(() -> format("%s[%s]", p, formatObject(value)), p.apply(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> gt(T value) {
    return greaterThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> greaterThan(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.GREATER_THAN, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> ge(T value) {
    return greaterThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> greaterThanOrEqualTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.GREATER_THAN_OR_EQUAL_TO, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> lt(T value) {
    return lessThan(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lessThan(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.LESS_THAN, singletonList(value));
  }

  public static <T extends Comparable<? super T>> Predicate<T> le(T value) {
    return lessThanOrEqualTo(value);
  }

  public static <T extends Comparable<? super T>> Predicate<T> lessThanOrEqualTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.LESS_THAN_OR_EQUAL_TO, singletonList(value));
  }

  public static <T extends Comparable<T>> Predicate<T> eq(T value) {
    return equalTo(value);
  }

  public static <T extends Comparable<T>> Predicate<T> equalTo(T value) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.EQUAL_TO, singletonList(value));
  }

  public static Predicate<String> matchesRegex(String regex) {
    requireNonNull(regex);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.MATCHES_REGEX, singletonList(regex));
  }

  public static Predicate<String> containsString(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.CONTAINS_STRING, singletonList(string));
  }

  public static Predicate<String> startsWith(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.STARTS_WITH, singletonList(string));
  }

  public static Predicate<String> endsWith(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.ENDS_WITH, singletonList(string));
  }

  public static Predicate<String> equalsIgnoreCase(String string) {
    requireNonNull(string);
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.EQUALS_IGNORE_CASE, singletonList(string));
  }

  public static Predicate<String> isEmptyString() {
    return Leaf.IS_EMPTY_STRING.instance();
  }

  public static Predicate<String> isNullOrEmptyString() {
    return Leaf.IS_NULL_OR_EMPTY_STRING.instance();
  }

  public static <E> Predicate<Collection<E>> contains(Object entry) {
    return ParameterizedLeafFactory.create(ParameterizedLeafFactory.CONTAINS, singletonList(entry));
  }

  public static Predicate<Object[]> isEmptyArray() {
    return Leaf.IS_EMPTY_ARRAY.instance();
  }

  public static Predicate<? super Collection<?>> isEmpty() {
    return Leaf.IS_EMPTY_COLLECTION.instance();
  }

  public static <E> Predicate<Stream<E>> allMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return PrintablePredicateFactory.allMatch(predicate);
  }

  public static <E> Predicate<Stream<E>> noneMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return PrintablePredicateFactory.noneMatch(predicate);
  }

  public static <E> Predicate<Stream<E>> anyMatch(Predicate<E> predicate) {
    requireNonNull(predicate);
    return PrintablePredicateFactory.anyMatch(predicate);
  }

  @SafeVarargs
  public static <T> Predicate<T> and(Predicate<? super T>... predicates) {
    return PrintablePredicateFactory.and(asList(predicates));
  }

  @SafeVarargs
  public static <T> Predicate<T> or(Predicate<? super T>... predicates) {
    return PrintablePredicateFactory.or(asList(predicates));
  }

  @SafeVarargs
  public static <T> Predicate<T> allOf(Predicate<? super T>... predicates) {
    return PrintablePredicateFactory.allOf(asList(predicates));
  }

  @SafeVarargs
  public static <T> Predicate<T> anyOf(Predicate<? super T>... predicates) {
    return PrintablePredicateFactory.anyOf(asList(predicates));
  }

  public static <T> Predicate<T> not(Predicate<T> cond) {
    return PrintablePredicateFactory.not(cond);
  }

  public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> transform(String funcName, Function<O, P> func) {
    return transform(function(funcName, func));
  }

  public static <O, P> PrintablePredicateFactory.TransformingPredicate.Factory<P, O> transform(Function<O, P> function) {
    return PrintablePredicateFactory.transform(function);
  }

  /**
   * // @formatter:off
   * Returns a {@link Predicate} created from a method specified by a {@code methodQuery}.
   * If the {@code methodQuery} matches none or more than one methods, a {@code RuntimeException} will be thrown.
   *
   * The suffix {@code p} stands for "predicate" following the custom in LISP culture
   * and it is necessary to avoid collision with {@link Functions#call(MethodQuery)} method.
   *
   * // @formatter:on
   *
   * @param methodQuery A query object that specifies a method to be invoked by the returned predicate.
   * @param <T>         the type of the input to the returned predicate
   * @return Created predicate.
   * @see Functions#classMethod(Class, String, Object[])
   * @see Functions#instanceMethod(Object, String, Object[])
   * @see Functions#parameter()
   */
  @SuppressWarnings("ConstantConditions")
  public static <T> Predicate<T> callp(MethodQuery methodQuery) {
    return predicate(
        methodQuery.describe(),
        t -> InternalChecks.ensureValue(
            invokeMethod(methodQuery.bindActualArguments((o) -> o instanceof Parameter, o -> t)),
            v -> v instanceof Boolean,
            v -> format("Method matched with '%s' must return a boolean value but it gave: '%s'.", methodQuery.describe(), v)));
  }

  /**
   * // @formatter:off
   * Returns a predicate that calls a method which matches the given {@code methodName}
   * and {@code args} on the object given as input to it.
   *
   * Note that method look up is done when the predicate is applied.
   * This means this method does not throw any exception by itself and in case
   * you give wrong {@code methodName} or {@code arguments}, an exception will be
   * thrown when the returned function is applied.
   * // @formatter:on
   *
   * @param methodName The method name
   * @param arguments  Arguments passed to the method.
   * @param <T>        The type of input to the returned predicate
   * @return A predicate that invokes the method matching the {@code methodName} and {@code args}
   * @see Functions#parameter()
   */
  public static <T> Predicate<T> callp(String methodName, Object... arguments) {
    return callp(Functions.instanceMethod(Functions.parameter(), methodName, arguments));
  }

  /**
   * Note that a predicate returned by this method is stateful and not to be re-used.
   *
   * @param locatorFactory A function to return a cursor which points the location where a given token appears in an original string.
   * @param tokens         Tokens to be found in a given string passed to the returned predicate.
   * @param <T>            The type of token to be searched for.
   * @return A predicate that checks if `tokens` are all contained in a given string
   * in the order, where they appear in the argument.
   */
  @SuppressWarnings("unchecked")
  static <T> Predicate<String> findTokens(Function<T, Function<String, Cursor>> locatorFactory, T... tokens) {
    AtomicBoolean result = new AtomicBoolean(true);
    AtomicInteger lastTestedPosition = new AtomicInteger(0);
    StringBuilder bExpectation = new StringBuilder();
    StringBuilder bActual = new StringBuilder();
    class CursoredString implements Evaluator.Snapshottable {
      public int previousFailingPosition;
      String originalString;
      int    position;

      CursoredString(String originalString) {
        this.originalString = originalString;
        this.position = 0;
      }

      CursoredString findNext(T token) {
        Function<String, Cursor> locator = locatorFactory.apply(token);
        Cursor cursor = locator.apply(originalString.substring(this.position));
        if (cursor.position >= 0) {
          updateOngoingExplanation(bExpectation, token, cursor, (lf, t) -> "found for:" + locatorFactory + "[" + t + "]");
          updateOngoingExplanation(bActual, token, cursor, (lf, t) -> "found for:" + locatorFactory + "[" + t + "]");

          this.position += cursor.position + cursor.length;
        } else {
          this.previousFailingPosition = this.position;
        }
        lastTestedPosition.set(this.position);
        return this;
      }

      private void updateOngoingExplanation(StringBuilder b, T token, Cursor cursor, BiFunction<Object, T, String> locatorFactoryFormatter) {
        b.append(this.originalString, this.position, this.position + cursor.position);
        b.append("<");
        b.append(formatObject(this.originalString.substring(this.position + cursor.position, this.position + cursor.position + cursor.length)));
        b.append(":");
        b.append(locatorFactoryFormatter.apply(locatorFactory, token));
        b.append(">");
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
    class CursoredStringPredicate extends PrintablePredicate<CursoredString> implements
        Predicate<CursoredString>,
        Evaluable.LeafPred<CursoredString>,
        Evaluator.Explainable {
      final T each;

      CursoredStringPredicate(T each) {
        super(new Object(), emptyList(), () -> "findTokenBy[" + locatorFactory + "[" + each + "]]", cursoredString -> {
          cursoredStringForSnapshotting.previousFailingPosition = cursoredString.previousFailingPosition;
          cursoredStringForSnapshotting.position = cursoredString.position;
          cursoredStringForSnapshotting.originalString = cursoredString.originalString;
          return cursoredString.position != cursoredString.findNext(each).position;
        });
        this.each = each;
      }

      @Override
      public boolean test(CursoredString v) {
        boolean ret = super.test(v);
        result.set(ret && result.get());
        return ret;
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
        return formatExplanation(bExpectation, "SHOULD BE FOUND AFTER THIS POSITION");
      }

      @Override
      public Object explainActual(Object actualValue) {
        return formatExplanation(bActual, "BUT NOT FOUND");
      }

      private String formatExplanation(StringBuilder b, String keyword) {
        String ret = b.toString() + format("%n") + "<" + this.locatorFactoryName() + ":" + keyword + ">";
        b.delete(0, b.length());
        return ret;
      }
    }
    return Predicates.transform(function("findTokens" + formatObject(tokens), CursoredString::new))
        .check(Predicates.allOf(
            Stream.concat(
                    Arrays.stream(tokens).map(CursoredStringPredicate::new),
                    Stream.of(endMarkPredicateForString(lastTestedPosition, bExpectation, bActual, result, () -> cursoredStringForSnapshotting.originalString)))
                .toArray(Predicate[]::new)));

  }

  private static Predicate<Object> endMarkPredicateForString(AtomicInteger lastTestedPosition, StringBuilder ongoingExpectationExplanation, StringBuilder ongoingActualExplanation, AtomicBoolean result, Supplier<String> originalStringSupplier) {
    return makeExplainable((PrintablePredicate<? super Object>) predicate("(end)", v -> result.get()), new Evaluator.Explainable() {

      @Override
      public Object explainExpectation() {
        return ongoingExpectationExplanation.toString() + originalStringSupplier.get().substring(lastTestedPosition.get());
      }

      @Override
      public Object explainActual(Object actualValue) {
        return ongoingActualExplanation.toString() + originalStringSupplier.get().substring(lastTestedPosition.get());
      }
    });
  }

  private static <T> Predicate<T> makeExplainable(PrintablePredicate<? super T> p, Evaluator.Explainable explainable) {
    class ExplainablePredicate extends PrintablePredicate<T> implements
        Predicate<T>,
        Evaluable.LeafPred<T>,
        Evaluator.Explainable {

      protected ExplainablePredicate() {
        super(new Object(), emptyList(), p::toString, p);
      }

      @Override
      public Predicate<? super T> predicate() {
        return predicate;
      }

      @Override
      public Object explainExpectation() {
        return explainable.explainExpectation();
      }

      @Override
      public Object explainActual(Object actualValue) {
        return explainable.explainActual(actualValue);
      }
    }

    return new ExplainablePredicate();
  }

  public static Predicate<String> findSubstrings(String... tokens) {
    return findTokens(Printables.function("substring", token -> string -> new Cursor(string.indexOf(token), token.length())), tokens);
  }

  public static Predicate<String> findRegexPatterns(Pattern... patterns) {
    return findTokens(function("matchesRegex", token -> string -> {
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

  private static class Explanation {
    final         Object value;
    private final String formatString;

    private Explanation(Object value, String formatString) {
      this.value = value;
      this.formatString = formatString;
    }

    @Override
    public String toString() {
      return format(formatString, formatObject(this.value));
    }
  }

  static class CursoredList<EE> implements Evaluator.Snapshottable {
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

  @SuppressWarnings("unchecked")
  @SafeVarargs
  public static <E> Predicate<List<E>> findElements(Predicate<? super E>... predicates) {
    AtomicBoolean result = new AtomicBoolean(true);
    List<Object> expectationExplanationList = new LinkedList<>();
    List<Object> actualExplanationList = new LinkedList<>();
    List<Object> rest = new LinkedList<>();
    AtomicInteger previousPosition = new AtomicInteger(0);
    Function<Predicate<? super E>, Predicate<CursoredList<E>>> predicatePredicateFunction = (Predicate<? super E> p) -> (Predicate<CursoredList<E>>) cursoredList -> {
      AtomicInteger j = new AtomicInteger(0);
      boolean isFound = cursoredList.currentList().stream()
          .peek((E each) -> j.getAndIncrement())
          .anyMatch(p);
      if (isFound) {
        updateExplanationsForFoundElement(
            expectationExplanationList, actualExplanationList,
            cursoredList.currentList().get(j.get() - 1),
            p, (List<Object>) cursoredList.currentList().subList(0, j.get() - 1));
        rest.clear();
        rest.add(cursoredList.currentList().subList(j.get(), cursoredList.currentList().size()));
        cursoredList.position += j.get();
        previousPosition.set(cursoredList.position);
        return true;
      }
      updateExplanationsForMissedPredicateIfCursorMoved(
          expectationExplanationList, actualExplanationList,
          cursoredList.position > previousPosition.get(),
          p, cursoredList.currentList().subList(0, j.get()));
      result.set(false);
      previousPosition.set(cursoredList.position);
      return false;
    };
    return Predicates.transform(function("toCursoredList", (List<E> v)-> new CursoredList<>(v)))
        .check(allOf(Stream.concat(
                Arrays.stream(predicates)
                    .map((Predicate<? super E> each) -> predicate("findElementBy[" + each + "]", predicatePredicateFunction.apply(each))),
                Stream.of(endMarkPredicateForList(result, expectationExplanationList, actualExplanationList, rest)))
            .toArray(Predicate[]::new)));
  }

  private static <E> void updateExplanationsForFoundElement(List<Object> expectationExplanationList, List<Object> actualExplanationList, E foundElement, Predicate<? super E> matchedPredicate, List<Object> skippedElements) {
    if (!skippedElements.isEmpty()) {
      //      expectationExplanationList.add(skippedElements);
      actualExplanationList.add(skippedElements);
    }
    actualExplanationList.add(new Explanation(foundElement, "<%s:found for:" + matchedPredicate + ">"));
    expectationExplanationList.add(new Explanation(matchedPredicate, "<matching element for:%s>"));
  }

  private static <E> void updateExplanationsForMissedPredicateIfCursorMoved(List<Object> expectationExplanationList, List<Object> actualExplanationList, boolean isCursorMoved, Predicate<? super E> missedPredicate, List<E> scannedElements) {
    if (isCursorMoved) {
      //expectationExplanationList.add(scannedElements);
      actualExplanationList.add(scannedElements);
    }
    Explanation missedInExpectation = new Explanation(missedPredicate, "<matching element for:%s>");
    expectationExplanationList.add(missedInExpectation);

    Explanation missedInActual = new Explanation(missedPredicate, "<NOT FOUND:matching element for:%s>");
    actualExplanationList.add(missedInActual);
  }

  private static Predicate<Object> endMarkPredicateForList(AtomicBoolean result, List<Object> expectationExplanationList, List<Object> actualExplanationList, List<?> rest) {
    return makeExplainable((PrintablePredicate<? super Object>) predicate("(end)", v -> result.get()), new Evaluator.Explainable() {

      @Override
      public Object explainExpectation() {
        return renderExplanationString(expectationExplanationList);
      }

      @Override
      public Object explainActual(Object actualValue) {
        return renderExplanationString(createFullExplanationList(actualExplanationList, rest));
      }

      private List<Object> createFullExplanationList(List<Object> explanationList, List<?> rest) {
        return Stream.concat(explanationList.stream(), rest.stream()).collect(toList());
      }

      private String renderExplanationString(List<Object> fullExplanationList) {
        return fullExplanationList
            .stream()
            .map(e -> {
              if (e instanceof List) {
                return String.format("<%s:skipped>",
                    ((List<?>) e).stream()
                        .map(InternalUtils::formatObject)
                        .collect(joining(",")));
              }
              return e;
            })
            .map(Object::toString)
            .collect(joining(String.format("%n")));
      }
    });
  }

  enum Def {
    ;

    public static final Function<Class<?>, Predicate<?>> IS_INSTANCE_OF$2 = function(() -> "isInstanceOf", (Class<?> c) -> c::isInstance);
  }

  static class Cursor {
    /**
     * The "relative" position, where the token was found, from the beginning of the string passed to a locator.
     * By convention, it is designed to pass a substring of the original string, which starts from the position,
     * where a token (element) searching attempt was made.
     */
    final int position;
    /**
     * A length of a token to be searched.
     */
    final int length;

    Cursor(int position, int length) {
      this.position = position;
      this.length = length;
    }
  }
}
