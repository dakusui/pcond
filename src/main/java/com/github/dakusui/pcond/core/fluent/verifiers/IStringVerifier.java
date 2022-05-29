package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;

public interface IStringVerifier<OIN> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, String>,
    IVerifier<IStringVerifier<OIN>, OIN, String>,
    Matcher.ForString<OIN> {
  default IStringVerifier<OIN> contains(String token) {
    return this.predicate(Predicates.containsString(token));
  }

  default IStringVerifier<OIN> startsWith(String prefix) {
    return this.predicate(Predicates.startsWith(prefix));
  }

  default IStringVerifier<OIN> isEmpty() {
    return this.predicate(Predicates.isEmptyString());
  }

  default IStringVerifier<OIN> isEqualTo(String string) {
    return this.predicate(explainableStringIsEqualTo(string));
  }

  default IStringVerifier<OIN> isNullOrEmpty() {
    return this.predicate(Predicates.isNullOrEmptyString());
  }

  default IStringVerifier<OIN> matchesRegex(String regex) {
    return this.predicate(Predicates.matchesRegex(regex));
  }

  default IStringVerifier<OIN> equalsIgnoreCase(String s) {
    return this.predicate(Predicates.equalsIgnoreCase(s));
  }

  default IStringVerifier<OIN> findRegexes(String... regexes) {
    return this.predicate(Predicates.findRegexes(regexes));
  }

  default IStringVerifier<OIN> findRegexPatterns(Pattern... patterns) {
    return this.predicate(Predicates.findRegexPatterns(patterns));
  }

  default IStringVerifier<OIN> findSubstrings(String... tokens) {
    return this.predicate(Predicates.findSubstrings(tokens));
  }

  class Impl<OIN>
      extends Verifier<IStringVerifier<OIN>, OIN, String>
      implements IStringVerifier<OIN> {
    public Impl(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public IStringVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
      return IVerifier.Factory.stringVerifier(transformerName, (Function<? super OIN, String>) function, predicate, originalInputValue);
    }
  }
}
