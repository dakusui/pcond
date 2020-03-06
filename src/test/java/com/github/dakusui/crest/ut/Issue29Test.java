package com.github.dakusui.crest.ut;

import com.github.dakusui.crest.core.ExecutionFailure;
import com.github.dakusui.crest.utils.TestBase;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.pcond.functions.Functions.THIS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class Issue29Test extends TestBase {
  @Test
  public void useStrict() {
    assertThat(
        "HELLO",
        allOf(
            asChar("charAt", arg(int.class, 1)).equalTo('E').$(),
            asString("substring", arg(int.class, 1), arg(int.class, 3)).equalTo("EL").$()
        )
    );
  }

  @SuppressWarnings("UnnecessaryBoxing")
  @Test(expected = IOException.class)
  public void useStrict2() throws IOException {
    try {
      assertThat(
          "HELLO",
          asString("substring", arg(int.class, 1), arg(Number.class, Integer.valueOf(3))).equalTo("EL").$()
      );
    } catch (ExecutionFailure e) {
      assertEquals(
          "Method matching 'substring[int 1, Number 3]' was not found by selector=default&&preferNarrower&&preferExact in java.lang.String.",
          e.getCause().getMessage()
      );
      throw new IOException();
    }
  }

  @Test
  public void useStrictForStaticMethodWithVarArgs() {
    assertThat(
        "HELLO",
        asString(
            call(String.class, "format", "%s WORLD %s", args(String.class, "hello", "!")).$())
            .equalTo("hello WORLD !")
            .$());
  }

  @Test
  public void useTHISkeyword() {
    assertThat(
        "HELLO",
        asBoolean(
            call("equals",
                arg(Object.class, THIS)
            ).$())
            .equalTo(true)
            .$());
  }

  @Test
  public void useStrictForStaticMethodWithVarArgsWithTHISobject() {
    assertThat(
        "HELLO",
        asString(
            call(String.class, "format", "%s %s WORLD %s",
                args(Object.class, "hello", THIS, "!")).$())
            .equalTo("hello HELLO WORLD !")
            .$());
  }

  @Test
  public void useStrictForOverloadedMethod() {
    try {
      assertThat(
          new OverloadedMethods(),
          allOf(
              asString(call("overloaded", arg(String.class, "value")).$())
                  .equalTo("str:value")
                  .$(),
              asString(call("overloaded", arg(Integer.class, 123)).$())
                  .equalTo("Integer:123")
                  .$(),
              asString(call("overloaded", arg(int.class, 123)).$())
                  .equalTo("int:123")
                  .$(),
              asString(call("overloaded", arg(Object.class, "value")).$())
                  .equalTo("obj:value")
                  .$()
          ));
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void useStrictForOverloadedMethodWithPrimitive() {
    try {
      assertThat(
          new OverloadedMethods(),
          asString(call("overloaded", arg(int.class, 123)).$())
              .equalTo("int:123")
              .$());
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void useNormalForOverMethod() {
    try {
      OverloadedMethods overloaded = new OverloadedMethods();
      Stream.of(
          asList("int", overloaded.overloaded(123)),
          asList("Integer", overloaded.overloaded(new Integer(123))),
          asList("String", overloaded.overloaded("value")),
          asList("Object", overloaded.overloaded(new Object() {
            @Override
            public String toString() {
              return "value";
            }
          }))
      ).forEach(
          System.out::println
      );
      assertThat(
          overloaded,
          asString(call("overloaded", "value").$())
              .equalTo("str:value")
              .$()
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  public static class OverloadedMethods {
    public String overloaded(String value) {
      return String.format("str:%s", value);
    }

    public String overloaded(Integer value) {
      return String.format("Integer:%s", value);
    }

    public String overloaded(int value) {
      return String.format("int:%s", value);
    }

    public String overloaded(Object value) {
      return String.format("obj:%s", value);
    }
  }

  private static boolean isWider(Method a, Method b) {
    if (Objects.equals(a, b))
      return false;
    for (int i = 0; i < a.getParameterCount(); i++)
      if (a.getParameterTypes()[i].isAssignableFrom(b.getParameterTypes()[i]))
        return false;
    return true;
  }
}
