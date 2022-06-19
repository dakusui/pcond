package com.github.dakusui.pcond.provider.exceptions;

public class PreconditionViolationException extends RuntimeException {
  public PreconditionViolationException(String message) {
    super(message);
  }
}
