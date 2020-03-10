package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Functions;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class PrintablesFunctionTest {
  public static class Simple {
    @Test
    public void test() {
      Function<?, ?> f = Functions.identity();
      Function<?, ?> ff = Functions.identity();
      Function<?, ?> g = Functions.stringify();
      Object n = null;
      Object o = new Object();
      assertThat(
          f,
          allOf(
              is(f),
              is(ff),
              not(is(g)),
              not(is(n)),
              not(is(o))));
    }
  }

  public static class Parameterized {
    @Test
    public void test() {
      Function<?, ?> f = Functions.elementAt(0);
      Function<?, ?> ff = Functions.elementAt(0);
      Function<?, ?> g = Functions.cast(String.class);
      Object n = null;
      Object o = new Object();
      assertThat(
          f,
          allOf(
              is(f),
              is(ff),
              not(is(g)),
              not(is(n)),
              not(is(o))));
    }
  }

  public static class Composed {
    @Test
    public void testCompose() {
      Function<?, ?> f1 = Functions.identity().compose(Functions.identity());
      Function<?, ?> f2 = Functions.identity().compose(Functions.identity());
      Function<?, ?> g = Functions.identity().compose(Functions.stringify());
      Function<?, ?> h = Functions.stringify().compose(Functions.length());
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

    @Test
    public void testComposeParameterized() {
      Function<?, ?> f1 = Functions.identity().compose(Functions.elementAt(0));
      Function<?, ?> f2 = Functions.identity().compose(Functions.elementAt(0));
      Function<?, ?> g = Functions.identity().compose(Functions.cast(String.class));
      Function<?, ?> h = Functions.cast(String.class).compose(Functions.length());
      Object n = null;
      Object o = new Object();

      assertThat(
          f1,
          CoreMatchers.allOf(
              is(f1),
              is(f2),
              not(is(g)),
              not(is(h)),
              not(is(n)),
              not(is(o))
          ));
    }
  }
}