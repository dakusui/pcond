package com.github.dakusui.shared.utils.ut;

import org.junit.BeforeClass;

import static com.github.dakusui.pcond.internals.InternalUtils.assertFailsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

/**
 * Created by hiroshi.ukai on 8/26/17.
 */
public class TestBase extends com.github.dakusui.shared.utils.TestBase {
  public static class ForAssertionEnabledVM extends TestBase {
    @BeforeClass
    public static void setUpBeforeAll() {
      assumeThat(assertFailsWith(false), is(true));
    }
  }
}
