package com.github.dakusui.pcond.core.identifieable;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

public abstract class IdentifiableFactoryForParameterizedPredicate<V> extends Identifiable.Base implements IdentifiableExample.IdentifiableFactory<IdentifiableExample.IdentifiableFactory.IdentifiablePredicate<V>> {
  protected IdentifiableFactoryForParameterizedPredicate(Object creator, List<Object> args) {
    super(creator, args);
  }

  protected IdentifiableFactoryForParameterizedPredicate() {
    this(new Object(), emptyList());
  }

  @Override
  public IdentifiablePredicate<V> create(List<Object> args) {
    Predicate<V> predicate = createPredicate(args);
    return new IdentifiablePredicate.Base<V>(this, args) {
      @Override
      public boolean test(V v) {
        assert predicate != null;
        return predicate.test(v);
      }
    };
  }

  protected abstract Predicate<V> createPredicate(List<Object> args);
}
