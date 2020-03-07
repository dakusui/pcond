package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Functions;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class PrintableFunctionTest {
  @Test
  public void testCompose() {
    Function<?, ?> f1 = Functions.identity().compose(Functions.identity());
    Function<?, ?> f2 = Functions.identity().compose(Functions.identity());
    Function<?, ?> g = Functions.identity().compose(Functions.stringify());
    Function<?, ?> h = Functions.stringify().compose(Functions.length());
    Function<?, ?> i = Functions.identity().andThen(Functions.identity());
    Object o = new Object();

    assertThat(
        f1,
        CoreMatchers.allOf(
            is(f1),
            is(f2),
            not(is(g)),
            not(is(h)),
            not(is(o))
        ));
  }

  @Test
  public void testAndThen() {
    Function<?, ?> f1 = Functions.identity().andThen(Functions.identity());
    Function<?, ?> f2 = Functions.identity().andThen(Functions.identity());
    Function<?, ?> g = Functions.identity().andThen(Functions.stringify());
    Function<?, ?> h = Functions.stringify().andThen(Functions.length());
    Function<?, ?> i = Functions.identity().compose(Functions.identity());
    Object o = new Object();

    assertThat(
        f1,
        CoreMatchers.allOf(
            is(f1),
            is(f2),
            not(is(g)),
            not(is(h)),
            not(is(i)),
            not(is(o))
        ));
  }
}
