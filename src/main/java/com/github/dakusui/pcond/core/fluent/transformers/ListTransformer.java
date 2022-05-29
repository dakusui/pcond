package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.core.fluent.Verifier;
import com.github.dakusui.pcond.core.fluent.verifiers.ListVerifier;
import com.github.dakusui.pcond.core.fluent.verifiers.Matcher;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.internals.InternalUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface ListTransformer<OIN, E> extends Transformer<ListTransformer<OIN, E>, OIN, List<E>>, Matcher.ForList<OIN, E> {
  @Override
  ListVerifier<OIN, E> then();

  default ObjectTransformer<OIN, E> elementAt(int i) {
    return this.transformToObject(Functions.elementAt(i));
  }

  default IntegerTransformer<OIN> size() {
    return this.transformToInteger(Functions.size());
  }

  default ListTransformer<OIN, E> subList(int begin, int end) {
    return this.transformToList(Printables.function("subList", v -> v.subList(begin, end)));
  }

  default ListTransformer<OIN, E> subList(int begin) {
    return this.transformToList(Printables.function("subList", v -> v.subList(begin, v.size())));
  }

  default StreamTransformer<OIN, E> stream() {
    return this.transformToStream(Printables.function("listStream", Collection::stream));
  }

  default BooleanTransformer<OIN> isEmpty() {
    return this.transformToInBoolean(Printables.function("listIsEmpty", List::isEmpty));
  }

  class Impl<OIN, E>
      extends BaseTransformer<ListTransformer<OIN, E>, OIN, List<E>>
      implements ListTransformer<OIN, E> {
    /**
     * Constructs an object of this class.
     *
     * @param transformerName    A name of transformer printed in a failure report.
     * @param parent             A parent transformer
     * @param function           A function that transforms an input value in to the target value of this transformer.
     * @param originalInputValue An original input value.
     */
    public <IN> Impl(String transformerName, Transformer<?, OIN, IN> parent, Function<? super IN, ? extends List<E>> function, OIN originalInputValue) {
      super(transformerName, parent, function, originalInputValue);
    }

    @Override
    public ListVerifier<OIN, E> then() {
      return Verifier.Factory.listVerifier(this.transformerName(), this.function(), InternalUtils.dummyPredicate(), this.originalInputValue());
    }
  }
}
