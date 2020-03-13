package com.github.dakusui.pcond.perf;

import com.github.dakusui.pcond.Assertions;
import com.github.dakusui.pcond.Preconditions;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.Objects;

@Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Perf {
  private static final int numLoops = 1_000_000_000;

  @BeforeClass
  public static void warmUp() {
    int w = 0, x = 0, y = 0, z = 0;
    for (int i = 0; i < 10_000_000; i++) {
      w = noCheck(w);
      x = objectsRequireNonNull(x);
      y = preconditionsRequireNonNull(y);
      z = preconditionsRequireNonNullWithSimpleLambda(z);
    }
  }

  @Test
  public void a0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void a1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void a2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void a3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void a4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void b0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void b1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void b2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void b3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void b4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void c0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void c1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void c2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void c3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void c4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void d1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void d2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void d0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void d3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void d4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void e0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void e1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void e2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void e3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void e4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void f0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void f1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void f2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void f3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void f4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void g0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void g1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void g2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void g3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void g4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void h0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void h1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void h2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void h3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void h4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void i0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void i1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void i2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void i3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void i4_testAssertNonNull() {
    testAssertNonNulls();
  }

  @Test
  public void j0_testNoCheck() {
    testNoCheck();
  }

  @Test
  public void j1_testObjectsRequireNonNull() {
    testObjectsRequireNonNull();
  }

  @Test
  public void j2_testPreconditionsRequireNonNull() {
    testPreconditionsRequireNonNull();
  }

  @Test
  public void j3_testPreconditionsRequireNonNullWithSimpleLambda() {
    testPreconditionsRequireNonNullWithSimpleLambda();
  }

  @Test
  public void j4_testAssertNonNull() {
    testAssertNonNulls();
  }

  public static void testNoCheck() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = noCheck(i);
    long after = System.currentTimeMillis();
    System.out.println("noCheck:" + i + ":" + (after - before));
  }

  public static void testObjectsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = objectsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("objectsRequireNonNull:" + i + ":" + (after - before));
  }

  public static void testPreconditionsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNull:" + i + ":" + (after - before));

  }

  public static void testPreconditionsRequireNonNullWithSimpleLambda() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNullWithSimpleLambda(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNullWithSimpleLambda:" + i + ":" + (after - before));
  }

  public static void testAssertNonNulls() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = assertNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("assertNonNull:" + i + ":" + (after - before));
  }

  @Test
  public void testDirectAssertNonNulls() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = directAssertNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("assertNonNull:" + i + ":" + (after - before));
  }

  public static int noCheck(int i) {
    return i + 1;
  }

  public static int objectsRequireNonNull(Integer i) {
    return Objects.requireNonNull(i) + 1;
  }

  public static int preconditionsRequireNonNull(Integer i) {
    return Preconditions.requireNonNull(i) + 1;
  }

  public static int assertNonNull(int i) {
    Assertions.assertInt(i, Predicates.isNotNull());
    return i + 1;
  }

  public static int directAssertNonNull(int i) {
    assert Predicates.isNotNull().test(null);
    return i + 1;
  }

  public static int preconditionsRequireNonNullWithSimpleLambda(Integer i) {
    //noinspection Convert2MethodRef
    return Preconditions.requireArgument(i, v -> v != null) + 1;
  }

  @Ignore
  @Test
  public void test() {
    Assertions.assertValue(null, Predicates.isNotNull());
  }

}
