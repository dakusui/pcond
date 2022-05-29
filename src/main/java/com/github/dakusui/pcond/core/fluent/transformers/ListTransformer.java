package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.ITransformer;
import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ListTransformer<OIN, E>
    extends Transformer<ListTransformer<OIN, E>, OIN, List<E>>
    implements Matcher.ForList<OIN, E> {
  /**
   * Constructs an object of this class.
   *
   * @param transformerName    A name of transformer printed in a failure report.
   * @param parent             A parent transformer
   * @param function           A function that transforms an input value in to the target value of this transformer.
   * @param originalInputValue An original input value.
   */
  public <IN> ListTransformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends List<E>> function, OIN originalInputValue) {
    super(transformerName, parent, function, originalInputValue);
  }

  @Override
  public ListVerifier<OIN, E> then() {
    return new ListVerifier<>(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
  }

  public ObjectTransformer<OIN, E> elementAt(int i) {
    return this.transformToObject(Functions.elementAt(i));
  }

  public IIntegerTransformer<OIN> size() {
    return this.transformToInteger(Functions.size());
  }

  public ListTransformer<OIN, E> subList(int begin, int end) {
    return this.transformToList(Printables.function("subList", v -> v.subList(begin, end)));
  }

  public ListTransformer<OIN, E> subList(int begin) {
    return this.transformToList(Printables.function("subList", v -> v.subList(begin, v.size())));
  }

  public StreamTransformer<OIN, E> stream() {
    return this.transformToStream(Printables.function("listStream", Collection::stream));
  }

  public IBooleanTransformer<OIN> isEmpty() {
    return this.transformToInBoolean(Printables.function("listIsEmpty", List::isEmpty));
  }
}
