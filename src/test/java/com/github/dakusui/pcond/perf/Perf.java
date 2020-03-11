package com.github.dakusui.pcond.perf;

import com.github.dakusui.pcond.Preconditions;
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
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = noCheck(i);
    long after = System.currentTimeMillis();
    System.out.println("noCheck:" + numLoops + ":" + (after - before));
  }

  @Test
  public void a1_testObjectsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = objectsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("objectsRequireNonNull:" + numLoops + ":" + (after - before));
  }

  @Test
  public void a2_testPreconditionsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNull:" + numLoops + ":" + (after - before));
  }

  @Test
  public void a3_testPreconditionsRequireNonNullWithSimpleLambda() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNullWithSimpleLambda(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNullWithSimpleLambda:" + numLoops + ":" + (after - before));
  }

  @Test
  public void b0_testNoCheck() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = noCheck(i);
    long after = System.currentTimeMillis();
    System.out.println("noCheck:" + numLoops + ":" + (after - before));
  }

  @Test
  public void b1_testObjectsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = objectsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("objectsRequireNonNull:" + numLoops + ":" + (after - before));
  }

  @Test
  public void b2_testPreconditionsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNull:" + numLoops + ":" + (after - before));
  }

  @Test
  public void b3_testPreconditionsRequireNonNullWithSimpleLambda() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNullWithSimpleLambda(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNullWithSimpleLambda:" + numLoops + ":" + (after - before));
  }

  @Test
  public void c0_testNoCheck() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = noCheck(i);
    long after = System.currentTimeMillis();
    System.out.println("noCheck:" + numLoops + ":" + (after - before));
  }

  @Test
  public void c1_testObjectsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = objectsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("objectsRequireNonNull:" + numLoops + ":" + (after - before));
  }

  @Test
  public void c2_testPreconditionsRequireNonNull() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNull(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNull:" + numLoops + ":" + (after - before));
  }

  @Test
  public void c3_testPreconditionsRequireNonNullWithSimpleLambda() {
    int i = 0;
    long before = System.currentTimeMillis();
    while (i < numLoops)
      i = preconditionsRequireNonNullWithSimpleLambda(i);
    long after = System.currentTimeMillis();
    System.out.println("preconditionsRequireNonNullWithSimpleLambda:" + numLoops + ":" + (after - before));
  }

  private static int noCheck(int i) {
    return i + 1;
  }

  public static int objectsRequireNonNull(Integer i) {
    return Objects.requireNonNull(i) + 1;
  }

  public static int preconditionsRequireNonNull(Integer i) {
    return Preconditions.requireNonNull(i) + 1;
  }

  public static int preconditionsRequireNonNullWithSimpleLambda(Integer i) {
    //noinspection Convert2MethodRef
    return Preconditions.requireArgument(i, v -> v != null) + 1;
  }
}
