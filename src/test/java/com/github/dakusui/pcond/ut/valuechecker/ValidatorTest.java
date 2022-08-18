package com.github.dakusui.pcond.ut.valuechecker;

import com.github.dakusui.pcond.utils.ut.TestBase;
import com.github.dakusui.pcond.validator.ExceptionComposer;
import com.github.dakusui.pcond.validator.MessageComposer;
import com.github.dakusui.pcond.validator.ReportComposer;
import com.github.dakusui.pcond.validator.Validator;
import org.junit.Test;

public class ValidatorTest extends TestBase {
  public static class TestValidator implements Validator {
    private final Configuration configuration = new Configuration() {
      @Override
      public int summarizedStringLength() {
        return 40;
      }

      @Override
      public boolean useEvaluator() {
        return false;
      }

      @Override
      public ExceptionComposer exceptionComposer() {
        return null;
      }

      @Override
      public MessageComposer messageComposer() {
        return null;
      }

      @Override
      public ReportComposer reportComposer() {
        return null;
      }
    };


    @Override
    public Configuration configuration() {
      return configuration;
    }
  }

  @Test
  public void test2() {
    System.out.println(TestValidator.class.getName());
    System.setProperty("com.github.dakusui.pcond.provider.AssertionProvider", "com.github.dakusui.pcond.ut.providers.AssertionProviderTest$TestAssertionProvider");
    System.out.println("-->" + Validator.INSTANCE.getClass().getCanonicalName());
  }
}
