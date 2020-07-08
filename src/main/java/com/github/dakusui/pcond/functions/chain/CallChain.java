package com.github.dakusui.pcond.functions.chain;

import com.github.dakusui.pcond.functions.MultiFunction;
import com.github.dakusui.pcond.functions.chain.compat.CompatCall;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public interface CallChain {

  default CallChain then(String methodName, Object... args) {
    return thenOn(CompatCall.THIS, methodName, args);
  }

  /**
   * Adds a call of a method specified by {@code object}, {@code methodName} and
   * {@code args}.
   *
   * @param object     An object on which the method should be invoked.
   * @param methodName A name of the method to be invoked
   * @param args       Arguments with which the method is invoked.
   * @return This object.
   */
  CallChain thenOn(Object object, String methodName, Object... args);

  default <R> MultiFunction<R> build() {
    return new MultiFunction.Builder<R>(null)
        .$();
  }

  /**
   * A synonym of {@code Call.create(Functions.THIS, methodName, args)}.
   *
   * @param methodName A name of the method to be invoked.
   * @param args       Arguements with which the method is invoked.
   * @return The result of the invocation.
   * @see CompatCall#createOn(Object, String, Object...)
   */
  static CallChain create(String methodName, Object... args) {
    return createOn(CompatCall.THIS, methodName, args);
  }

  /**
   * Creates a {@code Call} object which invokes a method specified by a {@code methodName},
   * {@code args}, and {@code object}.
   * <p>
   * If the {@code object} is {@code null}, an exception will be thrown.
   * If the {@code object} is {@code Functions#THIS}, the method is searched from
   * the target object.
   * If the {@code object} is an instance of {@code java.lang.Class}, the method is
   * searched from the class described by the {@code object}.
   * Otherwise, the method will be searched from the class of the {@code object}
   * itself.
   * <p>
   * The method is searched by a {@code InternalUtils.findMethod}.
   *
   * @param object     An object on which the method is invoked
   * @param methodName A name to specify a method to be invoked
   * @param args       Argument values with which the method is invoked
   * @return A result of the invocation.
   * @see CompatCall#THIS
   * @see ChainUtils#findMethod(Class, String, Object[])
   */
  static CallChain createOn(Object object, String methodName, Object... args) {
    return new CallChain.Impl(null,  methodName, args);
  }

  class Impl implements CallChain {
    private final String    methodName;
    private final Object[]  args;
    private final CallChain parent;

    public Impl(CallChain parent, String methodName, Object[] args) {
      this.parent = parent;
      this.methodName = requireNonNull(methodName);
      this.args = args;
    }


    @Override
    public CallChain thenOn(Object object, String methodName, Object... args) {
      return null; //new Impl(this, object, methodName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> MultiFunction<R> build() {
      return this.parent == null
          ? null //toFunction()
          : null;//this.parent.build().andThen(toFunction());
    }
    @SuppressWarnings({ "unchecked", "RedundantCast", "rawtypes" })
    private Function toFunction() {
      return null;
      /*
      return ChainedFunction.create(
          this.target instanceof Class
              ? (Function) ChainUtils.invokeStatic((Class<?>) this.target, methodName, args)
              : (Function) ChainUtils.invokeOn(this.target, methodName, args)
      );

       */
    }
  }
}
