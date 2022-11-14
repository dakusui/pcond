package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.Checker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;

public interface StringChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    Checker<
        StringChecker<R, OIN>,
        R,
        OIN,
        String> {
  default StringChecker<R, OIN> contains(String token) {
    return this.appendPredicateAsChild(Predicates.containsString(token));
  }

  default StringChecker<R, OIN> startsWith(String prefix) {
    return this.appendPredicateAsChild(Predicates.startsWith(prefix));
  }

  default StringChecker<R, OIN> isEmpty() {
    return this.appendPredicateAsChild(Predicates.isEmptyString());
  }

  default StringChecker<R, OIN> isNotEmpty() {
    return this.appendPredicateAsChild(Predicates.isEmptyString().negate());
  }

  default StringChecker<R, OIN> isEqualTo(String string) {
    return this.appendPredicateAsChild(explainableStringIsEqualTo(string));
  }

  default StringChecker<R, OIN> isNull() {
    return this.appendPredicateAsChild(Predicates.isNull());
  }

  default StringChecker<R, OIN> isNotNull() {
    return this.appendPredicateAsChild(Predicates.isNotNull());
  }

  default StringChecker<R, OIN> isNullOrEmpty() {
    return this.appendPredicateAsChild(Predicates.isNullOrEmptyString());
  }

  default StringChecker<R, OIN> matchesRegex(String regex) {
    return this.appendPredicateAsChild(Predicates.matchesRegex(regex));
  }

  default StringChecker<R, OIN> equalsIgnoreCase(String s) {
    return this.appendPredicateAsChild(Predicates.equalsIgnoreCase(s));
  }

  default StringChecker<R, OIN> findRegexes(String... regexes) {
    return this.appendPredicateAsChild(Predicates.findRegexes(regexes));
  }

  default StringChecker<R, OIN> findRegexPatterns(Pattern... patterns) {
    return this.appendPredicateAsChild(Predicates.findRegexPatterns(patterns));
  }

  default StringChecker<R, OIN> findSubstrings(String... tokens) {
    return this.appendPredicateAsChild(Predicates.findSubstrings(tokens));
  }

  class Impl<
      R extends Matcher<R, R, OIN, OIN>,
      OIN>
      extends
      Matcher.Base<
          StringChecker<R, OIN>,
          R,
          OIN,
          String> implements
      StringChecker<R, OIN> {
    protected Impl(OIN rootValue, R root) {
      super(rootValue, root);
    }
  }
}
