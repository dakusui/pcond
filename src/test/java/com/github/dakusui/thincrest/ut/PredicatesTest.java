package com.github.dakusui.thincrest.ut;

import com.github.dakusui.thincrest.TestAssertions;
import com.github.dakusui.pcond.forms.Functions;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.ComparisonFailure;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static com.github.dakusui.valid8j.Requires.requireArgument;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class PredicatesTest {
  public static class MessageTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void testFormat() {
      try {
        requireArgument(100, Predicates.and(Predicates.isNotNull(), Predicates.isInstanceOf(String.class)));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        String message = e.getMessage().replaceAll(" +", " ");
        System.out.println(message);
        TestAssertions.assertThat(
            lineAt(message, 1),
            Predicates.containsString("100->and ->false"));
        TestAssertions.assertThat(
            lineAt(message, 2),
            Predicates.containsString("isNotNull ->true"));
        TestAssertions.assertThat(
            lineAt(message, 3),
            Predicates.containsString("isInstanceOf[class java.lang.String]->false"));
        throw e;
      }
    }
  }

  public static class IsInstanceOfTest extends TestBase {
    @Test(expected = IllegalArgumentException.class)
    public void test() {
      try {
        requireArgument(100, Predicates.isInstanceOf(String.class));
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        assertThat(
            lineAt(e.getMessage(), 1),
            allOf(
                containsString("isInstanceOf"),
                containsString("java.lang.String"),
                containsString("false")));
        throw e;
      }
    }
  }

  public static class IsNullTest extends TestBase.ForAssertionEnabledVM {
    @Test
    public void whenMet$thenTrue() {
      assertTrue(Predicates.isNull().test(null));
    }

    @Test
    public void whenNotMet$thenFalse() {
      assertFalse(Predicates.isNull().test("Hello"));
    }
  }

  public static class IsNotNullTest extends TestBase.ForAssertionEnabledVM {
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
      assertEquals("=[123]", Predicates.eq(123).toString());
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

  public static class AndTest extends TestBase.ForAssertionEnabledVM {
    @Test
    public void performSingleAnd$thenTrue() {
      assertTrue(Predicates.and(Predicates.alwaysTrue()).test("hello"));
    }

    @Test
    public void performAnd$thenTrue() {
      assertTrue(Predicates.and(Predicates.not(Predicates.isNull()), Predicates.not(Predicates.isEmptyString())).test("hello"));
    }

    @Test
    public void performAnd$thenFalse() {
      assertFalse(Predicates.and(Predicates.not(Predicates.isNull()), Predicates.not(Predicates.isEmptyString())).test(null));
    }

    @Test
    public void performOr$thenTrue() {
      assertTrue(Predicates.or(Predicates.isNull(), Predicates.isEmptyString()).test(null));
    }
  }

  public static class OrTest extends TestBase {
    @Test
    public void performSingleOr$thenTrue() {
      assertTrue(Predicates.or(Predicates.alwaysTrue()).test("hello"));
    }
  }

  public static class NotTest extends TestBase {
    @Test
    public void test() {
      assertFalse(Predicates.not(Predicates.alwaysTrue()).test(true));
    }
  }

  public static class FindStringTest extends TestBase {

    @Test(expected = NoSuchElementException.class)
    public void example() {
      assertFalse(
          Predicates.allOf(
                  Predicates.isNotNull(),
                  Predicates.transform(Functions.findString("aPattern")
                          .andThen(Functions.findString("nextPattern")
                              .andThen(Functions.findString("-"))))
                      .check(Predicates.isNotNull()))
              .test("hello aPattern, world, gallia est omnis divisa, quarum unum incolunt Belgae,  nextPattern!!!!!"));
    }
  }

  public static class FindStringsTest extends TestBase {
    @Test(expected = ComparisonFailure.class)
    public void givenSomeFoundSomeNot$whenFindString$thenFailed() {
      String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur. De Bello Gallicco.";
      try {
        TestAssertions.assertThat(text, Predicates.findSubstrings("Gallia", "quarum", "Belgium", "nostra", "De", "Gallia", "Gallicco"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        throw e;
      }
    }

    @Test
    public void givenAllFound$whenFindString$thenPassed() {
      String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur. De Bello Gallicco.";
      try {
        TestAssertions.assertThat(text, Predicates.findSubstrings("Gallia", "quarum", "Belgae", "nostra", "De", "Gallicco"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        throw e;
      }
    }

    @Test(expected = ComparisonFailure.class)
    public void givenSomeFoundSomeNotFound$whenFindRegexes$thenFailed() {
      String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur";
      try {
        TestAssertions.assertThat(text, Predicates.findRegexes("Gall.a", "quar.m", "Belgium", "nostr(um|a)"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        throw e;
      }
    }
    @Test
    public void givenAllFound$whenFindRegexes$thenPassed() {
      String text = "Gallia est omnis divisa in partes tres, quarum unum incolunt Belgae, aliam Acquitanii, tertiam nostra Galli Appellantur";
      try {
        TestAssertions.assertThat(text, Predicates.findRegexes("Gall.a", "quar.m", "Belg.+e,", "nostr(um|a)"));
      } catch (ComparisonFailure e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public static class FindElementsTest extends TestBase {
    @Test(expected = ComparisonFailure.class)
    public void givenSomeToBeFoundSomeNotToBe$whenFindElements$thenFailed() {
      List<String> list = asList("Hello", "world", "", "everyone", "quick", "brown", "fox", "runs", "forever");
      list.forEach(System.out::println);
      TestAssertions.assertThat(list,
          Predicates.findElements(
              Predicates.isEqualTo("world"),
              Predicates.isEqualTo("cat"), Predicates.isEqualTo("organization"), Predicates.isNotNull(), Predicates.isEqualTo("fox"), Predicates.isEqualTo("world")));
    }

    @Test
    public void givenAllFound$whenFindElements$thenPassed() {
      List<String> list = asList("Hello", "world", "", "everyone", "quick", "brown", "fox", "runs", "forever");
      list.forEach(System.out::println);
      TestAssertions.assertThat(list,
          Predicates.findElements(
              Predicates.isEqualTo("world"),
              Predicates.isNotNull(),
              Predicates.isEqualTo("fox")));

    }
  }
}