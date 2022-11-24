package com.github.dakusui.pcond.core;

/**
 * A class to hold an entry of execution history of the {@link Evaluator}.
 * When an evaluator enters into one {@link Evaluable} (actually a predicate or a function),
 * an {@link OnGoing} entry is created and held by the evaluator as a current
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

  private final int level;

  Object inputExpectation;
  Object detailInputExpectation;

  Object inputActualValue;
  Object detailInputActualValue;

  boolean outputExpectation;

  Object detailOutputExpectation;


  /**
   * A flag to let the framework know this entry should be printed in a less outstanding form.
   */
  final boolean trivial;

  EvaluationEntry(String formName, Type type, int level, boolean outputExpectation, Object detailOutputExpectation, Object inputActualValue, Object detailInputActualValue, boolean trivial) {
    this.type = type;
    this.level = level;
    this.formName = formName;
    this.inputExpectation = inputActualValue;
    this.detailInputExpectation = detailInputActualValue;
    this.outputExpectation = outputExpectation;
    this.detailOutputExpectation = detailOutputExpectation;
    this.inputActualValue = inputActualValue;
    this.detailInputActualValue = detailInputActualValue;
    this.trivial = trivial;
  }

  EvaluationEntry(EvaluationEntry base) {
    this.type = base.type();
    this.level = base.level();
    this.formName = base.formName();
    this.inputExpectation = base.inputExpectation();
    this.detailInputExpectation = base.detailInputExpectation();
    this.inputActualValue = base.inputActualValue();
    this.detailInputActualValue = base.detailInputActualValue();
    this.detailOutputExpectation = base.detailOutputExpectation();
    this.outputExpectation = base.outputExpectation();
    this.trivial = base.isTrivial();
  }

  public String formName() {
    return formName;
  }

  public Type type() {
    return this.type;
  }

  public abstract boolean evaluationFinished();

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public boolean isTrivial() {
    return this.trivial;
  }

  public int level() {
    return level;
  }

  public Object inputExpectation() {
    return this.inputExpectation;
  }

  public Object detailInputExpectation() {
    return this.detailInputExpectation;
  }

  public boolean outputExpectation() {
    return this.outputExpectation;
  }

  public Object detailOutputExpectation() {
    return this.detailOutputExpectation;
  }

  public Object inputActualValue() {
    return this.inputActualValue;
  }

  final public Object detailInputActualValue() {
    return this.detailInputActualValue;
  }

  public abstract Object outputActualValue();

  public abstract Object detailOutputActualValue();

  public enum Type {
    TRANSFORM,
    CHECK,
    AND,
    OR,
    NOT,
    LEAF,
    FUNCTION,
    ;
  }

  static class Finalized extends EvaluationEntry {
    final Object outputActualValue;
    final Object detailOutputActualValue;

    Finalized(
        String formName, Type type,
        int level,
        boolean outputExpectation, Object detailOutputExpectation,
        Object inputActualValue, Object detailInputActualValue,
        Object outputActualValue, Object detailOutputActualValue,
        boolean trivial) {
      super(formName, type, level, outputExpectation, detailOutputExpectation, inputActualValue, detailInputActualValue, trivial);
      this.outputActualValue = outputActualValue;
      this.detailOutputActualValue = detailOutputActualValue;
    }

    Finalized(OnGoing onGoing, Object detailOutputExpectation, Object detailInputActualValue, Object outputActualValue, Object detailOutputActualValue) {
      super(onGoing);
      this.detailOutputExpectation = detailOutputExpectation;
      this.detailInputActualValue = detailInputActualValue;
      this.outputActualValue = outputActualValue;
      this.detailOutputActualValue = detailOutputActualValue;
    }

    @Override
    public boolean evaluationFinished() {
      return true;
    }

    @Override
    public Object outputActualValue() {
      return outputActualValue;
    }

    @Override
    public Object detailOutputActualValue() {
      return this.detailOutputActualValue;
    }
  }

  static class OnGoing extends EvaluationEntry {
    final int positionInEntries;

    OnGoing(String formName, Type type, int level, boolean outputExpectation, Object inputActualValue, Object detailInputActualValue1, boolean trivial, int positionInEntries) {
      super(formName, type, level, outputExpectation, outputExpectation, inputActualValue, detailInputActualValue1, trivial);
      this.positionInEntries = positionInEntries;
    }

    Finalized finalizeEntry(Object detailOutputExpectation, Object detailInputActualValue, Object outputActualValue, Object detailOutputActualValue) {
      return new Finalized(this, detailOutputExpectation, detailInputActualValue, outputActualValue, detailOutputActualValue);
    }

    @Override
    public boolean evaluationFinished() {
      return false;
    }

    @Override
    public Object outputActualValue() {
      throw new UnsupportedOperationException();
    }

    @Override
    public Object detailOutputActualValue() {
      throw new UnsupportedOperationException();
    }
  }
}
