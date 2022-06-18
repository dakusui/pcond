package com.github.dakusui.pcond.core;

import com.github.dakusui.pcond.internals.InternalChecks;
import com.github.dakusui.pcond.provider.ValueChecker;

import java.util.Objects;

public enum Configurations {
  ;

  /**
   * This method needs to be called at the beginning of the usage of this library.
   *
   * @param assertionProviderClass A provider class to be used hereafter.
   */
  public static void initializeWith(Class<? extends ValueChecker> assertionProviderClass) {
    System.setProperty(ValueChecker.class.getCanonicalName(), assertionProviderClass.getName());
    InternalChecks.requireState(ValueChecker.INSTANCE, v -> Objects.equals(assertionProviderClass, v.getClass()),
        () -> assertionProviderClass.getName() + " was requested, but actually, " + ValueChecker.INSTANCE.getClass() + " was already loaded.");
  }
}
