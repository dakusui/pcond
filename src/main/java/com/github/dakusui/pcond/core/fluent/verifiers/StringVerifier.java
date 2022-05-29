package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class StringVerifier<OIN>
    extends Verifier<IStringVerifier<OIN>, OIN, String>
    implements IStringVerifier<OIN> {
  public StringVerifier(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
    super(transformerName, function, predicate, originalInputValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  public IStringVerifier<OIN> create(String transformerName, Function<? super OIN, ? extends String> function, Predicate<? super String> predicate, OIN originalInputValue) {
    return IVerifier.Factory.stringVerifier(transformerName, (Function<? super OIN, String>) function, predicate, originalInputValue);
  }
}
