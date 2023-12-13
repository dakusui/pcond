package com.github.dakusui.thincrest;

import com.github.dakusui.pcond.fluent.DummyValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DummyList<E> extends ArrayList<E> implements DummyValue {
  public DummyList(Collection<E> collection) {
    this.addAll(collection);
  }

  public static <E> List<E> fromList(List<E> list) {
    return new DummyList<>(list);
  }
}
