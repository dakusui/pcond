package com.github.dakusui.pcond.provider;

public class ApplicationException extends RuntimeException {
  public ApplicationException(String message) {
    this(message, null);
  }

  public ApplicationException(String message, Throwable cause) {
    super(message, cause);
  }
}
