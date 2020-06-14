package com.github.dakusui.pcond.ut.equalness;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Printables;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EqualnessTest {
  Function<Object[], Function<?, ?>> DUMMY_FUNCTION = dummyFunctionFactory();

  @SuppressWarnings("Convert2Lambda")
  private static Function<Object[], Function<?, ?>> dummyFunctionFactory() {
    return args -> Printables.function("dummy",
        new Function<Object, Object>() {
          @Override
          public Object apply(Object o) {
            return o;
          }
        });
  }

  private Function<Object[], Function<?, ?>> targetFunctionFactory() {
    return args -> Functions.length();
  }


  @Test
  public void test() {
    Function<?, ?> f = targetFunctionFactory().apply(new Object[] {});
    Function<?, ?> g = targetFunctionFactory().apply(new Object[] {});

    assertNotEquals(f, DUMMY_FUNCTION);
    assertEquals(f, g);
    assertEquals(f.hashCode(), g.hashCode());
  }


}
