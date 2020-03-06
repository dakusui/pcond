package com.github.dakusui.crest.examples;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.data.Offset;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InAssertJ {

  @Test
  public void test() {
    List<String> list = Arrays.asList("hoge", "fuga", "piyo");

    SoftAssertions softly = new SoftAssertions();
    softly.assertThat(true)
        .isTrue();
    softly.assertThat("hoge")
        .isEqualTo("hoge");
    softly.assertThat(list)
        .isNotEmpty()
        .contains("hoge", "fuga", "piyo", "poyo");

    softly.assertAll();
  }

  @Test
  public void closeTo() {
    assertThat(100.1)
        .isCloseTo(100, Offset.offset(0.05))
        .isCloseTo(100, Offset.offset(0.11))
    ;
  }

  @Test
  public void softlyCloseTo() {
    SoftAssertions softly = new SoftAssertions();
    assertThat(100.1)
        .isCloseTo(100, Offset.offset(0.05))
        .isCloseTo(100, Offset.offset(0.11))
    ;
    softly.assertAll();
  }

  @Test
  public void isBetween() {
    assertThat(99.0).isBetween(99.0, 100.0);
  }

  @Test
  public void contains() {
    assertThat(Arrays.asList("Hello", "world"))
        .contains("Hello")
        .contains("world")
        .contains("everyone")
    ;
  }

  @Test
  public void containsOnly1$thenPass() {
    assertThat(Arrays.asList("Hello", "world", "everyone")).containsOnly("Hello", "world", "everyone");
  }
  @Test
  public void containsOnly2$thenFail() {
    assertThat(Arrays.asList("Hello", "world")).containsOnly("Hello", "world", "everyone");
  }
  @Test
  public void containsOnly3$thenFail() {
    assertThat(Arrays.asList("Hello", "world", "everyone")).containsOnly("Hello", "world");
  }
}
