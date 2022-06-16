package com.github.dakusui.pcond;

import com.google.java.contract.Invariant;
import com.google.java.contract.Requires;

@Invariant({ "title != null && title.length() > 0", "price > 0" })
public class CofojaSandbox {
  private final String title;
  private       int    price;

  @Requires({ "title != null && title.length() > 0", "price > 0" })
  public CofojaSandbox(String title, int price) {
    this.title = title;
    this.price = price;
  }

  public CofojaSandbox() {
    this.title = "helloTitle";
  }

  public int getPrice() {
    return price;
  }

  @Requires("price > 0")
  public void setPrice(int price) {
    this.price = price;
  }

  public String getTitle() {
    return title;
  }

  public static void main(String... args) {
    new CofojaSandbox("Hello, world", 100).setPrice(-10);
  }
}