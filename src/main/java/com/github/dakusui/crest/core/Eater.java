package com.github.dakusui.crest.core;

import com.github.dakusui.pcond.functions.Printable;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.dakusui.pcond.functions.Predicates.equalTo;
import static java.util.Objects.requireNonNull;

public interface Eater<T /* Target*/, C /* Target container */> {
  Eater after(T target);

  /**
   * Builds a function.
   *
   * @return The function.
   */
  Function<C, C> build();

  /**
   * A synonym of {@code build} method.
   *
   * @return A built function.
   */
  default Function<C, C> $() {
    return build();
  }

  abstract class Base<T, C> implements Eater<T, C> {
    final T           finder;
    final Eater<T, C> parent;

    Base(Eater<T, C> parent, T finder) {
      this.parent = parent;
      this.finder = requireNonNull(finder);
    }

    @Override
    public Function<C, C> build() {
      return this.parent == null
          ? toFunction()
          : this.parent.build().andThen(toFunction());
    }

    Function<C, C> toFunction() {
      return ChainedFunction.create(
          Printable.function(
              describeFunction(),
              createFunction()
          ));
    }

    String describeFunction() {
      return String.format("->after[%s]", this.finder);
    }

    protected abstract Function<C, C> createFunction();

  }

  class RegexEater extends Base<String, String> {
    public RegexEater(Eater<String, String> parent, String target) {
      super(parent, target);
    }

    /**
     * @param target A regex
     * @return A new {@code RegexEater}
     */
    @Override
    public RegexEater after(String target) {
      return new RegexEater(this, target);
    }

    protected Function<String, String> createFunction() {
      return (String container) -> {
        Matcher matcher = Pattern.compile(String.format("(%s)", finder)).matcher(container);
        if (matcher.find())
          return restOf(container, matcher.group(1));
        throw new AssertionFailedError(String.format("regex:%s was not found", finder));
      };
    }

    private String restOf(String container, String matched) {
      return container.substring(matched.length() + container.indexOf(matched));
    }
  }

  class ListEater<T> extends Base<Predicate<T>, List<T>> {

    public ListEater(Eater<Predicate<T>, List<T>> parent, Predicate<T> target) {
      super(parent, target);
    }

    @Override
    protected Function<List<T>, List<T>> createFunction() {
      return container -> {
        int index = findTarget(container);
        if (index < 0)
          throw new AssertionFailedError(String.format("Element:%s was not found", finder));
        return container.subList(index + 1, container.size());
      };
    }

    @Override
    public ListEater<T> after(Predicate<T> finder) {
      return new ListEater<>(this, finder);
    }

    public ListEater<T> afterElement(T target) {
      return new ListEater<>(this, equalTo(target));
    }

    int findTarget(List<T> container) {
      int i = 0;
      for (T elem : container) {
        if (this.finder.test(elem))
          return i;
        i++;
      }
      return -1;
    }
  }
}
