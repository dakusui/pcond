package com.github.dakusui.pcond.core.fluent3.builtins;

import com.github.dakusui.pcond.core.fluent3.AbstractObjectChecker;
import com.github.dakusui.pcond.core.fluent3.Matcher;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;

public interface StringChecker<
    R extends Matcher<R, R, OIN, OIN>,
    OIN> extends
    AbstractObjectChecker<
        StringChecker<R, OIN>,
        R,
        OIN,
        String> {
  default StringChecker<R, OIN> contains(String token) {
    return this.checkWithPredicate(Predicates.containsString(token));
  }

  default StringChecker<R, OIN> startsWith(String prefix) {
    return this.checkWithPredicate(Predicates.startsWith(prefix));
  }

  default StringChecker<R, OIN> isEmpty() {
    return this.checkWithPredicate(Predicates.isEmptyString());
  }

  default StringChecker<R, OIN> isNotEmpty() {
    return this.checkWithPredicate(Predicates.isEmptyString().negate());
  }

  default StringChecker<R, OIN> isEqualTo(String string) {
    return this.checkWithPredicate(explainableStringIsEqualTo(string));
  }

  default StringChecker<R, OIN> isNullOrEmpty() {
    return this.checkWithPredicate(Predicates.isNullOrEmptyString());
  }

  default StringChecker<R, OIN> matchesRegex(String regex) {
    return this.checkWithPredicate(Predicates.matchesRegex(regex));
  }

  default StringChecker<R, OIN> equalsIgnoreCase(String s) {
    return this.checkWithPredicate(Predicates.equalsIgnoreCase(s));
  }

  default StringChecker<R, OIN> findRegexes(String... regexes) {
    return this.checkWithPredicate(Predicates.findRegexes(regexes));
  }

  default StringChecker<R, OIN> findRegexPatterns(Pattern... patterns) {
    return this.checkWithPredicate(Predicates.findRegexPatterns(patterns));
  }

  default StringChecker<R, OIN> findSubstrings(String... tokens) {
    return this.checkWithPredicate(Predicates.findSubstrings(tokens));
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
    protected Impl(Supplier<OIN> rootValue, R root) {
      super(rootValue, root);
    }
  }
}
