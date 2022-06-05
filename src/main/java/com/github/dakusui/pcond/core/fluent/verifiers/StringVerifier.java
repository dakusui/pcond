package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Matcher;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;

public interface StringVerifier<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, String>,
    Verifier<StringVerifier<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default StringVerifier<OIN> contains(String token) {
    return this.predicate(Predicates.containsString(token));
  }

  default StringVerifier<OIN> startsWith(String prefix) {
    return this.predicate(Predicates.startsWith(prefix));
  }

  default StringVerifier<OIN> isEmpty() {
    return this.predicate(Predicates.isEmptyString());
  }

  default StringVerifier<OIN> isEqualTo(String string) {
    return this.predicate(explainableStringIsEqualTo(string));
  }

  default StringVerifier<OIN> isNullOrEmpty() {
    return this.predicate(Predicates.isNullOrEmptyString());
  }

  default StringVerifier<OIN> matchesRegex(String regex) {
    return this.predicate(Predicates.matchesRegex(regex));
  }

  default StringVerifier<OIN> equalsIgnoreCase(String s) {
    return this.predicate(Predicates.equalsIgnoreCase(s));
  }

  default StringVerifier<OIN> findRegexes(String... regexes) {
    return this.predicate(Predicates.findRegexes(regexes));
  }

  default StringVerifier<OIN> findRegexPatterns(Pattern... patterns) {
    return this.predicate(Predicates.findRegexPatterns(patterns));
  }

  default StringVerifier<OIN> findSubstrings(String... tokens) {
    return this.predicate(Predicates.findSubstrings(tokens));
  }

  class Impl<OIN>
      extends Verifier.Base<StringVerifier<OIN>, OIN, String>
      implements StringVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StringVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
      return Verifier.Factory.stringVerifier(transformerName, (Function<? super OIN, String>) function, predicate, originalInputValue);
    }
  }
}
