package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.core.printable.ExplainablePredicate.explainableStringIsEqualTo;

public class StringVerifier<OIN> extends Verifier<StringVerifier<OIN>, OIN, String> implements Matcher.ForString<OIN> {
  public StringVerifier(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate) {
    super(transformerName, function, predicate);
  }

  @Override
  protected StringVerifier<OIN> create() {
    return new StringVerifier<>(this.transformerName, this.function(), this.predicate());
  }

  public StringVerifier<OIN> contains(String token) {
    return this.predicate(Predicates.containsString(token));
  }

  public StringVerifier<OIN> startsWith(String prefix) {
    return this.predicate(Predicates.startsWith(prefix));
  }

  public StringVerifier<OIN> isEmpty() {
    return this.predicate(Predicates.isEmptyString());
  }

  public StringVerifier<OIN> isEqualTo(String string) {
    return this.predicate(explainableStringIsEqualTo(string));
  }

  public StringVerifier<OIN> isNullOrEmpty() {
    return this.predicate(Predicates.isNullOrEmptyString());
  }

  public StringVerifier<OIN> matchesRegex(String regex) {
    return this.predicate(Predicates.matchesRegex(regex));
  }

  public StringVerifier<OIN> equalsIgnoreCase(String s) {
    return this.predicate(Predicates.equalsIgnoreCase(s));
  }

  public StringVerifier<OIN> findRegexes(String... regexes) {
    return this.predicate(Predicates.findRegexes(regexes));
  }

  public StringVerifier<OIN> findRegexPatterns(Pattern... patterns) {
    return this.predicate(Predicates.findRegexPatterns(patterns));
  }

  public StringVerifier<OIN> findSubstrings(String... tokens) {
    return this.predicate(Predicates.findSubstrings(tokens));
  }
}
