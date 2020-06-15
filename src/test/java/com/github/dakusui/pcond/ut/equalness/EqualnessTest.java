package com.github.dakusui.pcond.ut.equalness;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(Parameterized.class)
@FixMethodOrder(value = MethodSorters.JVM)
public class EqualnessTest extends TestBase {
  final Object f;
  final Object g;
  final Object another;

  @SuppressWarnings("unchecked")
  public EqualnessTest(Object arg1, Object arg2, Object arg3, Object arg4) {
    final Function<Object[], Object> targetFunctionFactory;
    final Object[] argsForTarget;
    final Function<Object[], Object> anotherFunctionFactory;
    final Object[] argsForAnother;
    targetFunctionFactory = (Function<Object[], Object>) arg1;
    argsForTarget = (Object[]) arg2;
    anotherFunctionFactory = (Function<Object[], Object>) arg3;
    argsForAnother = (Object[]) arg4;

    f = targetFunctionFactory.apply(argsForTarget);
    g = targetFunctionFactory.apply(argsForTarget);
    another = anotherFunctionFactory.apply(argsForAnother);
  }

  @Test
  public void print() {
    System.out.println("Test:");
    System.out.printf("  Target function (f):                                    %s%n", f);
    System.out.printf("  A function g that should return true when f.equals(g):  %s%n", g);
    System.out.printf("  A function g that should return false when f.equals(g): %s%n", another);
  }

  @Test
  public void returnsSameHashCode() {
    assertEquals(f.hashCode(), g.hashCode());
  }

  @Test
  public void equalsWithOneCreatedFromSameFactoryAndArguments() {
    assertEquals(f, g);
  }

  @Test
  public void equalsWithOneCreatedFromSameFactoryAndArguments_reversed() {
    assertEquals(g, f);
  }

  @Test
  public void equalsWithItself() {
    assertEquals(f, f);
  }

  @Test
  public void notEqualsWithAnother() {
    assertNotEquals(f, another);
  }

  @Parameterized.Parameters
  public static Object[][] parameters() {
    return new Object[][] {
        new Object[] { Utils.length(), args(), Utils.dummyFunctionFactory(), args() },
        new Object[] { Utils.elementAt(), args(1), Utils.dummyFunctionFactory(), args() },
        new Object[] { Utils.elementAt(), args(1), Utils.elementAt(), args(2) },
        new Object[] { Utils.stringifyAndThenIdentity(), args(), Utils.identityComposeStringify(), args() }, // This is desired to fail.
        new Object[] { Utils.custom(), args(), Utils.dummyFunctionFactory(), args() },
        new Object[] { Utils.raw(), args(), Utils.dummyFunctionFactory(), args() },
    };
  }

  private static Object[] args(Object... args) {
    return args;
  }

  enum Utils {
    ;

    @SuppressWarnings("RedundantCast")
    private static Function<Object[], Object> elementAt() {
      return args -> Functions.elementAt((int) args[0]);
    }

    private static Function<Object[], Object> length() {
      return args -> Functions.length();
    }

    private static Function<Object[], Object> stringifyAndThenIdentity() {
      return args -> Functions.stringify().andThen(Functions.identity());
    }

    private static Function<Object[], Object> identityComposeStringify() {
      return args -> Functions.identity().compose(Functions.stringify());
    }

    private static Function<Object[], Object> custom() {
      return args -> Printables.function("custom", Function.identity());
    }

    private static Function<Object[], Object> raw() {
      return args -> Function.identity();
    }

    @SuppressWarnings("Convert2Lambda")
    private static Function<Object[], Object> dummyFunctionFactory() {
      return args -> Printables.function("dummy",
          new Function<Object, Object>() {
            @Override
            public Object apply(Object o) {
              return o;
            }
          });
    }
  }
}
