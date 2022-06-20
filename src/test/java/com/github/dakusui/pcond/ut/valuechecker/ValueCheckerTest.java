package com.github.dakusui.pcond.ut.valuechecker;

import com.github.dakusui.pcond.core.Configurations;
import com.github.dakusui.pcond.valuechecker.*;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

public class ValueCheckerTest extends TestBase {
  public static class TestValueChecker implements ValueChecker {
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
    System.out.println(TestValueChecker.class.getName());
    System.setProperty("com.github.dakusui.pcond.provider.AssertionProvider", "com.github.dakusui.pcond.ut.providers.AssertionProviderTest$TestAssertionProvider");
    System.out.println("-->" + ValueChecker.INSTANCE.getClass().getCanonicalName());
  }

  @Test(expected = IllegalStateException.class)
  public void test3() {
    Configurations.initializeWith(TestValueChecker.class);
    System.out.println(ValueChecker.INSTANCE.getClass().getCanonicalName());
    Configurations.initializeWith(ValueChecker.Impl.class);
  }
}
