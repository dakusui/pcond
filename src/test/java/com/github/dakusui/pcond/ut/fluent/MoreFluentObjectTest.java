package com.github.dakusui.pcond.ut.fluent;

import org.junit.Test;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static com.github.dakusui.pcond.fluent.Fluents.*;
import static com.github.dakusui.pcond.forms.Functions.parameter;
import static com.github.dakusui.pcond.forms.Predicates.isNotNull;
import static java.util.Arrays.asList;

public class MoreFluentObjectTest {
  @Test
  public void test_asLong() {
    long var = 123;
    assertWhen(valueOf((Object) var).asLong().then().asLong().equalTo(123L));
  }

  @Test
  public void test_asInteger() {
    int var = 123;
    assertWhen(valueOf((Object) var).asInteger().then().asInteger().equalTo(123));
  }

  @Test
  public void test_asShort() {
    short var = 123;
    assertWhen(valueOf((Object) var).asShort().then().asShort().equalTo((short) 123));
  }

  @Test
  public void test_asDouble() {
    double var = 123.0;
    assertWhen(valueOf((Object) var).asDouble().then().asDouble().equalTo(123.0));
  }

  @Test
  public void test_asFloat() {
    float var = 123.0f;
    assertWhen(valueOf((Object) var).asFloat().then().asFloat().equalTo(123.0f));
  }

  @Test
  public void test_asBoolean() {
    boolean var = false;
    assertWhen(valueOf((Object) var).asBoolean().then().asBoolean().isEqualTo(false));
  }


  @Test
  public void test_asListOf() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf((Object) var).asListOf((String) value()).then().asListOf((String) value()).isEqualTo(asList("hello", "world")));
  }

  @Test
  public void test_asListOfClass() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf((Object) var).asListOfClass(String.class).then().isEqualTo(asList("hello", "world")));
  }

  @Test
  public void test_asStreamOf() {
    Stream<String> var = Stream.of("hello", "world");
    assertWhen(valueOf((Object) var).asStreamOf((String) value()).then().asStreamOf((String) value()).allMatch(isNotNull()));
  }

  @Test
  public void test_asStreamOfClass() {
    Stream<String> var = Stream.of("hello", "world");
    assertWhen(valueOf((Object) var).asStreamOfClass(String.class).then().allMatch(isNotNull()));
  }


  @Test
  public void test_intoLongWith() {
    String var = "123";
    assertWhen(valueOf(var).then().intoLongWith(Long::parseLong).equalTo(123L));
  }

  @Test
  public void test_intoIntegerWith() {
    String var = "123";
    assertWhen(valueOf(var).then().intoIntegerWith(Integer::parseInt).equalTo(123));
  }

  @Test
  public void test_intoShortWith() {
    String var = "123";
    assertWhen(valueOf(var).then().intoShortWith(Short::parseShort).equalTo((short) 123));
  }

  @Test
  public void test_intoDoubleWith() {
    String var = "123.0";
    assertWhen(valueOf(var).then().intoDoubleWith(Double::parseDouble).equalTo(123.0));
  }

  @Test
  public void test_intoFloatWith() {
    String var = "123.0f";
    assertWhen(valueOf(var).then().intoFloatWith(Float::parseFloat).equalTo(123.0f));
  }

  @Test
  public void test_intoBooleanWith() {
    String var = "false";
    assertWhen(valueOf(var).then().intoBooleanWith(Boolean::parseBoolean).isEqualTo(false));
  }


  @Test
  public void test_intoLong() {
    long var = 123;
    assertWhen(valueOf(var).then().intoLong().equalTo(123L));
  }

  @Test
  public void test_intoInteger() {
    int var = 123;
    assertWhen(valueOf(var).then().intoInteger().equalTo(123));
  }

  @Test
  public void test_intoShort() {
    short var = 123;
    assertWhen(valueOf(var).then().intoShort().equalTo((short) 123));
  }

  @Test
  public void test_intoDouble() {
    double var = 123.0;
    assertWhen(valueOf(var).then().intoDouble().equalTo(123.0));
  }

  @Test
  public void test_intoFloat() {
    float var = 123.0f;
    assertWhen(valueOf(var).then().intoFloat().equalTo(123.0f));
  }

  @Test
  public void test_intoBoolean() {
    boolean var = false;
    assertWhen(valueOf(var).then().intoBoolean().isEqualTo(false));
  }


  @Test
  public void test_intoString() {
    String var = "hello";
    assertWhen(valueOf(var).then().intoString().isEqualTo("hello"));
  }

  @Test
  public void test_intoObject() {
    String var = "hello";
    assertWhen(valueOf(var).then().intoObject().isNotNull());
  }

  @Test
  public void test_intoList() {
    List<String> var = asList("hello", "world");
    assertWhen(valueOf(var).then().intoList().isEqualTo(asList("hello", "world")));
  }

  @Test
  public void test_intoStream() {
    Stream<String> var = Stream.of("hello", "world");
    assertWhen(valueOf(var).then().intoStream().allMatch(isNotNull()));
  }

  @Test
  public void test_isNull() {
    String var = null;
    assertWhen(valueOf(var).then().isNull());
  }

  @Test
  public void test_sameReferenceAs() {
    Object var = new Object();
    assertWhen(valueOf(var).then().isSameReferenceAs(var));
  }

  @Test
  public void test_invoke() {
    Object var = new Object();
    assertWhen(valueOf(var).invoke("toString").then().invoke("toString").asString().contains("Object"));
  }

  @Test
  public void test_invokeStatic() {
    Object var = new Object();
    assertWhen(valueOf(var)
        .invokeStatic(Objects.class, "toString", parameter()).then()
        .invokeStatic(Objects.class, "toString", parameter()).asString().contains("Object"));
  }
}
