package com.github.dakusui.pcond.utils;

import org.junit.After;
import org.junit.Before;

public abstract class TestBase {
  @Before
  public void before() {
    TestUtils.suppressStdOutErrIfRunUnderSurefire();
  }

  @After
  public void after() {
    TestUtils.restoreStdOutErr();
  }
}
