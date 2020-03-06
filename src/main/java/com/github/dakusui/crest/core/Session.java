package com.github.dakusui.crest.core;

import com.github.dakusui.pcond.functions.TransformingPredicate;
import org.opentest4j.AssertionFailedError;

import java.util.*;
import java.util.function.*;

import static com.github.dakusui.crest.utils.InternalUtils.*;
import static java.lang.String.format;
import static java.util.Arrays.asList;

public interface Session<T> {
  static <T> void perform(String message, T value, Matcher<? super T> matcher, ExceptionFactory exceptionFactory) {
    Report report = perform(value, matcher);
    if (!report.isSuccessful()) {
      if (report.exceptions().isEmpty()) {
        Throwable exception = exceptionFactory.create(message, report, report.exceptions());
        throw wrap(exception);
      }
      Throwable firstException = report.exceptions().get(0);
      if (firstException instanceof AssertionFailedError)
        throw new AssertionFailedError(
            firstException.getMessage(),
            report.expectation(),
            report.mismatch()
        );
      throw new ExecutionFailure(message, report.expectation(), report.mismatch(), report.exceptions());
    }
  }

  static RuntimeException wrap(Throwable exception) {
    if (exception instanceof RuntimeException)
      throw (RuntimeException) exception;
    if (exception instanceof Error)
      throw (Error) exception;
    throw new RuntimeException(exception);
  }

  static <T> Report perform(T value, Matcher<T> matcher) {
    return perform(value, matcher, create());
  }

  static <T> Report perform(T value, Matcher<T> matcher, Session<T> session) {
    if (matcher.matches(value, session, new LinkedList<>())) {
      session.matched(true);
    } else {
      session.describeActualValue(value);
      matcher.describeExpectation(session.matched(false));
      matcher.describeMismatch(value, session);
    }
    return session.report();
  }

  void describeActualValue(T value);

  Report report();

  void describeExpectation(Matcher.Composite<T> matcher);

  void describeExpectation(Matcher.Leaf<T> matcher);

  void describeMismatch(T value, Matcher.Composite<T> matcher);

  void describeMismatch(T value, Matcher.Leaf<T> matcher);

  @SuppressWarnings("unchecked")
  default <X> boolean matches(Matcher.Leaf<T> leaf, T value, Consumer<Throwable> listener) {
    if (this instanceof Impl)
      ((Impl<T>) this).snapshot(value, null, value);
    try {
      return this.test(
          (Predicate<X>) leaf.p(),
          this.apply(
              (Function<T, X>) leaf.func(),
              value
          ));
    } catch (RuntimeException | Error exception) {
      throwIfBlacklisted(exception);
      listener.accept(exception);
      addException(exception);
      return false;
    }
  }

  <I, O> O apply(Function<I, O> func, I value);

  <I> boolean test(Predicate<I> pred, I value);

  static <T> Session<T> create() {
    return new Impl<>();
  }

  Session<T> addException(Throwable exception);

  Session<T> matched(boolean b);

  @FunctionalInterface
  interface ExceptionFactory {
    Throwable create(String message, Report report, List<Throwable> causes);
  }

  class Impl<T> implements Session<T> {
    static class Writer {
      private int level = 0;
      private List<String> buffer = new LinkedList<>();

      Impl.Writer enter() {
        level++;
        return this;
      }

      Impl.Writer leave() {
        level--;
        return this;
      }

      Impl.Writer appendLine(String format, Object... args) {
        buffer.add(String.format(indent(this.level) + format, args));
        return this;
      }

      String write() {
        return String.join("\n", this.buffer);
      }

      private String indent(int level) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < level; i++) {
          builder.append("  ");
        }
        return builder.toString();
      }
    }

    private static final String VARIABLE_NAME = "x";
    private static final String TRANSFORMED_VARIABLE_NAME = "y";
    private Map<Function<?, ?>, Function<?, ?>> memoizationMapForFunctions = new HashMap<>();
    private Map<Predicate<?>, Predicate<?>> memoizationMapForPredicates = new HashMap<>();
    private Map<List<Object>, String> snapshots = new HashMap<>();

    Impl.Writer expectationWriter = new Impl.Writer();
    Impl.Writer mismatchWriter = new Impl.Writer();

    private boolean result;
    private List<Throwable> exceptions = new LinkedList<>();

    @Override
    public Report report() {
      if (!Impl.this.exceptions.isEmpty())
        mismatchWriter.appendLine("FAILED");
      return new Report() {
        private List<Throwable> exceptions = Collections.unmodifiableList(Impl.this.exceptions);
        private boolean result = Impl.this.result;
        private String mismatch = mismatchWriter.write();
        private String expectation = expectationWriter.write();

        @Override
        public String expectation() {
          return expectation;
        }

        @Override
        public String mismatch() {
          return mismatch;
        }

        @Override
        public List<Throwable> exceptions() {
          return this.exceptions;
        }

        @Override
        public boolean isSuccessful() {
          return result && exceptions().isEmpty();
        }
      };

    }

    @Override
    public void describeExpectation(Matcher.Composite<T> matcher) {
      beginExpectation(matcher);
      try {
        matcher.children().forEach(each -> each.describeExpectation(this));
      } finally {
        endExpectation(matcher);
      }
    }

    void beginExpectation(Matcher.Composite<T> matcher) {
      expectationWriter.appendLine(format("%s:[", matcher.name())).enter();
    }

    @SuppressWarnings("unused")
    void endExpectation(Matcher.Composite<T> matcher) {
      expectationWriter.leave().appendLine("]");
    }

    @Override
    public void describeMismatch(T value, Matcher.Composite<T> matcher) {
      beginMismatch(matcher);
      try {
        matcher.children().forEach(each -> each.describeMismatch(value, this));
      } finally {
        endMismatch();
      }
    }

    @Override
    public void describeActualValue(T value) {
      this.expectationWriter.appendLine("%s=%s satisfies", VARIABLE_NAME, snapshotOf(null, value));
      this.mismatchWriter.appendLine("%s=%s did not satisfy", VARIABLE_NAME, snapshotOf(null, value));
    }

    @Override
    public void describeExpectation(Matcher.Leaf<T> matcher) {
      describeExpectationTo(expectationWriter, matcher);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void describeMismatch(T value, Matcher.Leaf<T> matcher) {
      if (this.matches(matcher, value, (Consumer<Throwable>) NOP)) {
        describeExpectationTo(mismatchWriter, matcher);
        return;
      }
      Function<T, ?> func = matcher.func();
      Predicate<?> p = matcher.p();
      appendMismatchSummary(value, func, p);
      // if p is transforming predicate
      //    p(y) == true
      //    y    =  f(func(x))
      // -> In this case, how p worked can be broken down into p(y) side and
      //    f(func(x)) side.
      explainMatcherPredicate(value, func, p);
    }

    private void appendMismatchSummary(T value, Function<T, ?> func, Predicate<?> p) {
      String formattedExpectation = formatExpectation(p, func);
      String formattedFunctionOutput = this.snapshotOf(func, value);
      if (fails(func, value)) {
        this.mismatchWriter.appendLine(
            "%s failed with %s",
            formattedExpectation,
            formattedFunctionOutput
        );
      } else if (fails(p, this.apply(func, value))) {
        this.mismatchWriter.appendLine(
            "%s failed with %s",
            formattedExpectation,
            this.snapshotOf(p, this.apply(func, value))
        );
      } else {
        this.mismatchWriter.appendLine("%s: NOT MET", formattedExpectation);
      }
    }

    void describeExpectationTo(Impl.Writer writer, Matcher.Leaf<T> matcher) {
      writer.appendLine("%s", formatExpectation(matcher.p(), matcher.func()));
    }

    void beginMismatch(Matcher.Composite<T> matcher) {
      this.mismatchWriter.appendLine("%s:[", matcher.name());
      mismatchWriter.enter();
    }

    void endMismatch() {
      this.mismatchWriter.leave().appendLine("]: NOT MET");
    }


    @Override
    public Session<T> addException(Throwable exception) {
      this.exceptions.add(exception);
      return this;
    }

    @SuppressWarnings("unchecked")
    private boolean fails(Function<?, ?> func, Object value) {
      try {
        this.apply((Function<Object, Object>) func, value);
        return false;
      } catch (Throwable t) {
        throwIfBlacklisted(t);
        return true;
      }
    }

    @SuppressWarnings("unchecked")
    private boolean fails(Predicate<?> p, Object value) {
      try {
        this.test((Predicate<Object>) p, value);
        return false;
      } catch (Throwable t) {
        throwIfBlacklisted(t);
        return true;
      }
    }

    /**
     * During the assertion process invoked by {@code perform} method, {@code apply}
     * method of {@code Function}s held by the matcher to be performed must not
     * be called directly but through this method.
     *
     * @param func  A function to be applied.
     * @param value A value given to {@code func}.
     * @param <I>   A type of {@code value} given to {@code func}.
     * @param <O>   A type of {@code value} returned from {@code func}
     * @return Result of {@code func} with {@code value}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <I, O> O apply(Function<I, O> func, I value) {
      Object ret = null;
      try {
        if (func instanceof ChainedFunction) {
          ChainedFunction<I, O> cf = (ChainedFunction<I, O>) func;
          if (cf.previous() != null) {
            ret = this.apply((Function<Object, Object>) cf.chained(), apply((Function<Object, Object>) cf.previous(), value));
            return (O) ret;
          }
        }
        ret = memoizedFunction(func).apply(value);
        return (O) ret;
      } catch (Throwable e) {
        ret = e;
        throw rethrow(e);
      } finally {
        snapshot(ret, func, value);
      }
    }

    /**
     * During the assertion process invoked by {@code perform} method, {@code test}
     * method of {@code Predicate}s held by the matcher to be performed must not
     * be called directly but through this method.
     *
     * @param pred  A predicate to be applied.
     * @param value A value given to {@code pred}.
     * @param <I>   A type of {@code value} given to {@code pred}.
     * @return Result of {@code pred} with {@code value}.
     */
    @Override
    public <I> boolean test(Predicate<I> pred, I value) {
      Object ret = null;
      try {
        if (pred instanceof TransformingPredicate) {
          ret = applyAndTest((TransformingPredicate<Object, Object>) pred, value);
          return (boolean) ret;
        }
        ret = memoizedPredicate(pred).test(value);
        return (boolean) ret;
      } catch (Throwable e) {
        ret = e;
        throw rethrow(e);
      } finally {
        snapshot(ret, pred, value);
      }
    }

    public <I> Object applyAndTest(TransformingPredicate<Object, Object> pred, I value) {
      @SuppressWarnings("unchecked") Function<Object, Object> func = (Function<Object, Object>) pred.function();
      return test(pred.predicate(), apply(func, value));
    }

    @Override
    public Session<T> matched(boolean b) {
      this.result = b;
      return this;
    }

    @SuppressWarnings("unchecked")
    private <I, O> Function<I, O> memoizedFunction(Function<I, O> function) {
      return (Function<I, O>) memoizationMapForFunctions.computeIfAbsent(function, this::memoize);
    }

    @SuppressWarnings("unchecked")
    private <I> Predicate<I> memoizedPredicate(Predicate<I> p) {
      return (Predicate<I>) memoizationMapForPredicates.computeIfAbsent(p, this::memoize);
    }

    private <I, O> Function<I, O> memoize(Function<I, O> function) {
      Map<I, Supplier<O>> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i,
          (I j) -> {
            try {
              O result = function.apply(j);
              return () -> result;
            } catch (RuntimeException | Error e) {
              throwIfBlacklisted(e);
              return () -> {
                if (e instanceof RuntimeException)
                  throw (RuntimeException) e;
                throw (Error) e;
              };
            }
          }
      ).get();
    }

    private <I> Predicate<I> memoize(Predicate<I> predicate) {
      Map<I, BooleanSupplier> memo = new HashMap<>();
      return (I i) -> memo.computeIfAbsent(i,
          (I j) -> {
            try {
              boolean result = predicate.test(j);
              return () -> result;
            } catch (RuntimeException | Error e) {
              throwIfBlacklisted(e);
              return () -> {
                if (e instanceof RuntimeException)
                  throw (RuntimeException) e;
                throw (Error) e;
              };
            }
          }
      ).getAsBoolean();
    }

    private void explainMatcherPredicate(T value, Function<T, ?> func, Predicate<?> p) {
      if (p instanceof TransformingPredicate && !fails(func, value)) {
        explainTransformingPredicate(value, func, (TransformingPredicate<?, ?>) p);
      } else {
        explainFunction(value, func, VARIABLE_NAME, this.mismatchWriter, -3);
      }
    }

    private void explainTransformingPredicate(T value, Function<T, ?> func, TransformingPredicate<?, ?> p) {
      this.mismatchWriter
          .enter()
          .appendLine("%s=%s%s", TRANSFORMED_VARIABLE_NAME, VARIABLE_NAME, func);
      try {
        explainFunction(value, func, VARIABLE_NAME, this.mismatchWriter);
      } finally {
        this.mismatchWriter.leave();
      }
      this.mismatchWriter
          .enter()
          .appendLine("%s%s %s", TRANSFORMED_VARIABLE_NAME, p.function(), p.predicate())
          .leave();
      //noinspection unchecked
      explainFunction((T) apply(func, value), (Function<T, ?>) p.function(), TRANSFORMED_VARIABLE_NAME, this.mismatchWriter);
    }

    private void explainFunction(T value, Function<T, ?> func, String variableName, Impl.Writer writer) {
      explainFunction(value, func, variableName, writer, -1);
    }

    @SuppressWarnings("unchecked")
    private void explainFunction(T value, Function<T, ?> func, String variableName, Impl.Writer writer, int adjustment) {
      if (func instanceof ChainedFunction) {
        explainChainedFunction(value, (ChainedFunction<Object, Object>) func, variableName, writer, adjustment);
      } else {
        if (!(func instanceof TrivialFunction)) {
          writer.enter();
          try {
            writer.appendLine("%s%s=%s", variableName, func, this.snapshotOf(func, value));
          } finally {
            writer.leave();
          }
        }
      }
    }

    @SuppressWarnings("unchecked")
    private <I, O> void explainChainedFunction(I value, ChainedFunction<I, O> chained, String variableName, Impl.Writer writer, int adjustment) {
      writer.enter();
      try {
        class Entry {
          private final String formattedFunctionName;
          private final String snapshot;

          private Entry(String funcName, String snapshot) {
            this.formattedFunctionName = funcName;
            this.snapshot = snapshot;
          }
        }
        List<Entry> workEntries = new LinkedList<>();
        for (ChainedFunction<Object, Object> c = (ChainedFunction<Object, Object>) chained;
             c != null;
             c = (ChainedFunction<Object, Object>) c.previous()) {
          workEntries.add(0, new Entry(
              formatFunction(c, variableName),
              snapshotOf(c, value)
          ));
        }
        List<String> work = new LinkedList<>();
        String previousReplacement = "";
        for (Entry entry : workEntries) {
          String formattedFunctionName = entry.formattedFunctionName;
          String replacement = previousReplacement + spaces(formattedFunctionName.length() - previousReplacement.length() + adjustment);
          work.add(String.format(
              "%s+-%s%s",
              replacement,
              times('-', workEntries.get(workEntries.size() - 1).formattedFunctionName.length() - formattedFunctionName.length()),
              entry.snapshot
          ));
          previousReplacement = replacement + "|";
          work.add(previousReplacement);
        }
        Collections.reverse(work);
        work.forEach(e -> mismatchWriter.appendLine("%s", e));
      } finally {
        writer.leave();
      }
    }

    private void snapshot(Object out, Object funcOrPredicate, Object value) {
      List<Object> key = asList(funcOrPredicate, value);
      if (!snapshots.containsKey(key)) {
        if (out instanceof String || out instanceof Throwable) {
          snapshots.put(key, String.format("%s", summarizeValue(out)));
        } else {
          String simpleClassName = toSimpleClassName(out);
          if ("".equals(simpleClassName))
            snapshots.put(key, String.format("%s", summarizeValue(out)));
          else
            snapshots.put(key, String.format("%s:%s", summarizeValue(out), simpleClassName));
        }
      }
    }

    private <I> String snapshotOf(Object funcOrPred, I value) {
      return this.snapshots.get(asList(funcOrPred, value));
    }

    private static String formatExpectation(Predicate<?> p, Function<?, ?> function) {
      if (p instanceof TransformingPredicate) {
        TransformingPredicate<?, ?> pp = (TransformingPredicate<?, ?>) p;
        return String.format("(%s=%s%s)%s %s", TRANSFORMED_VARIABLE_NAME, VARIABLE_NAME, function, pp.function(), pp.predicate());
      } else
        return format("%s %s", formatFunction(function, VARIABLE_NAME), p);
    }
  }
}
