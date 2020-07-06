package com.github.dakusui.pcond.functions.chain.compat;

import com.github.dakusui.pcond.functions.Printables;
import com.github.dakusui.pcond.functions.chain.CallChain;
import com.github.dakusui.pcond.functions.chain.ChainUtils;

import java.util.function.Function;

public class CompatUtils {
  public static <I, E> Function<? super I, ? extends E> invokeOn(Object self, String methodName, Object... args) {
    return Printables.function(
        self == CallChain.THIS
            ? () -> String.format("%s%s", methodName, ChainUtils.summarize(args))
            : () -> String.format("->%s.%s%s", self, methodName, ChainUtils.summarize(args)),
        (I target) -> ChainUtils.invokeMethod(
            ChainUtils.replaceTarget(self, target),
            methodName,
            args
        ));
  }
}
