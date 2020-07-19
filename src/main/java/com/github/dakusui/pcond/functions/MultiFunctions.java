package com.github.dakusui.pcond.functions;

import com.github.dakusui.pcond.core.multi.MultiFunction;

import java.util.List;
import java.util.function.Predicate;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

public enum MultiFunctions {
;
  public static MultiFunction<Boolean> isEqualTo() {
    return multiPredicate((args) -> ((String) args.get(0)).endsWith((String) args.get(1)))
        .name("endsWith")
        .addParameters(asList(String.class, String.class))
        .$();
  }

  public static MultiFunction<Boolean> endsWith() {
    return multiPredicate((args) -> ((String) args.get(0)).endsWith((String) args.get(1)))
        .name("endsWith")
        .addParameters(asList(String.class, String.class))
        .$();
  }

  public static MultiFunction<Boolean> startsWith() {
    return multiPredicate((args) -> ((String) args.get(0)).endsWith((String) args.get(1)))
        .name("startsWith")
        .addParameters(asList(String.class, String.class))
        .$();
  }

  public static MultiFunction.Builder<Boolean> multiPredicate(Predicate<List<Object>> predicateBody) {
    return new MultiFunction.Builder<>(args -> requireNonNull(predicateBody).test(args));
  }
}
