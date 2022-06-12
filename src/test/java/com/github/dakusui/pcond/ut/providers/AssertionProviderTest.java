package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.core.Configurations;
import com.github.dakusui.pcond.provider.AssertionProvider;
import com.github.dakusui.pcond.provider.impls.JUnit4AssertionProvider;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.util.Properties;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

public class AssertionProviderTest extends TestBase {
  public static class TestAssertionProvider implements AssertionProvider {
    private final Configuration configuration = new Configuration() {
    };

    public TestAssertionProvider(Properties properties) {
    }

    @Override
    public Configuration configuration() {
      return this.configuration;
    }

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
    public <T, E extends Exception> T require(T value, Predicate<? super T> cond) throws E {
      return null;
    }

    @Override
    public <T, E extends Exception> T require(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
      return null;
    }

    @Override
    public <T, E extends Exception> T validate(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
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
    public <T, E extends Exception> T ensure(T value, Predicate<? super T> cond) throws E {
      return null;
    }

    @Override
    public <T, E extends Exception> T ensure(T value, Predicate<? super T> cond, Function<String, E> exceptionComposer) throws E {
      return null;
    }

    @Override
    public <T> void checkInvariant(T value, Predicate<? super T> cond) {
    }

    @Override
    public <T> void checkPrecondition(T value, Predicate<? super T> cond) {
    }

    @Override
    public <T> void checkPostcondition(T value, Predicate<? super T> cond) {
    }

    @Override
    public <T> void assertThat(T value, Predicate<? super T> cond) {
    }

    @Override
    public <T> void assumeThat(T value, Predicate<? super T> cond) {
    }
  }

  @Test
  public void testAssertionEnabled() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(new Properties());
    assertThat(
        assertionProvider,
        instanceOf(JUnit4AssertionProvider.class));
  }

  @Test
  public void test() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(new Properties() {{
      put(AssertionProvider.class.getCanonicalName(), TestAssertionProvider.class.getName());
    }});
    assertThat(
        assertionProvider,
        instanceOf(TestAssertionProvider.class)
    );
  }

  @Test
  public void test2() {
    System.out.println(TestAssertionProvider.class.getName());
    System.setProperty("com.github.dakusui.pcond.provider.AssertionProvider", "com.github.dakusui.pcond.ut.providers.AssertionProviderTest$TestAssertionProvider");
    System.out.println("-->" + AssertionProvider.INSTANCE.getClass().getCanonicalName());
  }

  @Test(expected = IllegalStateException.class)
  public void test3() {
    Configurations.initializeWith(TestAssertionProvider.class);
    System.out.println(AssertionProvider.INSTANCE.getClass().getCanonicalName());
    Configurations.initializeWith(JUnit4AssertionProvider.class);
  }
}
