package com.github.dakusui.ut.valid8j.ut;

import com.github.dakusui.valid8j.Validates;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.shared.ApplicationException;
import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.pcond.validator.exceptions.ValidationException;
import org.junit.Test;

import java.util.function.Function;

import static com.github.dakusui.pcond.forms.Functions.call;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.shared.utils.TestUtils.firstLineOf;
import static org.junit.Assert.assertEquals;

public class ValidatesTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void test() throws ApplicationException {
    try {
      Object ret = Validates.validate(null, Predicates.not(Predicates.isEqualTo(null)), ApplicationException::new);
      System.out.println(ret);
    } catch (ApplicationException e) {
      assertEquals("Value:null violated: !isEqualTo[null]", firstLineOf(e.getMessage()));
      throw e;
    }
  }

  @Test
  public void test2() {
    Object ret = Validates.validate("Hello", Predicates.not(Predicates.isEqualTo(null)), ApplicationException::new);
    System.out.println(ret);
    assertEquals("Hello", ret);
  }

  @Test
  public void testValidateMethod$passing() {
    Object ret = Validates.validate("Hello", Predicates.not(Predicates.isEqualTo(null)), UnsupportedOperationException::new);
    System.out.println(ret);
    assertEquals("Hello", ret);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testValidateMethod$failing() {
    try {
      Object ret = Validates.validate("Bye", Predicates.isEqualTo(null), UnsupportedOperationException::new);
      System.out.println(ret);
    } catch (UnsupportedOperationException e) {
      assertEquals("Value:\"Bye\" violated: isEqualTo[null]", firstLineOf(e.getMessage()));
      throw e;
    }
  }

  /*
  com.github.dakusui.pcond.provider.ApplicationException: Value:"HELLO, WORLD" violated: <P> <= (<P>.toLowerCase()) (<P> <= (<P>.equals(<hello, world>))&&<P> <= (<P>.toUpperCase()) isEqualTo["Non!"])
transformAndCheck                                       -> false
  <P> <= (<P>.toLowerCase())("HELLO, WORLD")            -> "hello, world"
  &&                                                    -> false
    <P> <= (<P>.equals(<hello, world>))("hello, world") -> true
    transformAndCheck                                   -> false
      <P> <= (<P>.toUpperCase())("hello, world")        -> "HELLO, WORLD"
      isEqualTo["Non!"]("HELLO, WORLD")                 -> false

   */
  @Test(expected = ApplicationException.class)
  public void testX() {
    Validates.validate(
        "Hello, World",
        transform(call("toUpperCase").andThen(call("toLowerCase")))
            .castTo(String.class)
            .check(allOf(
                callp("equals", "hello, world"),
                transform(call("toUpperCase")).check(Predicates.isEqualTo("Non!"))
            )),
        ApplicationException::new
    );
  }

  /*
com.github.dakusui.pcond.provider.ApplicationException: Value:"HELLO, WORLD" violated: toLowerCase (isEqualTo["hello, world"]&&toUpperCase isEqualTo["Non!"])
transformAndCheck                             -> false
  toLowerCase("HELLO, WORLD")                 -> "hello, world"
  &&                                          -> false
    isEqualTo["hello, world"]("hello, world") -> true
    transformAndCheck                         -> false
      toUpperCase("hello, world")             -> "hello, world"
      isEqualTo["Non!"]("hello, world")       -> false

   */
  @Test(expected = ApplicationException.class)
  public void testY() {
    Validates.validate(
        "Hello, World",
        transform(stringToLowerCase())
            .check(allOf(
                isEqualTo("hello, world"),
                transform(stringToUpperCase()).check(Predicates.isEqualTo("Non!"))
            )),
        ApplicationException::new
    );
  }

  @Test
  public void test_validateNonNull_pass() {
    String var = "Hello";
    Validates.validateNonNull(var);
  }

  @Test
  public void test_validateState_pass() {
    String var = "Hello";
    Validates.validateState(var, isNotNull());
  }

  @Test
  public void test_validateArgument_pass() {
    String var = "Hello";
    Validates.validateArgument(var, isNotNull());
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_validateArgument_fail() {
    String var = "Hello";
    Validates.validateArgument(var, isNull());
  }

  @Test
  public void test_validate_pass() {
    String var = "Hello";
    Validates.validate(var, isNotNull());
  }

  @Test(expected = ValidationException.class)
  public void test_validate_fail() {
    String var = "Hello";
    Validates.validate(var, isNull());
  }

  private Function<String, String> stringToLowerCase() {
    return Printables.function("toLowerCase", String::toLowerCase);
  }

  private Function<String, String> stringToUpperCase() {
    return Printables.function("toUpperCase", String::toLowerCase);
  }

}
