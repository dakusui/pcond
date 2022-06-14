package com.github.dakusui.pcond.ut;

import com.github.dakusui.pcond.Validations;
import com.github.dakusui.pcond.forms.Predicates;
import com.github.dakusui.pcond.forms.Printables;
import com.github.dakusui.pcond.provider.ApplicationException;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Function;

import static com.github.dakusui.pcond.forms.Functions.call;
import static com.github.dakusui.pcond.forms.Predicates.*;
import static com.github.dakusui.pcond.utils.TestUtils.firstLineOf;
import static org.junit.Assert.assertEquals;

public class ValidationsTest extends TestBase {
  @Test(expected = ApplicationException.class)
  public void test() throws ApplicationException {
    try {
      Object ret = Validations.validate(null, Predicates.not(Predicates.isEqualTo(null)), ApplicationException::new);
      System.out.println(ret);
    } catch (ApplicationException e) {
      assertEquals("Value:null violated: !isEqualTo[null]", firstLineOf(e.getMessage()));
      throw e;
    }
  }

  @Test
  public void test2() {
    Object ret = Validations.validate("Hello", Predicates.not(Predicates.isEqualTo(null)), ApplicationException::new);
    System.out.println(ret);
    assertEquals("Hello", ret);
  }

  @Test
  public void testValidateMethod$passing() throws IOException {
    Object ret = Validations.validate("Hello", Predicates.not(Predicates.isEqualTo(null)), IOException::new);
    System.out.println(ret);
    assertEquals("Hello", ret);
  }

  @Test(expected = IOException.class)
  public void testValidateMethod$failing() throws Throwable {
    try {
      Object ret = Validations.validate("Bye", Predicates.isEqualTo(null), IOException::new);
      System.out.println(ret);
    } catch (AssertionError e) {
      assertEquals("Value:\"Bye\" violated: isEqualTo[null]", firstLineOf(e.getCause().getMessage()));
      throw e.getCause();
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
    Validations.validate(
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
    Validations.validate(
        "Hello, World",
        transform(stringToLowerCase())
            .check(allOf(
                isEqualTo("hello, world"),
                transform(stringToUpperCase()).check(Predicates.isEqualTo("Non!"))
            )),
        ApplicationException::new
    );
  }

  private Function<String, String> stringToLowerCase() {
    return Printables.function("toLowerCase", String::toLowerCase);
  }

  private Function<String, String> stringToUpperCase() {
    return Printables.function("toUpperCase", String::toLowerCase);
  }

}
