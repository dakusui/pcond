package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.provider.AssertionProvider;
import com.github.dakusui.pcond.provider.impls.DefaultAssertionProvider;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.util.Properties;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

public class AssertionProviderTest extends TestBase {
  public static class TestAssertionProvider implements AssertionProvider<RuntimeException> {
    @Override
    public <T> T requireNonNull(T value) {
      return null;
    }

    @Override
    public <T> T requireArgument(T value, Predicate<? super T> cond) {
      return null;
    }

    @Override
    public <T> T requireState(T value, Predicate<? super T> cond) {
      return null;
    }

    @Override
    public <T> T require(T value, Predicate<? super T> cond) {
      return null;
    }

    @Override
    public <T> T validate(T value, Predicate<? super T> cond) throws RuntimeException {
      return null;
    }

    @Override
    public <T> T ensureNonNull(T value) {
      return null;
    }

    @Override
    public <T> T ensureState(T value, Predicate<? super T> cond) {
      return null;
    }

    @Override
    public <T> T ensure(T value, Predicate<? super T> cond) {
      return null;
    }

    @Override
    public <T> boolean validation(T value, Predicate<? super T> cond) throws RuntimeException {
      return false;
    }

    @Override
    public <T> boolean that(T value, Predicate<? super T> cond) {
      return false;
    }

    @Override
    public <T> boolean precondition(T value, Predicate<? super T> cond) {
      return false;
    }

    @Override
    public <T> boolean postcondition(T value, Predicate<? super T> cond) {
      return false;
    }
  }

  @Test
  public void testAssertionEnabled() {
    AssertionProvider<?> assertionProvider = AssertionProvider.createAssertionProvider(System.getProperties());
    assertThat(
        assertionProvider,
        instanceOf(DefaultAssertionProvider.class));
  }

  @Test
  public void test() {
    AssertionProvider<?> assertionProvider = AssertionProvider.createAssertionProvider(new Properties() {{
      put(AssertionProvider.class.getCanonicalName(), TestAssertionProvider.class.getName());
    }});
    assertThat(
        assertionProvider,
        instanceOf(TestAssertionProvider.class)
    );
  }
}
