package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ListVerifier<OIN, E> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, List<E>>,
    Verifier<ListVerifier<OIN, E>, OIN, List<E>>,
    Matcher.ForList<OIN, E> {
  @Override
  ListVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue);

  default ListVerifier<OIN, E> isEmpty() {
    return predicate(Predicates.isEmpty());
  }

  default ListVerifier<OIN, E> contains(E element) {
    return predicate(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default ListVerifier<OIN, E> findElementsInorderBy(Predicate<E>... predicates) {
    return predicate(Predicates.findElements(predicates));
  }

  @SuppressWarnings("unchecked")
  default ListVerifier<OIN, E> findElementsInorder(E... elements) {
    return this.findElementsInorderBy(
        Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]s", e -> Objects.equals(v, e)))
            .toArray(Predicate[]::new));
  }

  class Impl<OIN, E>
      extends BaseVerifier<ListVerifier<OIN, E>, OIN, List<E>>
      implements ListVerifier<OIN, E> {
    public Impl(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      super(transformerName, function, predicate, originalInputValue);
    }

    @Override
    public ListVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue) {
      return Verifier.Factory.listVerifier(transformerName, function, predicate, originalInputValue);
    }
  }
}
