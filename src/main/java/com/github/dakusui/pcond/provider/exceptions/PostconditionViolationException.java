package com.github.dakusui.pcond.provider.exceptions;

public class PostconditionViolationException extends RuntimeException {
  public PostconditionViolationException(String message) {
    super(message);
  }
}
