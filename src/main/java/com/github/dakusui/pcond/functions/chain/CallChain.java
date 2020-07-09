package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.MultiFunction;

import java.util.Collections;

import static com.github.dakusui.pcond.functions.chain.ChainUtils.instanceMethod;
import static com.github.dakusui.pcond.functions.chain.ChainUtils.methodCall;
import static java.util.Objects.requireNonNull;

public interface CallChain {

  /**
   * Adds a call of a method specified by {@code object}, {@code methodName} and
   * {@code args}.
   *
   * @return This object.
   */
  CallChain andThen(ChainUtils.MethodQuery methodQuery);

  <R> MultiFunction<? extends R> build();


  static CallChain create(ChainUtils.MethodQuery query) {
    return new Impl(null, query);
  }

  class Impl implements CallChain {
    private final CallChain              parent;
    private final ChainUtils.MethodQuery methodQuery;

    public Impl(CallChain parent, ChainUtils.MethodQuery methodQuery) {
      this.parent = parent;
      this.methodQuery = requireNonNull(methodQuery);
    }

    @Override
    public CallChain andThen(ChainUtils.MethodQuery methodQuery) {
      return new Impl(this, methodQuery);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MultiFunction<? extends R> build() {
      if (this.parent == null)
        return methodCall(this.methodQuery);
      return new MultiFunction.Builder<>(args ->
          (R) methodCall(instanceMethod(
              this.parent.build().apply(args),
              this.methodQuery.methodName(),
              this.methodQuery.arguments())).apply(args))
          .addParameters(Collections.emptyList())
          .$();
    }
  }
}
