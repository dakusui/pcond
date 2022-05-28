package com.github.dakusui.pcond.core.fluent;

import java.util.function.Function;

import static com.github.dakusui.pcond.internals.InternalUtils.dummyFunction;

/**
 * Method names start with `as` or contain `As` suggests that the methods should be
 * used when you know the type of the object you are treating at the line of your code.
 * <p>
 * One starts with `into` or contains `Into` should be used for objects you need to
 * apply a function in order to convert it to treat it in the following lines.
 *
 * @param <TX>  The type of this object.
 * @param <OIN> Original input type.
 * @param <OUT> (Current) Output type.
 */
public abstract class Transformer<
    TX extends ITransformer<TX, OIN, OUT>,
    OIN, OUT>
    implements
    ITransformer<TX, OIN, OUT> {
  private final Function<OIN, OUT> function;
  private final String             transformerName;

  /**
   * Constructs an instance of this class.
   *
   * @param transformerName THe name of transformer. This can be {@code null}.
   * @param parent          The parent of the new transformer. {@code null} if it is a root.
   * @param function        A function with which a given value is converted.
   * @param <IN>            The type of the input.
   */
  @SuppressWarnings("unchecked")
  public <IN> Transformer(String transformerName, ITransformer<?, OIN, IN> parent, Function<? super IN, ? extends OUT> function) {
    this.transformerName = transformerName;
    this.function = (Function<OIN, OUT>) ITransformer.chainFunctions(parent == null ? dummyFunction() : parent.function(), function);
  }

  @Override
  public Function<? super OIN, ? extends OUT> function() {
    return this.function;
  }

  @Override
  public String transformerName() {
    return this.transformerName;
  }
}
