package com.github.dakusui.pcond.ut.providers;

import com.github.dakusui.pcond.core.Configurations;
import com.github.dakusui.pcond.provider.*;
import com.github.dakusui.pcond.utils.ut.TestBase;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Predicate;

public class AssertionProviderTest extends TestBase {
  public static class TestAssertionProvider implements AssertionProvider {
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
    System.out.println(TestAssertionProvider.class.getName());
    System.setProperty("com.github.dakusui.pcond.provider.AssertionProvider", "com.github.dakusui.pcond.ut.providers.AssertionProviderTest$TestAssertionProvider");
    System.out.println("-->" + AssertionProvider.INSTANCE.getClass().getCanonicalName());
  }

  @Test(expected = IllegalStateException.class)
  public void test3() {
    Configurations.initializeWith(TestAssertionProvider.class);
    System.out.println(AssertionProvider.INSTANCE.getClass().getCanonicalName());
    Configurations.initializeWith(AssertionProvider.Impl.class);
  }
}
