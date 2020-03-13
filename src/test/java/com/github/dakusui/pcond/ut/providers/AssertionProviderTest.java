package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.core.AssertionProvider;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.Properties;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class AssertionProviderTest extends TestBase {
  public static class TestAssertionProvider implements AssertionProvider {
    @Override
    public <T, E extends Throwable> T requireNonNull(T value) throws E {
      return null;
    }

    @Override
    public <T, E extends Throwable> T requireArgument(T value, Predicate<? super T> cond) throws E {
      return null;
    }

    @Override
    public <T, E extends Throwable> T requireState(T value, Predicate<? super T> cond) throws E {
      return null;
    }

    @Override
    public <T, E extends Throwable> T require(T value, Predicate<? super T> cond) throws E {
      return null;
    }

    @Override
    public <T, E extends Throwable> T ensureNonNull(T value) throws E {
      return null;
    }

    @Override
    public <T, E extends Throwable> T ensureState(T value, Predicate<? super T> cond) throws E {
      return null;
    }

    @Override
    public <T, E extends Throwable> T ensure(T value, Predicate<? super T> cond) throws E {
      return null;
    }
  }

  @Test
  public void testAssertionEnabled() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(System.getProperties(), true);
    assertThat(
        assertionProvider,
        instanceOf(AssertionProvider.Default.class));
  }

  @Test
  public void testAssertionDisabled() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(System.getProperties(), false);
    assertThat(
        assertionProvider,
        instanceOf(AssertionProvider.Passthrough.class));
  }

  @Test
  public void test() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(new Properties() {{
      put(AssertionProvider.class.getCanonicalName(), TestAssertionProvider.class.getName());
    }}, false);
    assertThat(
        assertionProvider,
        instanceOf(TestAssertionProvider.class)
    );
  }
}
