package com.github.dakusui.pcond.core;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class IdentifiableExample extends Identifiable.Base {
  final static Factory<com.github.dakusui.pcond.core.IdentifiableExample> FACTORY  = new Factory.Base<com.github.dakusui.pcond.core.IdentifiableExample>(emptyList()) {
    @Override
    public com.github.dakusui.pcond.core.IdentifiableExample create(List<Object> args) {
      return new com.github.dakusui.pcond.core.IdentifiableExample(this, args);
    }
  };
  final static Factory<com.github.dakusui.pcond.core.IdentifiableExample> FACTORY2 = new Factory.Base<com.github.dakusui.pcond.core.IdentifiableExample>(emptyList()) {
    @Override
    public com.github.dakusui.pcond.core.IdentifiableExample create(List<Object> args) {
      return new com.github.dakusui.pcond.core.IdentifiableExample(this, args);
    }
  };

  private IdentifiableExample(Factory<? extends Base> factory, List<Object> args) {
    super(factory, args);
  }

  public String message() {
    return String.format("%s, %s!", this.arg(0), this.arg(1));
  }

  public static com.github.dakusui.pcond.core.IdentifiableExample create(String salute, String name) {
    return FACTORY.create(asList(salute, name));
  }

  public static void main(String... args) {
    com.github.dakusui.pcond.core.IdentifiableExample risa = create("Hello", "Risa");
    com.github.dakusui.pcond.core.IdentifiableExample risa2 = create("Hello", "Risa");
    com.github.dakusui.pcond.core.IdentifiableExample hiroshi = create("Hello", "Hiroshi");
    com.github.dakusui.pcond.core.IdentifiableExample hiroshi2 = FACTORY2.create(asList("Hello", "Hiroshi"));

    System.out.println(risa.message());
    System.out.println(hiroshi.message());
    System.out.println(risa.equals(hiroshi));
    System.out.println(risa.equals(risa2));
    System.out.println(hiroshi.equals(hiroshi2)); // different origin
    //noinspection EqualsWithItself
    System.out.println(hiroshi.equals(hiroshi));
    System.out.println(hiroshi.equals(new Object()));

    Factory.ForParameterizedPredicate<String> factory = new Factory.ForParameterizedPredicate<String>() {
      @Override
      protected Predicate<String> createPredicate(List<Object> args) {
        return s -> s.equals("hiroshi");
      }
    };

    Predicate<String> risaLoves = factory.create(singletonList("risa"));
    System.out.println(risaLoves.test("hiroshi"));
  }
}
