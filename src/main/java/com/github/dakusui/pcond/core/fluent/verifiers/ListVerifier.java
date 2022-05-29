package com.github.dakusui.pcond.core.fluent.verifiers;

import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListVerifier<OIN, E>
    extends Verifier<ListVerifier<OIN, E>, OIN, List<E>>
    implements Matcher.ForList<OIN, E> {
  public ListVerifier(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate) {
    super(transformerName, function, predicate);
  }

  @Override
  public ListVerifier<OIN, E> create(String transformerName, Function<? super OIN, ? extends List<E>> function, Predicate<? super List<E>> predicate) {
    return new ListVerifier<>(transformerName, function, predicate);
  }

  public ListVerifier<OIN, E> isEmpty() {
    return predicate(Predicates.isEmpty());
  }

  public ListVerifier<OIN, E> contains(E element) {
    return predicate(Predicates.contains(element));
  }

  @SafeVarargs
  public final ListVerifier<OIN, E> findElementsInorderBy(Predicate<E>... predicates) {
    return predicate(Predicates.findElements(predicates));
  }

  @SuppressWarnings("unchecked")
  public final ListVerifier<OIN, E> findElementsInorder(E... elements) {
    return this.findElementsInorderBy(
        Arrays.stream(elements)
            .map(v -> Printables.predicate("[" + v + "]s", e -> Objects.equals(v, e)))
            .toArray(Predicate[]::new));
  }
}
