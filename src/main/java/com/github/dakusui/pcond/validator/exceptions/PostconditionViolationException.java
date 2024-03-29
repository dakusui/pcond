package com.github.dakusui.pcond.validator.exceptions;

/**
 * An exception intended to be used, when a post-condition is not satisfied.
 */
public class PostconditionViolationException extends RuntimeException {
  public PostconditionViolationException(String message) {
    super(message);
  }
}
