package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.pcond.functions.Predicates;

import java.util.Objects;
import java.util.function.Function;

public class AsString<IN> extends AsComparable<IN, String, AsString<IN>> {
  public AsString(Function<? super IN, ? extends String> function) {
    super(function);
  }

  public AsString<IN> matchesRegex(String regex) {
    return this.check(Predicates.matchesRegex(Objects.requireNonNull(regex)));
  }

  public AsString<IN> containsString(String string) {
    return this.check(Predicates.containsString(Objects.requireNonNull(string)));

  }

  public AsString<IN> startsWith(String s) {
    return this.check(Predicates.startsWith(Objects.requireNonNull(s)));
  }

  public AsString<IN> endsWith(String s) {
    return this.check(Predicates.endsWith(Objects.requireNonNull(s)));
  }

  public AsString<IN> equalsIgnoreCase(String s) {
    return this.check(Predicates.equalsIgnoreCase(Objects.requireNonNull(s)));
  }

  public AsString<IN> isEmpty() {
    return this.check(Predicates.isEmptyString());
  }

  public AsString<IN> isEmptyOrNull() {
    return this.check(Predicates.isEmptyOrNullString());
  }
}
