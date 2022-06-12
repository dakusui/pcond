package com.github.dakusui.pcond.provider;

import java.util.function.Function;

public interface ExceptionComposer {
  ReportComposer reportComposer();

  <T extends RuntimeException> T testSkippedException(String message);

  default <T extends RuntimeException> T testSkippedException(Explanation explanation) {
    return testSkippedException(explanation.toString());
  }

  <T extends Error> T testFailedException(String message);

  default <T extends Error> T testFailedException(Explanation explanation) {
    return testFailedException(explanation.toString());
  }

  @SuppressWarnings("unchecked")
  default <E extends RuntimeException> Function<String, E> preconditionViolationException() {
    return message -> (E) new PreconditionViolationException(message);
  }

  @SuppressWarnings("unchecked")
  default <E extends Exception> Function<String, E> postconditionViolationException() {
    return message -> (E) new PostconditionViolationException(message);
  }
}
