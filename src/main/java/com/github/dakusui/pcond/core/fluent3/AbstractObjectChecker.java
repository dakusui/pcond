package com.github.dakusui.pcond.core.fluent3;

import com.github.dakusui.pcond.core.refl.MethodQuery;
import com.github.dakusui.pcond.forms.Predicates;

import static com.github.dakusui.pcond.forms.Functions.parameter;

public interface AbstractObjectChecker<
    V extends Checker<V, R, OIN, T>,
    R extends Matcher<R, R, OIN, OIN>,
    OIN,
    T> extends Checker<
    V, R, OIN, T
    > {

  default V isNotNull() {
    return this.appendPredicateAsChild(Predicates.isNotNull());
  }

  default V isNull() {
    return this.appendPredicateAsChild(Predicates.isNull());
  }

  /**
   * Checks the object with an argument if they are "equal", using `equalTo` method.
   *
   * @return the updated object.
   */
  default V isEqualTo(Object anotherObject) {
    return this.appendPredicateAsChild(Predicates.isEqualTo(anotherObject));
  }

  default V isSameReferenceAs(Object anotherObject) {
    return this.appendPredicateAsChild(Predicates.isSameReferenceAs(anotherObject));
  }

  default V isInstanceOf(Class<?> klass) {
    return this.appendPredicateAsChild(Predicates.isInstanceOf(klass));
  }

  default V invoke(String methodName, Object... args) {
    return this.appendPredicateAsChild(Predicates.callp(MethodQuery.instanceMethod(parameter(), methodName, args)));
  }

  default V invokeStatic(Class<?> klass, String methodName, Object... args) {
    return this.appendPredicateAsChild(Predicates.callp(MethodQuery.classMethod(klass, methodName, args)));
  }
}
