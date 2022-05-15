package com.github.dakusui.pcond;

import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.Objects;

public enum Pcond {
  ;

  /**
   * This method needs to be called at the beginning of the usage of this library.
   *
   * @param assertionProviderClass A provider class to be used hereafter.
   */
  public static void initializeWith(Class<? extends AssertionProvider<?>> assertionProviderClass) {
    System.setProperty(AssertionProvider.class.getCanonicalName(), assertionProviderClass.getName());
    InternalChecks.requireState(AssertionProvider.INSTANCE, v -> Objects.equals(assertionProviderClass, v.getClass()),
        () -> assertionProviderClass.getName() + " was requested, but actually, " + AssertionProvider.INSTANCE.getClass() + " was already loaded.");
  }
}
