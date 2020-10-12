package com.github.dakusui.pcond.core.identifieable;

import com.github.dakusui.pcond.core.multi.MultiFunction;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.internals.InternalChecks.requireArgumentListSize;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

public class PrintableMultiFunction<R> extends PrintableFunction<List<? super Object>, R> implements MultiFunction<R> {
  private final String         name;
  private final List<Class<?>> parameterTypes;

  protected PrintableMultiFunction(
      Object creator,
      List<Object> args,
      Supplier<String> formatter,
      String name,
      Function<? super List<? super Object>, ? extends R> function,
      List<Class<?>> parameterTypes) {
    super(
        creator,
        args,
        formatter,
        function);
    this.name = name;
    this.parameterTypes = new ArrayList<>(parameterTypes);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public int arity() {
    return parameterTypes.size();
  }

  @Override
  public Class<?> parameterType(int i) {
    return parameterTypes.get(i);
  }

  /**
   * A builder for a {@link MultiFunction} instance.
   *
   * @param <R> The type of value returned by the multi-function built by this object.
   */
  static class Builder<R> {
    private final Object                                              creator        = Builder.class;
    private       List<Object>                                        identityArgs;
    private       String                                              name           = "(anonymous)";
    private final Function<? super List<? super Object>, ? extends R> body;
    private final List<Class<?>>                                      parameterTypes = new LinkedList<>();
    private       Supplier<String>                                    formatter      = () -> name + "(" + parameterTypes.stream().map(Class::getSimpleName).collect(joining(",")) + ")";

    public Builder(Function<List<Object>, R> body) {
      requireNonNull(body);
      this.body = args -> body.apply(requireArgumentListSize(requireNonNull(args), parameterTypes.size()));
    }

    public Builder<R> addParameters(List<Class<?>> parameterTypes) {
      requireNonNull(parameterTypes).stream().map(this::addParameter).forEach(Objects::requireNonNull);
      return this;
    }

    public Builder<R> identityArgs(List<Object> identity) {
      this.identityArgs = requireNonNull(identity);
      return this;
    }

    public Builder<R> name(String name) {
      this.name = name;
      return this;
    }

    public Builder<R> addParameter(Class<?> parameterType) {
      this.parameterTypes.add(requireNonNull(parameterType));
      return this;
    }

    public Builder<R> formatter(Supplier<String> formatter) {
      this.formatter = requireNonNull(formatter);
      return this;
    }

    public PrintableMultiFunction<R> $() {
      return new PrintableMultiFunction<R>(
          this.creator,
          this.identityArgs,
          this.formatter,
          this.name,
          this.body,
          this.parameterTypes
      );
    }
  }
}
