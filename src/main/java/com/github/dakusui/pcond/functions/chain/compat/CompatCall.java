package com.github.dakusui.pcond.functions.chain.compat;

import com.github.dakusui.pcond.functions.chain.ChainUtils;
import com.github.dakusui.pcond.functions.chain.ChainedFunction;

import java.util.Arrays;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * An interface of a builder for a function that transforms a "target object" into
 * another value by calling a chain of methods on it and a value from a previous call.
 * <p>
 * That is, you can build a function that does following if a StringBuilder is given as
 * its input,
 * {@code
 * (StringBuilder b) -> b.append("hello").append(1).append("world").append("everyone").toString()
 * }
 * By following code,
 * {@code
 * Function func = Call.create("append", "hello")
 * .andThen("append", 1)
 * .andThen("append", "everyone")
 * .andThen("toString")
 * .$()
 * }
 * <p>
 * The benefit of using this class is to be able to print what you are going to do
 * in a pretty format in "Crest" library's output. If you call {@code toString}
 * method on the {@code func} object, you will get,
 * <p>
 * {@code
 * "@append[hello]->@append[1]->@append[everyone]->@toString[]"
 * }
 * <p>
 * , which is far more understandable than a string that you will get for a function
 * built in a normal way mentioned above such as
 *
 * <pre>
 *   "com.github.dakusui.crest.ut.CrestTest$CallMechanismTest$$Lambda$9/1556956098@4aa8f0b4"
 * </pre>
 */
public interface CompatCall {

  Object THIS = new Object() {
    public String toString() {
      return "(THIS)";
    }
  };

  /**
   * Add a method call of a method specified by {@code methodName} and {@code args}
   * to this builder on the current target object.
   * <p>
   * If you need to invoke a method on other than the current target object,
   * you need to use {@code andThenOn} method.
   * n
   *
   * @param methodName a name of method to be invoked.
   * @param args       Arguments with which the method is invoked.
   * @return This object.
   */
  default CompatCall andThen(String methodName, Object... args) {
    return andThenOn(THIS, methodName, args);
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
  CompatCall andThenOn(Object object, String methodName, Object... args);

  /**
   * Builds a function.
   *
   * @param <T> Type of the result of the function to be built.
   * @return The function.
   */
  <T> Function<Object, T> build();

  /**
   * A synonym of {@code build} method.
   *
   * @param <T> The type of the returned value.
   * @return A built function.
   */
  default <T> Function<Object, T> $() {
    return build();
  }

  /**
   * A synonym of {@code Call.create(Functions.THIS, methodName, args)}.
   *
   * @param methodName A name of the method to be invoked.
   * @param args       Arguements with which the method is invoked.
   * @return The result of the invocation.
   * @see CompatCall#createOn(Object, String, Object...)
   */
  static CompatCall create(String methodName, Object... args) {
    return createOn(THIS, methodName, args);
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
  static CompatCall createOn(Object object, String methodName, Object... args) {
    return new CompatCall.Impl(null, object, methodName, args);
  }

  interface Arg<T> {
    Class<T> type();

    T value();

    static <T> Arg<T> of(Class<T> type, T value) {
      return new Arg<T>() {
        @Override
        public Class<T> type() {
          return type;
        }

        @Override
        public T value() {
          return value;
        }

        @Override
        public String toString() {
          return String.format(
              "%s %s",
              type.getSimpleName(),
              value == null
                  ? null
                  : value.getClass().isArray()
                  ? Arrays.toString((Object[]) value)
                  : value);
        }
      };
    }
  }

  class Impl implements CompatCall {
    private final String     methodName;
    private final Object[]   args;
    private final CompatCall parent;
    private final Object     object;

    /**
     * Creates a new {@code Call.Impl} object.
     *
     * @param parent     This can be {@code null} when a head of chain is created.
     * @param object     An object on which a method specified by {@code methodName}
     *                   and {@code args} is invoked.
     * @param methodName A name of a method to be invoked.
     * @param args       Arguments of a method to be invoked.
     */
    Impl(CompatCall parent, Object object, String methodName, Object... args) {
      this.parent = parent;
      this.object = requireNonNull(object);
      this.methodName = requireNonNull(methodName);
      this.args = args;
    }

    @Override
    public CompatCall andThenOn(Object object, String methodName, Object... args) {
      return new Impl(this, object, methodName, args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Function<Object, T> build() {
      return this.parent == null
          ? toFunction()
          : this.parent.build().andThen(toFunction());
    }

    @SuppressWarnings({ "unchecked", "RedundantCast", "rawtypes" })
    private Function toFunction() {
      return ChainedFunction.create(
          this.object instanceof Class
              ? (Function) CompatUtils.invokeStatic((Class<?>) this.object, methodName, args)
              : (Function) CompatUtils.invokeOn(this.object, methodName, args)
      );
    }
  }
}
