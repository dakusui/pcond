package com.github.dakusui.pcond.core.fluent.checkers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Checker;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;

public interface StringChecker<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, String>,
    Checker<StringChecker<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default StringChecker<OIN> contains(String token) {
    return this.addPredicate(Predicates.containsString(token));
  }

  default StringChecker<OIN> startsWith(String prefix) {
    return this.addPredicate(Predicates.startsWith(prefix));
  }

  default StringChecker<OIN> isEmpty() {
    return this.addPredicate(Predicates.isEmptyString());
  }

  default StringChecker<OIN> isNotEmpty() {
    return this.addPredicate(Predicates.isEmptyString().negate());
  }

  default StringChecker<OIN> isEqualTo(String string) {
    return this.addPredicate(explainableStringIsEqualTo(string));
  }

  default StringChecker<OIN> isNullOrEmpty() {
    return this.addPredicate(Predicates.isNullOrEmptyString());
  }

  default StringChecker<OIN> matchesRegex(String regex) {
    return this.addPredicate(Predicates.matchesRegex(regex));
  }

  default StringChecker<OIN> equalsIgnoreCase(String s) {
    return this.addPredicate(Predicates.equalsIgnoreCase(s));
  }

  default StringChecker<OIN> findRegexes(String... regexes) {
    return this.addPredicate(Predicates.findRegexes(regexes));
  }

  default StringChecker<OIN> findRegexPatterns(Pattern... patterns) {
    return this.addPredicate(Predicates.findRegexPatterns(patterns));
  }

  default StringChecker<OIN> findSubstrings(String... tokens) {
    return this.addPredicate(Predicates.findSubstrings(tokens));
  }

  class Impl<OIN>
      extends Checker.Base<StringChecker<OIN>, OIN, String>
      implements StringChecker<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StringChecker<OIN> create(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
      return Checker.Factory.stringChecker(transformerName, (Function<? super OIN, String>) function, predicate, originalInputValue);
    }
  }
}
