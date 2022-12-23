package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.core.ValueHolder.State;

import static com.github.dakusui.pcond.core.EvaluationEntry.Type.FUNCTION;
import static com.github.dakusui.pcond.core.EvaluationEntry.Type.LEAF;
import static com.github.dakusui.pcond.core.Evaluator.Explainable.*;
import static com.github.dakusui.pcond.core.Evaluator.Impl.EVALUATION_SKIPPED;
import static com.github.dakusui.pcond.core.Evaluator.Snapshottable.toSnapshotIfPossible;
import static com.github.dakusui.pcond.core.ValueHolder.State.VALUE_RETURNED;
import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * A class to hold an entry of execution history of the {@link Evaluator}.
 * When an evaluator enters into one {@link Evaluable} (actually a predicate or a function),
 * an {@code OnGoing} entry is created and held by the evaluator as a current
 * one.
 * Since one evaluate can have its children and only one child can be evaluated at once,
 * on-going entries are held as a list (stack).
 *
 * When the evaluator leaves the evaluable, the entry is "finalized".
 * From the data held by an entry, "expectation" and "actual behavior" reports are generated.
 *
 * .Evaluation Summary Format
 * ----
 * +----------------------------------------------------------------------------- Failure Detail Index
 * |  +-------------------------------------------------------------------------- Input
 * |  |                                            +----------------------------- Form (Function/Predicate)
 * |  |                                            |                           +- Output
 * |  |                                            |                           |
 * V  V                                            V                           V
 * Book:[title:<De Bello G...i appellantur.>]->check:allOf               ->false
 * transform:title       ->"De Bello Gallico"
 * "De Bello Gallico"                        ->    check:allOf           ->false
 * isNotNull         ->true
 * [0]                                                     transform:parseInt->NumberFormatException:"For input s...ico""
 * null                                      ->        check:allOf       ->false
 * >=[10]        ->true
 * <[40]         ->true
 * Book:[title:<De Bello G...i appellantur.>]->    transform:title       ->"Gallia est omnis divis...li appellantur."
 * "Gallia est omnis divis...li appellantur."->    check:allOf           ->false
 * isNotNull         ->true
 * transform:length  ->145
 * 145                                       ->        check:allOf       ->false
 * [1]                                                         >=[200]       ->true
 * <[400]        ->true
 * ----
 *
 * Failure Detail Index::
 * In the full format of a failure report, detailed descriptions of mismatching forms are provided if the form is {@link Evaluator.Explainable}.
 * This index points an item in the detail part of the full report.
 * Input::
 * Values given to forms are printed here.
 * If the previous line uses the same value, the value will not be printed.
 * Form (Function/Predicate)::
 * This part displays names of forms (predicates and functions).
 * If a form is marked trivial, the framework may merge the form with the next line.
 * Output::
 * For predicates, expected boolean value is printed.
 * For functions, if a function does not throw an exception during its evaluation, the result will be printed here both for expectation and actual behavior summary.
 * If it throws an exception, the exception will be printed here in actual behavior summary.
 */
public abstract class EvaluationEntry {
  private final Type   type;
  /**
   * A name of a form (evaluable; function, predicate)
   */
  private final String formName;

  int level;

  Object inputExpectation;
  Object detailInputExpectation;

  Object inputActualValue;
  Object detailInputActualValue;

  Object outputExpectation;
  Object detailOutputExpectation;


  /**
   * A flag to let the framework know this entry should be printed in a less outstanding form.
   */
  final boolean squashable;

  EvaluationEntry(String formName, Type type, int level, Object inputExpectation_, Object detailInputExpectation_, Object outputExpectation, Object detailOutputExpectation, Object inputActualValue, Object detailInputActualValue, boolean squashable) {
    this.type = type;
    this.level = level;
    this.formName = formName;
    this.inputExpectation = inputExpectation_;
    this.detailInputExpectation = detailInputExpectation_;
    this.outputExpectation = outputExpectation;
    this.detailOutputExpectation = detailOutputExpectation;
    this.inputActualValue = inputActualValue;
    this.detailInputActualValue = detailInputActualValue;
    this.squashable = squashable;
  }

  static String composeDetailOutputActualValueFromInputAndThrowable(Object input, Throwable throwable) {
    StringBuilder b = new StringBuilder();
    b.append("Input: '").append(input).append("'").append(format("%n"));
    b.append("Input Type: ").append(input == null ? "(null)" : input.getClass().getName()).append(format("%n"));
    b.append("Thrown Exception: '").append(throwable.getClass().getName()).append("'").append(format("%n"));
    b.append("Exception Message: ").append(throwable.getMessage()).append(format("%n"));
    for (StackTraceElement each : throwable.getStackTrace()) {
      b.append("\t");
      b.append(each);
      b.append(format("%n"));
    }
    return b.toString();
  }

  static <T, E extends Evaluable<T>> Object computeInputActualValue(EvaluableIo<T, E, ?> evaluableIo) {
    return evaluableIo.input().value();
  }

  static <T, E extends Evaluable<T>> Object computeOutputExpectation(EvaluableIo<T, E, ?> evaluableIo, boolean expectationFlipped) {
    final State state = evaluableIo.output().state();
    if (state == VALUE_RETURNED) {
      if (evaluableIo.evaluableType() == FUNCTION)
        return toSnapshotIfPossible(evaluableIo.output().returnedValue());
      return !expectationFlipped;
    } else if (state == State.EXCEPTION_THROWN || state == State.EVALUATION_SKIPPED)
      return EVALUATION_SKIPPED;
    else
      throw new AssertionError("output state=<" + state + ">");
  }

  static <T, E extends Evaluable<T>> Object computeOutputActualValue(EvaluableIo<T, E, ?> evaluableIo) {
    if (evaluableIo.output().state() == State.VALUE_RETURNED)
      return toSnapshotIfPossible(evaluableIo.output().returnedValue());
    if (evaluableIo.output().state() == State.EXCEPTION_THROWN)
      return evaluableIo.output().thrownException();
    else
      return EVALUATION_SKIPPED;
    //throw new AssertionError();
  }

  static <T, E extends Evaluable<T>> boolean isExplanationRequired(Type evaluationEntryType, EvaluableIo<T, E, ?> evaluableIo, boolean expectationFlipped) {
    return asList(FUNCTION, LEAF).contains(evaluationEntryType) && (
        evaluableIo.output().state() == State.EXCEPTION_THROWN || (
            evaluableIo.evaluableType() == LEAF && returnedValueOrVoidIfSkipped(expectationFlipped, evaluableIo.output())));
  }

  private static <T, E extends Evaluable<T>> boolean returnedValueOrVoidIfSkipped(boolean expectationFlipped, ValueHolder<?> output) {
    if (output.state() == State.EVALUATION_SKIPPED)
      return false;
    return expectationFlipped ^ !(Boolean) output.returnedValue();
  }

  public String formName() {
    return formName;
  }

  public Type type() {
    return this.type;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isSquashable() {
    return this.squashable;
  }

  public abstract boolean requiresExplanation();

  public int level() {
    return level;
  }

  public Object inputExpectation() {
    return this.inputExpectation;
  }

  public Object detailInputExpectation() {
    return this.detailInputExpectation;
  }

  public Object outputExpectation() {
    return this.outputExpectation;
  }

  public Object detailOutputExpectation() {
    return this.detailOutputExpectation;
  }

  public Object inputActualValue() {
    return this.inputActualValue;
  }


  public abstract Object outputActualValue();

  public abstract Object detailOutputActualValue();

  public enum Type {
    TRANSFORM_AND_CHECK {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "transformAndCheck";
      }
    },
    TRANSFORM {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "transform";
      }
    },
    CHECK {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "check:" + evaluable.toString();
      }
    },
    AND {
      @Override
      String formName(Evaluable<?> evaluable) {
        return ((Evaluable.Conjunction<?>) evaluable).shortcut() ? "and" : "allOf";
      }
    },
    OR {
      @Override
      String formName(Evaluable<?> evaluable) {
        return ((Evaluable.Disjunction<?>) evaluable).shortcut() ? "or" : "anyOf";
      }
    },
    NOT {
      @Override
      String formName(Evaluable<?> evaluable) {
        return "not";
      }
    },
    LEAF {
      @Override
      String formName(Evaluable<?> evaluable) {
        return evaluable.toString();
      }
    },
    FUNCTION {
      @Override
      String formName(Evaluable<?> evaluable) {
        return ((Evaluable.Func<?>) evaluable).head().toString();
      }
    };

    abstract String formName(Evaluable<?> evaluable);
  }

  static class Finalized extends EvaluationEntry {
    final         Object  outputActualValue;
    final         Object  detailOutputActualValue;
    private final boolean requiresExplanation;

    Finalized(
        String formName,
        Type type,
        int level,
        Object inputExpectation_, Object detailInputExpectation_,
        Object outputExpectation, Object detailOutputExpectation,
        Object inputActualValue, Object detailInputActualValue,
        Object outputActualValue, Object detailOutputActualValue,
        boolean squashable, boolean requiresExplanation) {
      super(
          formName, type, level,
          inputExpectation_, detailInputExpectation_,
          outputExpectation, detailOutputExpectation,
          inputActualValue, detailInputActualValue, squashable);
      this.outputActualValue = outputActualValue;
      this.detailOutputActualValue = detailOutputActualValue;
      this.requiresExplanation = requiresExplanation;
    }

    @Override
    public Object outputActualValue() {
      return outputActualValue;
    }

    @Override
    public Object detailOutputActualValue() {
      return this.detailOutputActualValue;
    }

    @Override
    public boolean requiresExplanation() {
      return this.requiresExplanation;
    }
  }

  public static EvaluationEntry create(
      String formName, Type type,
      int level,
      Object inputExpectation_, Object detailInputExpectation_,
      Object outputExpectation, Object detailOutputExpectation,
      Object inputActualValue, Object detailInputActualValue,
      Object outputActualValue, Object detailOutputActualValue,
      boolean trivial, boolean requiresExplanation) {
    return new Finalized(
        formName, type,
        level,
        inputExpectation_, detailInputExpectation_,
        outputExpectation, detailOutputExpectation,
        inputActualValue, detailInputActualValue,
        outputActualValue, detailOutputActualValue,
        trivial, requiresExplanation
    );
  }

  public static class Impl extends EvaluationEntry {

    private final EvaluableIo<?, ?, ?> evaluableIo;
    private final boolean              expectationFlipped;

    private boolean finalized = false;
    private Object  outputActualValue;
    private Object  detailOutputActualValue;

    <T, E extends Evaluable<T>> Impl(
        EvaluationContext<T> evaluationContext,
        EvaluableIo<T, E, ?> evaluableIo) {
      super(
          evaluableIo.evaluableType().formName(evaluableIo.evaluable()),
          evaluableIo.evaluableType(),
          evaluationContext.visitorLineage.size(),
          computeInputExpectation(evaluableIo),                   // inputExpectation        == inputActualValue
          explainInputExpectation(evaluableIo),                   // detailInputExpectation  == detailInputActualValue
          null, // not necessary                                  // outputExpectation
          explainOutputExpectation(evaluableIo.evaluable()),      // detailOutputExpectation
          computeInputActualValue(evaluableIo),                   // inputActualValue
          explainInputActualValue(evaluableIo.evaluable(), computeInputActualValue(evaluableIo)), // detailInputActualValue
          evaluableIo.evaluable().isSquashable());
      this.evaluableIo = evaluableIo;
      this.expectationFlipped = evaluationContext.isExpectationFlipped();
    }

    private static <E extends Evaluable<T>, T> Object explainInputExpectation(EvaluableIo<T, E, ?> evaluableIo) {
      return explainInputActualValue(evaluableIo, computeInputExpectation(evaluableIo));
    }

    private static <E extends Evaluable<T>, T> Object computeInputExpectation(EvaluableIo<T, E, ?> evaluableIo) {
      return computeInputActualValue(evaluableIo);
    }

    @Override
    public boolean requiresExplanation() {
      return isExplanationRequired(evaluableIo().evaluableType(), evaluableIo(), this.expectationFlipped);
    }

    @SuppressWarnings("unchecked")
    private <I, O> EvaluableIo<I, Evaluable<I>, O> evaluableIo() {
      return (EvaluableIo<I, Evaluable<I>, O>) this.evaluableIo;
    }

    public Object outputExpectation() {
      assert finalized;
      return outputExpectation;
    }

    @Override
    public Object outputActualValue() {
      assert finalized;
      return outputActualValue;
    }

    @Override
    public Object detailOutputActualValue() {
      assert finalized;
      return detailOutputActualValue;
    }

    public void finalizeValues() {
      this.outputExpectation = computeOutputExpectation(evaluableIo(), expectationFlipped);
      this.outputActualValue = computeOutputActualValue(evaluableIo());
      this.detailOutputActualValue = explainActual(evaluableIo());
      this.finalized = true;
    }
  }
}
