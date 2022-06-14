package com.github.dakusui.pcond.provider.impls;

import com.github.dakusui.pcond.provider.AssertionProvider;

import java.util.Properties;

public class AssertionProviderImpl implements AssertionProvider {

  private final Configuration configuration;

  public AssertionProviderImpl(Properties properties) {
    this.configuration = new Configuration.Builder(properties)
        .assertionProviderClass(this.getClass())
        .useEvaluator(true)
        .build();
  }

  @Override
  public Configuration configuration() {
    return this.configuration;
  }
}
