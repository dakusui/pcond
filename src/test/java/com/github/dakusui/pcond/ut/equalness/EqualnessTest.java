package com.github.dakusui.pcond.ut.equalness;

import com.github.dakusui.pcond.functions.Functions;
import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.github.dakusui.pcond.Preconditions.requireNonNull;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

@RunWith(Parameterized.class)
@FixMethodOrder(value = MethodSorters.JVM)
public class EqualnessTest extends TestBase {
  final Object  target;
  final Object  targetSecond;
  final Object  equal;
  final Object  another;
  final boolean cached;

  public EqualnessTest(TestDef testDef) {
    target = testDef.targetObjectSupplier.get();
    targetSecond = testDef.targetObjectSupplier.get();
    equal = testDef.equalObjectSupplier.get();
    another = testDef.nonEqualObjectSupplier.get();
    cached = testDef.cached;
  }

  @Test
  public void print() {
    System.out.println("Test:");
    System.out.printf("  Target function (f):                                    %s%n", target);
    System.out.printf("  A function g that should return true when f.equals(g):  %s%n", targetSecond);
    System.out.printf("  A function g that should return false when f.equals(g): %s%n", another);
  }

  @Test
  public void returnsSameHashCode() {
    assertEquals(target.hashCode(), targetSecond.hashCode());
  }

  @Test
  public void equalsWithOneCreatedFromSameFactoryAndArguments() {
    assertEquals(target, targetSecond);
  }

  @Test
  public void equalsWithOneCreatedFromSameFactoryAndArguments_reversed() {
    assertEquals(targetSecond, target);
  }

  @Test
  public void equalsWithItself() {
    assertEquals(target, target);
  }

  @Test
  public void notEqualsWithAnother() {
    assertNotEquals(target, another);
  }

  @Test
  public void sameObject() {
    assumeTrue(this.cached);
    assertSame(this.target, this.targetSecond);
  }

  @Parameterized.Parameters
  public static TestDef[] parameters() {
    return new TestDef[] {
        define(args -> Functions.length()).$(),
        define(args -> Functions.elementAt((int) args[0]), 1).cached(false).$(),
        define(args -> Functions.elementAt((int) args[0]), 1)
            .nonEqualObjectFactory(args -> Functions.elementAt((int) args[0]), 2)
            .cached(false).$(),
        define(args -> Functions.stringify().andThen(Functions.identity()))
            .equalObjectFactory(args -> Functions.identity().compose(Functions.stringify()))
            .cached(false).$(),
        define(args -> Printables.function("custom", Function.identity()))
            .cached(false).$(),
        define(args -> Function.identity()).$(),
        define(args -> Predicates.allMatch((Predicate<?>) args[0]), Predicates.alwaysTrue())
            .cached(false).$(),
        define(args -> Predicates.alwaysTrue()).$(),
        define(args -> Predicates.isTrue()).$(),
        define(args -> Predicates.isFalse()).$(),
        define(args -> Predicates.isNull()).$(),
        define(args -> Predicates.isNotNull()).$(),
        define(args -> Predicates.isEqualTo(args[0]), new Object()).cached(false).$(),
        define(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .cached(false).$(),
        define(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .nonEqualObjectFactory(args -> Predicates.isSameReferenceAs(args[0]), new Object())
            .cached(false).$(),
    };
  }

  static TestDef.Builder define(Function<Object[], Object> targetObjectFactory, Object... args) {
    return new TestDef.Builder(targetObjectFactory, args);
  }

  static class TestDef {
    final Supplier<Object> targetObjectSupplier;
    final Supplier<Object> equalObjectSupplier;
    final Supplier<Object> nonEqualObjectSupplier;
    final boolean          cached;

    TestDef(Supplier<Object> targetObjectSupplier, Supplier<Object> equalObjectSupplier, Supplier<Object> nonEqualObjectSupplier, boolean cached) {
      this.targetObjectSupplier = targetObjectSupplier;
      this.equalObjectSupplier = equalObjectSupplier;
      this.nonEqualObjectSupplier = nonEqualObjectSupplier;
      this.cached = cached;
    }

    static class Builder {
      private final Supplier<Object> targetObjectSupplier;
      private       Supplier<Object> equalObjectSupplier;
      private       Supplier<Object> nonEqualObjectSupplier;
      private       boolean          cached;

      Builder(Function<Object[], Object> targetObjectFactory, Object... args) {
        this(() -> targetObjectFactory.apply(args));
      }

      Builder(Supplier<Object> targetObjectSupplier) {
        this.targetObjectSupplier = targetObjectSupplier;
        this.equalObjectSupplier = targetObjectSupplier;
        this.nonEqualObjectSupplier = () -> Utils.dummyFunctionFactory().apply(new Object[0]);
        this.cached(true);
      }

      Builder equalObjectFactory(Function<Object[], Object> equalObjectFactory, Object... args) {
        return this.equalObjectSupplier(() -> equalObjectFactory.apply(args));
      }

      Builder equalObjectSupplier(Supplier<Object> equalObjectSupplier) {
        this.equalObjectSupplier = requireNonNull(equalObjectSupplier);
        return this;
      }

      Builder nonEqualObjectFactory(Function<Object[], Object> nonEqualObjectFactory, Object... args) {
        return this.nonEqualObjectSupplier(() -> nonEqualObjectFactory.apply(args));
      }

      Builder nonEqualObjectSupplier(Supplier<Object> nonEqualObjectSupplier) {
        this.nonEqualObjectSupplier = requireNonNull(nonEqualObjectSupplier);
        return this;
      }

      Builder cached(boolean cached) {
        this.cached = cached;
        return this;
      }

      TestDef $() {
        return new TestDef(this.targetObjectSupplier, this.equalObjectSupplier, this.nonEqualObjectSupplier, this.cached);
      }
    }
  }

  enum Utils {
    ;

    @SuppressWarnings("Convert2Lambda")
    private static Function<Object[], Object> dummyFunctionFactory() {
      return args -> Printables.function("dummy",
          new Function<Object, Object>() {
            @Override
            public Object apply(Object o) {
              return o;
            }
          });
    }
  }
}
