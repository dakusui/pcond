package com.github.dakusui.crest.ut;

import org.junit.Ignore;
import org.junit.Test;

import static com.github.dakusui.crest.Crest.*;

@Ignore
public class NotMatcherTest {
  @Test
  public void test() {
    assertThat(
        1,
        not(asInteger()
            .equalTo(1)
            .$()));
  }

  @Test
  public void test2() {
    assertThat(
        1,
        asInteger()
            .equalTo(2)
            .$());
  }

  @Test
  public void test3() {
    assertThat(
        1,
        allOf(
            asInteger().equalTo(2).$()));
  }
}
