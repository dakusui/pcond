package com.github.dakusui.pcond.core;

public class Io {
  private final Object  inputExpectation;
  private final Object  outputExpectation;
  private final Object  inputActualValue;
  private final Object  outputActualValue;
  private final boolean unexpected;

  public Io(Object inputExpectation, Object outputExpectation, Object inputActualValue, Object outputActualValue, boolean unexpected) {
    this.inputExpectation = inputExpectation;
    this.outputExpectation = outputExpectation;
    this.inputActualValue = inputActualValue;
    this.outputActualValue = outputActualValue;
    this.unexpected = unexpected;
  }

  public Object getInputExpectation() {
    return inputExpectation;
  }

  public Object getOutputExpectation() {
    return outputExpectation;
  }

  public Object getInputActualValue() {
    return inputActualValue;
  }

  public Object getOutputActualValue() {
    return outputActualValue;
  }

  public boolean isUnexpected() {
    return unexpected;
  }
}
