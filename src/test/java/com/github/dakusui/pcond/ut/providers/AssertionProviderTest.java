package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.core.AssertionProvider;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class AssertionProviderTest extends TestBase {
  @Test
  public void testAssertionEnabled() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(true);
    assertThat(
        assertionProvider,
        instanceOf(AssertionProvider.Default.class));
    assertThat(
        assertionProvider.shouldStub(),
        is(false)
    );
  }

  @Test
  public void testAssertionDisabled() {
    AssertionProvider assertionProvider = AssertionProvider.createAssertionProvider(false);
    assertThat(
        assertionProvider,
        instanceOf(AssertionProvider.Passthrough.class));
    assertThat(
        assertionProvider.shouldStub(),
        is(true)
    );
  }
}
