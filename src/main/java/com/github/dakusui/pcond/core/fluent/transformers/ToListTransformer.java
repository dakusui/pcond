package com.github.dakusui.pcond.core.fluent.transformers;

import com.github.dakusui.pcond.core.fluent.Transformer;
import com.github.dakusui.pcond.forms.Functions;

import java.util.List;
import java.util.function.Function;

public class ToListTransformer<OIN, E> extends Transformer<ToListTransformer<OIN, E>, OIN, List<E>> {

  /**
   * @param function A function that transforms
   */
  public <COUT> ToListTransformer(Function<? super COUT, ? extends List<E>> function) {
    super(function);
  }

  public ToObjectTransformer<OIN, E> elementAt(int i) {
    return this.transformToObject(Functions.elementAt(i));
  }


  void method() {
    Functions.stream();
    Functions.elementAt(0);
    Functions.size();
  }
}
