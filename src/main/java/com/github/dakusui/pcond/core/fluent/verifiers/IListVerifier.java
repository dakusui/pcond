package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.Evaluable;
import com.github.dakusui.pcond.core.fluent.IVerifier;
import com.github.dakusui.pcond.core.identifieable.Identifiable;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface IListVerifier<OIN, E> extends
    Identifiable,
    Predicate<OIN>,
    Evaluable.Transformation<OIN, List<E>>,
    IVerifier<IListVerifier<OIN, E>, OIN, List<E>>,
    Matcher.ForList<OIN, E> {
  @Override
  IListVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate, OIN originalInputValue);

  default IListVerifier<OIN, E> isEmpty() {
    return predicate(Predicates.isEmpty());
  }

  default IListVerifier<OIN, E> contains(E element) {
    return predicate(Predicates.contains(element));
  }

  @SuppressWarnings("unchecked")
  default IListVerifier<OIN, E> findElementsInorderBy(Predicate<E>... predicates) {
    return predicate(Predicates.findElements(predicates));
  }

  @SuppressWarnings("unchecked")
  default IListVerifier<OIN, E> findElementsInorder(E... elements) {
    return this.findElementsInorderBy(
        Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]s", e -> Objects.equals(v, e)))
            .toArray(Predicate[]::new));
  }
}
