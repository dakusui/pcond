package com.github.dakusui.pcond.provider;

public class PostconditionViolationException extends RuntimeException {
  public PostconditionViolationException(String message) {
    super(message);
  }
}
