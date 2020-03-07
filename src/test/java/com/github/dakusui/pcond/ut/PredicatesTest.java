package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.functions.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class PredicatesTest {
  public static class IsNullTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.isNull().test(null));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.isNull().test("Hello"));
    }
  }

  public static class IsNotNullTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.isNotNull().test("HELLO"));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.isNotNull().test(null));
    }
  }

  public static class EqTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.eq(100).test(100));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.eq(100).test(99));
      assertFalse(Predicates.eq(100).test(101));
    }

    @Test
    public void whenEqualityIsChecked$thenSameIsSameAndDifferentIsDifferent() {
      Object target = Predicates.eq(100);
      assertThat(
          target,
          allOf(
              is(Predicates.eq(100)),
              is(target),
              not(is(new Object())),
              not(is(Predicates.eq(101)))));
    }

    @Test
    public void whenHashCode$thenSameIsSameAndDifferentIsDifferent() {
      int target = Predicates.eq(100).hashCode();
      assertThat(
          target,
          allOf(
              is(Predicates.eq(100).hashCode()),
              not(is(Predicates.eq(101).hashCode()))));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("~[123]", Predicates.eq(123).toString());
    }
  }

  public static class GtTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.gt(100).test(101));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.gt(100).test(100));
      assertFalse(Predicates.gt(100).test(99));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(">[123]", Predicates.gt(123).toString());
    }
  }

  public static class GeTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.ge(100).test(101));
      assertTrue(Predicates.ge(100).test(100));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.ge(100).test(99));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals(">=[123]", Predicates.ge(123).toString());
    }
  }

  public static class LtTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.lt(100).test(99));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.lt(100).test(100));
      assertFalse(Predicates.lt(100).test(101));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("<[123]", Predicates.lt(123).toString());
    }
  }

  public static class LeTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.le(100).test(99));
      assertTrue(Predicates.le(100).test(100));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.le(100).test(101));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("<=[123]", Predicates.le(123).toString());
    }
  }

  public static class AllMatchTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.allMatch(Predicates.ge(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenNot$thenFalse() {
      assertFalse(Predicates.allMatch(Predicates.gt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("allMatch[<=[123]]", Predicates.allMatch(Predicates.le(123)).toString());
    }
  }

  public static class NoneMatchTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.noneMatch(Predicates.lt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenNot$thenFalse() {
      assertFalse(Predicates.noneMatch(Predicates.gt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("noneMatch[<=[123]]", Predicates.noneMatch(Predicates.le(123)).toString());
    }
  }

  public static class AnyMatchTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.anyMatch(Predicates.le(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenNot$thenFalse() {
      assertFalse(Predicates.anyMatch(Predicates.lt(100)).test(Stream.of(100, 200, 300)));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("anyMatch[<=[123]]", Predicates.anyMatch(Predicates.le(123)).toString());
    }
  }

  public static class MatchesRegexTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.matchesRegex("hello.").test("hello!"));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.matchesRegex(".ello.").test("hello"));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("matchesRegex[\"hello.\"]", Predicates.matchesRegex("hello.").toString());
    }
  }

  public static class ContainsStringTest extends TestBase {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.containsString("hello").test("hello!"));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.containsString(".ello.").test("hello!"));
    }

    @Test
    public void whenToString$thenLooksGood() {
      assertEquals("containsString[\"hello\"]", Predicates.containsString("hello").toString());
    }
  }
}