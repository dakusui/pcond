package com.github.dakusui.pcond.sandbox;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BiscuitTest {
  static class Biscuit {
    private final String type;

    public Biscuit(String ginger) {
      this.type = ginger;
    }

    public String toString() {
      return this.type;
    }
  }

  @Test
  public void testEquals() {
    Biscuit theBiscuit = new Biscuit("Ginger");
    Biscuit myBiscuit = new Biscuit("Sugar");
    assertThat(theBiscuit, equalTo(myBiscuit));
  }
}