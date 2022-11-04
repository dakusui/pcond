package com.github.dakusui.shared.utils;

import org.junit.After;
import org.junit.Before;

public abstract class TestBase {
  @Before
  public void before() {
    TestUtils.suppressStdOutErrIfUnderPitestOrSurefire();
  }

  @After
  public void after() {
    TestUtils.restoreStdOutErr();
  }
}
