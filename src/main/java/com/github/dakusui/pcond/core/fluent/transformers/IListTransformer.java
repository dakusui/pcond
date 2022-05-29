package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.verifiers.IListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;

import java.util.Collection;
import java.util.List;

public interface IListTransformer<OIN, E> extends ITransformer<IListTransformer<OIN, E>, OIN, List<E>>, Matcher.ForList<OIN, E> {
  @Override
  IListVerifier<OIN, E> then();

  default IObjectTransformer<OIN, E> elementAt(int i) {
    return this.transformToObject(Functions.elementAt(i));
  }

  default IIntegerTransformer<OIN> size() {
    return this.transformToInteger(Functions.size());
  }

  default IListTransformer<OIN, E> subList(int begin, int end) {
    return this.transformToList(Printables.function("subList", v -> v.subList(begin, end)));
  }

  default IListTransformer<OIN, E> subList(int begin) {
    return this.transformToList(Printables.function("subList", v -> v.subList(begin, v.size())));
  }

  default StreamTransformer<OIN, E> stream() {
    return this.transformToStream(Printables.function("listStream", Collection::stream));
  }

  default IBooleanTransformer<OIN> isEmpty() {
    return this.transformToInBoolean(Printables.function("listIsEmpty", List::isEmpty));
  }
}
