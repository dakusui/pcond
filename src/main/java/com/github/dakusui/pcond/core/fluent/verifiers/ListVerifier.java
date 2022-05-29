package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.fluent.Verifier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListVerifier<OIN, E>
    extends Verifier<IListVerifier<OIN, E>, OIN, List<E>>
    implements IListVerifier<OIN, E> {
  public ListVerifier(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
    super(transformerName, function, predicate, originalInputValue);
  }

  @Override
  public IListVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
    return IVerifier.Factory.listVerifier(transformerName, function, predicate, originalInputValue);
  }
}
