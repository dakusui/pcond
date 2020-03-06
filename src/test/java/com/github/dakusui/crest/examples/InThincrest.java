package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.pcond.functions.Functions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.StringContains;
import org.junit.Test;

import java.util.*;

import static com.github.dakusui.crest.Crest.*;
import static com.github.dakusui.pcond.functions.Functions.*;
import static com.github.dakusui.pcond.functions.Predicates.eq;
import static com.github.dakusui.pcond.functions.Predicates.isEmpty;

/**
 * http://qiita.com/disc99/items/31fa7abb724f63602dc9
 */
public class InThincrest {
  private final List<String>       aList       = Collections.unmodifiableList(Arrays.asList("hoge", "fuga", "piyo"));
  private final String             aString     = "Hello, \tworld";
  private final String[]           anArray     = { "Gallia", "est", "omnis", "divisa" };
  private final Collection<String> aCollection = Collections.unmodifiableCollection(aList);
  private final Iterator<String>   anIterator  = aList.iterator();

  @Test
  public void withThincrest2$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asInteger(size()).eq(0).matcher(),
            Crest.asObjectList().containsAll(Arrays.asList("hoge", "fuga", "piyo", "poyo")).matcher()
        )
    );
  }

  // (2)
  // (3)
  @Test
  public void qiita_2_3$thenFail() {
    try {
      Crest.assertThat(
          aList,
          allOf(
              asBoolean(isEmpty()).isTrue().matcher(),
              asBoolean(isEmpty()).isFalse().matcher()
          )
      );
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void qiita_4_5$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asObject().equalTo("").matcher(),
            asObject().equalTo(aList).matcher()
        )
    );
  }

  // (6)
  // (7)
  @Test
  public void qiita_6_7$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asObject().isNull().matcher(),
            asObject().isNotNull().matcher()
        )
    );
  }

  // (8)
  @Test
  public void qiita_8$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asInteger(size()).eq(3).matcher(),
            asComparableOf(String.class, "toString").eq("HelloHelloHello").matcher()
        )
    );
  }

  @Test
  public void qiita_8$thenPass() {
    Crest.assertThat(
        aList,
        allOf(
            asInteger(size()).eq(3).matcher(),
            asComparableOf(String.class, "toString").eq("[hoge, fuga, piyo]").matcher()
        )
    );
  }

  @Test
  public void qiita_9_10$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asObjectList().isInstanceOf(LinkedList.class).matcher(),
            asObjectList().isSameAs(Collections.emptyList()).matcher()
        )
    );
    CoreMatchers.containsString("");
    StringContains.containsString("");
    StringContains.containsString("");
  }

  @Test
  public void qiita_11$thenFail() {
    Crest.assertThat(
        aList,
        asString().equalTo("[hello, world]").matcher()
    );
  }

  @Test
  public void qiita_12_13_14_15$thenFail() {
    Crest.assertThat(
        aList,
        allOf(
            asString().startsWith("[HELLO").matcher(),
            asString().endsWith("WORLD]").matcher(),
            asString().containsString("***").matcher(),
            asString().equalsIgnoreCase("***").matcher()
        )
    );
  }

  @Test
  public void qiita_12_13_14_15_17_18_19$thenPass() {
    Crest.assertThat(
        aList,
        anyOf(
            asString().startsWith("[hoge").matcher(),
            asString().endsWith("WORLD]").matcher(),
            asString().containsString("***").matcher(),
            asString().equalsIgnoreCase("***").matcher(),
            asString().isEmpty().matcher(),
            asString().isEmptyOrNull().matcher(),
            asString().matchesRegex(".*hoge.*").matcher()
        )
    );
  }

  @Test
  public void qiita_16$thenFail() {
    Crest.assertThat(
        aString,
        asString(
            function("trimSpace", (String s) -> s.replaceAll("\\s", ""))
        ).equalsIgnoreCase("HELLO,WORLD!").matcher()
    );
  }

  @Test
  public void qiita_16_simpler$thenFail() {
    Crest.assertThat(
        aString,
        asString((String s) -> s.replaceAll("\\s", "")).equalsIgnoreCase("HELLO,WORLD!").matcher()
    );
  }

  @Test
  public void qiita_20$thenFail() {
    Crest.assertThat(
        aString,
        asString().matchesRegex("[0-9]+").matcher()
    );
  }

  @Test
  public void qiita_20_another$thenFail() {
    Crest.assertThat(
        aString,
        asString().check(predicate("containsOnlyDigits", s -> s.matches("[0-9]+"))).matcher()
    );
  }

  @Test
  public void qiita_21$thenFail() {
    Crest.assertThat(
        aString,
        asInteger(
            countLines()
        ).eq(30).matcher()
    );
  }

  @Test
  public void qiita_21_simpler$thenFail() {
    // or, if you don't need friendly method explanation on failure.

    Crest.assertThat(
        aString,
        asInteger((String s) -> s.split("\n").length).eq(30).matcher()
    );
  }

  @Test
  public void qiita_24_25_26_27_integer$thenPass() {
    Crest.assertThat(
        123,
        allOf(
            asInteger().gt(100).matcher(),
            asInteger().ge(100).matcher(),
            asInteger().lt(200).matcher(),
            asInteger().le(200).matcher()
        )
    );
  }

  @Test
  public void qiita_23a$thenPass() {
    Crest.assertThat(
        123.4,
        asDouble().ge(100.0).le(200.0).matcher()
    );
  }

  @Test
  public void qiita_23a$thenFail() {
    Crest.assertThat(
        200.1,
        asDouble().ge(100.0).le(200.0).matcher()
    );
  }


  @Test
  public void qiita_24_25_26_27_integer1$thenFail() {
    Crest.assertThat(
        -123,
        allOf(
            asInteger().gt(100).matcher(),
            asInteger().ge(100).matcher(),
            asInteger().lt(200).matcher(),
            asInteger().le(200).matcher()
        )
    );
  }

  @Test
  public void qiita_24_25_26_27_integer2$thenFail() {
    Crest.assertThat(
        1230,
        allOf(
            asInteger().gt(100).matcher(),
            asInteger().ge(100).matcher(),
            asInteger().lt(200).matcher(),
            asInteger().le(200).matcher()
        )
    );
  }

  @Test
  public void qiita_28$thenPass() {
    Crest.assertThat(
        aList,
        asObjectList().containsExactly(Arrays.asList(
            "hoge", "fuga", "piyo"
        )).matcher()
    );
  }

  @Test
  public void qiita_28extra$thenFail() {
    Crest.assertThat(
        aList,
        asObjectList().containsExactly(Arrays.asList(
            "hoge", "fuga", "piyo", "hi"
        )).matcher()
    );
  }

  @Test
  public void qiita_28missing$thenFail() {
    Crest.assertThat(
        aList,
        asObjectList().containsExactly(Arrays.asList(
            "hoge", "fuga"
        )).matcher()
    );
  }

  @Test
  public void containsNone$thenPass() {
    System.out.println(aList);
    Crest.assertThat(
        aList,
        asObjectList().containsNone(Arrays.asList(
            "HOGE", "FUGA", "PIYO"
        )).matcher()
    );
  }

  @Test
  public void containsNone$thenFail() {
    System.out.println(aList);
    Crest.assertThat(
        aList,
        asObjectList().containsNone(Arrays.asList(
            "hoge", "fuga", "piyo", "hi"
        )).matcher()
    );
  }

  @Test
  public void containsNone$missing$thenFail() {
    System.out.println(aList);
    Crest.assertThat(
        aList,
        asObjectList().containsNone(Arrays.asList(
            "HOGE", "fuga"
        )).matcher()
    );
  }

  @Test
  public void qiita_31_32$thenFail() {
    Crest.assertThat(
        aCollection,
        allOf(
            asObjectList().isEmpty().matcher(),
            asInteger(size()).equalTo(2).matcher()
        )
    );
  }

  @Test
  public void qiita_33$thenFail() {
    Crest.assertThat(
        anIterator,
        // To check if its empty or not, type doesn't matter. Let's say 'Object'.
        Crest.asObjectList((Iterator<?> i) -> new LinkedList<Object>() {{
          while (i.hasNext()) {
            add(i.next());
          }
        }}).isEmpty().matcher()
    );
  }

  @Test
  public void qiita_39$thenPass() {
    Crest.assertThat(
        anArray,
        Crest.asObjectList(
            arrayToList()
        ).containsExactly(
            Arrays.asList("Gallia", "est", "omnis", "divisa")
        ).matcher()
    );
  }

  @Test
  public void qiita_39_typed$thenPass() {
    Crest.assertThat(
        anArray,
        Crest.asListOf(
            String.class,
            arrayToList()
        ).containsExactly(
            Arrays.asList("Gallia", "est", "omnis", "divisa")
        ).anyMatch(
            s -> s.matches("es.")
        ).matcher()
    );
  }


  @Test
  public void qiita_39_typed_2$thenFail() {
    Crest.assertThat(
        anArray,
        Crest.asListOf(
            String.class,
            arrayToList()
        ).containsExactly(
            Arrays.asList("Gallia", "est", "omnis", "divisa")
        ).noneMatch(
            s -> s.matches("es.")
        ).matcher()
    );
  }

  @Test
  public void qiita_39_typed_3$thenFail() {
    Crest.assertThat(
        anArray,
        Crest.asListOf(
            String.class,
            arrayToList()
        ).containsExactly(
            Arrays.asList("Gallia", "est", "omnis", "divisa")
        ).allMatch(
            s -> s.matches("es.")
        ).matcher()
    );
  }

  @Test
  public void qiita_39duplicate$thenPass() {
    Crest.assertThat(
        anArray,
        Crest.asObjectList(arrayToList()).containsExactly(Arrays.asList("Gallia", "est", "omnis", "omnis", "divisa")).matcher()
    );
  }

  @Test
  public void qiita_39missing$thenFail() {
    Crest.assertThat(
        anArray,
        Crest.asObjectList(arrayToList()).containsExactly(Arrays.asList("Gallia", "est", "omnis", "divisa", "in")).matcher()
    );
  }

  @Test
  public void qiita_39extra$thenFail() {
    Crest.assertThat(
        anArray,
        Crest.asObjectList(arrayToList()).containsExactly(Arrays.asList("est", "omnis", "divisa")).matcher()
    );
  }

  @Test
  public void qiita_40_43$thenFail() {
    Crest.assertThat(
        anArray,
        allOf(
            Crest.asListOf(String.class, Functions.arrayToList()).containsAll(Arrays.asList("Hello", "world")).matcher(),
            Crest.asListOf(String.class, Functions.arrayToList()).contains("Hello").matcher()
        ));
  }

  @Test
  public void qiita_41a$thenFail() {
    Crest.assertThat(
        anArray,
        asObjectList(Functions.arrayToList()).check(size(), eq(3)).matcher()
    );
  }

  @Test
  public void qiita_42a$thenFail() {
    Crest.assertThat(
        anArray,
        asObjectList(arrayToList()).check(size(), eq(3)).matcher()
    );
  }

  @Test
  public void qiita_42$thenFail() {
    Crest.assertThat(
        anArray,
        // To check if it's empty or not, type doesn't matter. Let's say 'Object'.
        Crest.asObjectList(arrayToList()).isEmpty().matcher()
    );
  }

  @Test
  public void qiita_43$thenFail() {
    Crest.assertThat(
        aCollection,
        // To check if it's empty or not, type doesn't matter. Let's say 'Object'.
        Crest.asListOf(String.class).contains("hello").matcher()
    );
  }
}
