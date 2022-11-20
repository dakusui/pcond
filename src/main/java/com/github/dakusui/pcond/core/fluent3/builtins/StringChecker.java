package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;
import static com.github.dakusui.pcond.internals.InternalUtils.trivialIdentityFunction;

public interface StringChecker<T> extends
    AbstractObjectChecker<
        StringChecker<T>,
        T,
        String> {
  default StringChecker<T> contains(String token) {
    return this.checkWithPredicate(Predicates.containsString(token));
  }

  default StringChecker<T> startsWith(String prefix) {
    return this.checkWithPredicate(Predicates.startsWith(prefix));
  }

  default StringChecker<T> isEmpty() {
    return this.checkWithPredicate(Predicates.isEmptyString());
  }

  default StringChecker<T> isNotEmpty() {
    return this.checkWithPredicate(Predicates.isEmptyString().negate());
  }

  default StringChecker<T> isEqualTo(String string) {
    return this.checkWithPredicate(explainableStringIsEqualTo(string));
  }

  default StringChecker<T> isNullOrEmpty() {
    return this.checkWithPredicate(Predicates.isNullOrEmptyString());
  }

  default StringChecker<T> matchesRegex(String regex) {
    return this.checkWithPredicate(Predicates.matchesRegex(regex));
  }

  default StringChecker<T> equalsIgnoreCase(String s) {
    return this.checkWithPredicate(Predicates.equalsIgnoreCase(s));
  }

  default StringChecker<T> findRegexes(String... regexes) {
    return this.checkWithPredicate(Predicates.findRegexes(regexes));
  }

  default StringChecker<T> findRegexPatterns(Pattern... patterns) {
    return this.checkWithPredicate(Predicates.findRegexPatterns(patterns));
  }

  default StringChecker<T> findSubstrings(String... tokens) {
    return this.checkWithPredicate(Predicates.findSubstrings(tokens));
  }

  @SuppressWarnings("unchecked")
  default StringChecker<T> check(Function<StringChecker<String>, Predicate<String>> phrase) {
    return this.addCheckPhrase(v -> phrase.apply((StringChecker<String>) v));
  }

  class Impl<T>
      extends
      Base<StringChecker<T>, T, String>
      implements
      StringChecker<T> {
    protected Impl(Supplier<T> rootValue, Function<T, String> transformFunction) {
      super(rootValue, transformFunction);
    }

    @Override
    public StringChecker<String> rebase() {
      return new StringChecker.Impl<>(this::value, trivialIdentityFunction());
    }
  }
}
