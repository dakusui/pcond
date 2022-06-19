package com.github.dakusui.pcond.valuechecker.exceptions;

public class PostconditionViolationException extends RuntimeException {
  public PostconditionViolationException(String message) {
    super(message);
  }
}
