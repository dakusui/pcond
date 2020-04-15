package com.github.dakusui.pcond.ut.providers;

import java.io.IOException;

public class Example {
  private static final Hello<?> INSTANCE = new Hello<Throwable>() {
    @Override
    public void hello() throws Throwable {
      throw new IOException("hello");
    }
  };

  public interface Hello<E extends Throwable> {
    void hello() throws E;
  }

  public static <E extends Throwable> void runHello() throws E {
    ((Hello<E>) INSTANCE).hello();
  }


  public static void main(String... args) {
    runHello();
  }
}
