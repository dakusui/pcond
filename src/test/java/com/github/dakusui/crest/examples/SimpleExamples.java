package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.pcond.functions.Functions;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.pcond.functions.Functions.size;
import static com.github.dakusui.pcond.functions.Predicates.eq;
import static java.util.Arrays.asList;

public class SimpleExamples {
  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfMatchesRegex_HELLO_$thenFail() {
    assertThat(
        asList("Hello", "world"),
        asString().matchesRegex("HELLO").any()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfMatchesRegexWithMathingOne$thenPass() {
    assertThat(
        asList("Hello", "world").toString(),
        asString().matchesRegex(".*Hello.*").all()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfContainsString_BYE_$thenFail() {
    assertThat(
        asList("Hello", "world"),
        asString().containsString("BYE").matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringAndCheckIfContainsString_Hello_$thenPass() {
    assertThat(
        asList("Hello", "world"),
        ////
        // You can use '$()' instead of 'matcher()' to build a matcher and make
        // the building process look more 'natural English sentence' like.
        asString().containsString("Hello").$()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringWithDynamic_toUpperCase_andContainsString_hello$thenFail() {
    assertThat(
        asList("Hello", "world").toString(),
        asString("toUpperCase").containsString("hello!").$()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsStringWithDynamic_toUpperCase_andContainsString_HELLO_$thenPass() {
    assertThat(
        asList("Hello", "world").toString(),
        asString("toUpperCase").containsString("HELLO").matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsListAndCheckIfContainsOnlyString_Hello_$thenFail() {
    assertThat(
        asList("Hello", "world"),
        asObjectList().containsOnly(Collections.singletonList("Hello")).matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsListAndCheckIfContainsOnlyString_Hello_World_$thenPass() {
    assertThat(
        asList("Hello", "world"),
        asObjectList().containsOnly(asList("Hello", "world")).matcher()
    );
  }

  @Test
  public void givenListOf_Hello_World_$whenAsListAndCheckIfContainsOnly_Hello_world_everyone_$thenPass() {
    assertThat(
        asList("Hello", "world"),
        asObjectList().containsOnly(asList("Hello", "world", "everyone")).matcher()
    );
  }

  @Test
  public void given100$whenAsComparableOfIntegerAndThenEq_100_$thenPass() {
    assertThat(
        100,
        asComparableOf(Integer.class).eq(100).matcher()
    );
  }


  @Test
  public void given_abc_$whenAsComparableOfStringAndThenEq_ABC_$thenFail() {
    assertThat(
        "abc",
        asComparableOf(String.class).eq("ABC").matcher()
    );
  }

  @Test
  public void test8_b$thenPass() {
    assertThat(
        "aStringToBeExamined",
        anyOf(
            asObject().equalTo("aStringToBeExamined!").matcher(),
            asObject("toString").equalTo("aStringToBeExamined").matcher(),
            asComparableOf(Integer.class, "length").eq(0).matcher()
        )
    );
  }

  @Test
  public void test8$thenPass() {
    assertThat(
        "aStringToBeExamined",
        anyOf(
            asObject().equalTo("aStringToBeExamined!").matcher(),
            asObject().equalTo("aStringToBeExamined").matcher(),
            asComparableOf(Integer.class, "length").eq(0).matcher()
        )
    );
  }

  @Test
  public void test8a$thenFail() {
    assertThat(
        "aStringToBeExamined",
        asObject().equalTo("aStringToBeExamined2").all()
    );

    assertThat(
        "aStringToBeExamined",
        asObject().all()
    );

    assertThat(
        "aStringToBeExamined",
        asString().equalTo("aStringToBeExamined2").all()
    );
  }


  @Test
  public void given_DeBelloGallicco_$whenAllOfAsStringFailingAndAsObjectFailingMatchers$thenFail() {
    assertThat(
        "Gallia est omnis divisa in partes tres, quarun unum incolunt Belgae, "
            + "alium Aquitani, tertium linua ipsorum Celtae, nostra Galli appelantur",
        allOf(
            asString().containsString("Caesar").check("contains", "est").containsString("Caesar").matcher(),
            asObject("length").check(predicate(">1024", o -> ((Integer) o) > 1024)).matcher()
        )
    );
  }

  @Test
  public void givenEmptyString$whenAsObjectByMethodCallAndDoFailingCheckByMethodCall$thenFail() {
    assertThat(
        "",
        asObject("length").check("equals", "hello").matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndFailingCheck$thenFail() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparableOf(Integer.class, "length").gt(50).matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByLengthMethodCallAndPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparableOf(Integer.class, "length").lt(50).matcher()
    );
  }

  @Test
  public void givenStringOf20Letters$whenAsComparableByPresetLengthFunctionAndPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        "12345678901234567890",
        asComparable(Functions.length()).ge(5).lt(50).matcher()
    );
  }

  @Test
  public void given_Hello_world_$whenAsComparableAndDoPassingCheck$thenPass() {
    assertThat(
        "Hello, world",
        asComparable(Functions.length()).ge(5).lt(50).matcher()
    );
  }

  @Test
  public void given_50_$whenAsComparableAndDoFailingCheck$thenFail() {
    assertThat(
        50,
        Crest.asComparableOf(Integer.class).ge(5).lt(50).all()
    );
  }


  @Test
  public void givenList$$whenContains101$thenFail() {
    assertThat(
        asList(100, 200, 300, 400, 500),
        Crest.asObjectList().contains(101).matcher()
    );
  }

  @Test
  public void givenList$$whenContains100$thenPass() {
    assertThat(
        asList(100, 200, 300, 400, 500),
        Crest.asObjectList().contains(100).matcher()
    );
  }


  @Test
  public void givenList$$whenContainsAll101$thenFail() {
    assertThat(
        asList(100, 200, 300, 400, 500),
        Crest.asObjectList().containsAll(asList(100, 101)).matcher()
    );
  }

  @Test
  public void givenList$$whenContainsAll100and200$thenPass() {
    assertThat(
        asList(100, 200, 300, 400, 500),
        Crest.asObjectList().containsAll(asList(100, 200)).matcher()
    );
  }

  @Test
  public void givenList$$whenIsEmpty$thenFail() {
    assertThat(
        asList(100, 200, 300, 400, 500),
        Crest.asObjectList().contains("100").isEmpty().matcher()
    );
  }

  @Test
  public void givenTypedList$$whenIsEmpty$thenFail() {
    List<Integer> aList = asList(100, 200, 300, 400, 500);
    assertThat(
        aList,
        Crest.asObjectList().contains("100").isEmpty().matcher()
    );
  }

  @Test
  public void givenEmptyList$$whenIsEmpty$thenPass() {
    assertThat(
        Collections.emptyList(),
        Crest.asObjectList().isEmpty().matcher()
    );
  }

  @Test
  public void givenString$whenParseIntAndTest$thenPass() {
    assertThat(
        "123",
        asInteger("length").eq(3).matcher()
    );
  }

  @Test
  public void givenArray$whenHasKey$thenFail() {
    Object[][] in = {
        { "hello", 5 },
        { "world", 5 },
        { "everyone", 8 },
    };
    Function<Object[][], HashMap<Object, Object>> arrToMap = function(
        "arrToMap",
        (Object[][] arr) -> new HashMap<Object, Object>() {
          {
            for (Object[] each : arr)
              put(each[0], each[1]);
          }
        }
    );
    assertThat(
        in,
        Crest.asObjectMap(arrToMap).hasKey("").hasKey(200).matcher()
    );
  }

  @Test
  public void givenArray$whenHasKey$thenPass() {
    Object[][] in = {
        { "hello", 5 },
        { "world", 5 },
        { "everyone", 8 },
    };
    Function<Object[][], HashMap<Object, Object>> arrToMap = function(
        "arrToMap",
        (Object[][] arr) -> new HashMap<Object, Object>() {
          {
            for (Object[] each : arr)
              put(each[0], each[1]);
          }
        }
    );
    assertThat(
        in,
        Crest.asObjectMap(arrToMap).hasKey("hello").hasKey("world").matcher()
    );
  }

  @Test
  public void givenMapWithoutTypes$whenHasKey$thenFail() {
    Map<?, ?> map = new HashMap<Object, Object>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        allOf(
            Crest.asObjectMap().hasKey("").hasKey(200).matcher()
        )
    );
  }


  @Test
  public void givenMap$whenHasValue$thenPass() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        Crest.asMapOf(String.class, Integer.class).hasValue(5).matcher()
    );
  }

  @Test
  public void givenMap$whenHasValue$thenFail() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        allOf(
            Crest.asMapOf(String.class, Integer.class).hasValue(10).matcher()
        )
    );
  }

  @Test
  public void givenMap$whenHasEntry$thenPass() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        Crest.asMapOf(String.class, Integer.class).hasEntry("world", 5).matcher()
    );
  }

  @Test
  public void givenMap$whenHasEntry$thenFail() {
    Map<String, Integer> map = new HashMap<String, Integer>() {{
      put("hello", 5);
      put("world", 5);
      put("everyone", 8);
    }};
    assertThat(
        map,
        allOf(
            Crest.asMapOf(String.class, Integer.class).hasEntry("hello", 8).matcher()
        )
    );
  }

  @Test
  public void givenTrue$whenAsBooleanAndCheck$thenFail() {
    assertThat(
        true,
        asBoolean().isFalse().matcher()
    );
  }

  @Test
  public void givenTrue$whenAsBooleanAndCheck$thenPass() {
    assertThat(
        true,
        asBoolean().isTrue().matcher()
    );
  }

  @Test
  public void given_true_$whenAsBooleanAndCheck$thenFail() {
    assertThat(
        "true",
        asBoolean("equals", "hello").isTrue().matcher()
    );
  }

  @Test
  public void given_true_$whenAsBooleanAndCheck$thenPass() {
    assertThat(
        "true",
        asBoolean("equals", "true").isTrue().matcher()
    );
  }


  @Test
  public void listSize$thenPass() {
    assertThat(
        asList("Hello", "world"),
        allOf(
            asListOf(String.class).check(size(), eq(2)).$()
        )
    );
  }

  @Test
  public void listSize$thenFail() {
    Predicate<? super Integer> failingPredicate = predicate(
        "failing predicate",
        i -> {
          throw new RuntimeException("FAILED");
        }
    );
    assertThat(
        asList("Hello", "world"),
        allOf(
            asListOf(String.class).check(size(), failingPredicate).$()
        )
    );
  }

  @Test
  public void listSizeWithFailingFunction$thenFail() {
    Function<List<String>, Integer> failingFunction = function("failingFunction", strings -> {
      throw new RuntimeException("FAILED");
    });
    assertThat(
        asList("Hello", "world"),
        allOf(
            asListOf(String.class).check(failingFunction, eq(2)).$()
        )
    );
  }

  @SuppressWarnings("RedundantCollectionOperation")
  public static void main(String... args) {
    System.out.println("empty.contrainsAll(empty):" + Collections.emptyList().containsAll(Collections.emptyList()));
    System.out.println("nonEmpty.contrainsAll(empty):" + Collections.singletonList("a").containsAll(Collections.emptyList()));
    System.out.println("empty.contrainsAll(nonEmpty):" + Collections.emptyList().containsAll(Collections.singleton("a")));
  }
}
