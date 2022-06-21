package com.github.dakusui.pcond.valuechecker.exceptions;

/**
 * An exception intended to be used, when a pre-condition is not satisfied.
 */
public class PreconditionViolationException extends RuntimeException {
  public PreconditionViolationException(String message) {
    super(message);
  }
}
