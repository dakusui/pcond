package com.github.dakusui.pcond.core.identifieable;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class IdentifiableExample extends Identifiable.Base {
  final static IdentifiableFactory<IdentifiableExample> FACTORY  = new IdentifiableFactory.Base<IdentifiableExample>(emptyList()) {
    @Override
    public IdentifiableExample create(List<Object> args) {
      return new IdentifiableExample(this, args);
    }
  };
  final static IdentifiableFactory<IdentifiableExample> FACTORY2 = new IdentifiableFactory.Base<IdentifiableExample>(emptyList()) {
    @Override
    public IdentifiableExample create(List<Object> args) {
      return new IdentifiableExample(this, args);
    }
  };

  private IdentifiableExample(IdentifiableFactory<? extends Base> factory, List<Object> args) {
    super(factory, args);
  }

  public String message() {
    return String.format("%s, %s!", this.arg(0), this.arg(1));
  }

  public static IdentifiableExample create(String salute, String name) {
    return FACTORY.create(asList(salute, name));
  }

  public static void main(String... args) {
    IdentifiableExample risa = create("Hello", "Risa");
    IdentifiableExample risa2 = create("Hello", "Risa");
    IdentifiableExample hiroshi = create("Hello", "Hiroshi");
    IdentifiableExample hiroshi2 = FACTORY2.create(asList("Hello", "Hiroshi"));

    System.out.println(risa.message());
    System.out.println(hiroshi.message());
    System.out.println(risa.equals(hiroshi));
    System.out.println(risa.equals(risa2));
    System.out.println(hiroshi.equals(hiroshi2)); // different origin
    //noinspection EqualsWithItself
    System.out.println(hiroshi.equals(hiroshi));
    System.out.println(hiroshi.equals(new Object()));

    IdentifiableFactoryForParameterizedPredicate<String> factory = new IdentifiableFactoryForParameterizedPredicate<String>() {
      @Override
      protected Predicate<String> createPredicate(List<Object> args) {
        return s -> s.equals("hiroshi");
      }
    };

    Predicate<String> risaLoves = factory.create(singletonList("risa"));
    System.out.println(risaLoves.test("hiroshi"));

    System.out.println(risaLoves.negate());
    System.out.println(risaLoves.negate());

    String as = "hello";
    String bs = "hello";
    System.out.println(as.hashCode());
    System.out.println(bs.hashCode());
  }

  public interface IdentifiableFactory<T extends Identifiable> extends Identifiable {
    T create(List<Object> args);

    interface IdentifiablePredicate<T> extends Identifiable, Predicate<T> {
      abstract class Base<T> extends Identifiable.Base implements IdentifiablePredicate<T> {
        protected Base(IdentifiableFactory<IdentifiablePredicate<T>> factory, List<Object> args) {
          super(factory, args);
        }
      }
    }

    abstract class Base<T extends Identifiable> extends Identifiable.Base implements IdentifiableFactory<T>, Identifiable {
      protected Base(Object creator, List<Object> args) {
        super(creator, args);
      }

      protected Base(List<Object> args) {
        this(new Object(), args);
      }
    }
  }
}
