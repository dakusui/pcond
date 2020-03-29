package com.github.dakusui.pcond.provider;

public class PreconditionViolationException extends RuntimeException {
  public PreconditionViolationException(String message) {
    super(message);
  }
}
