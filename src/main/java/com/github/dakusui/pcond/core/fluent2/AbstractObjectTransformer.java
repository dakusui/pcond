package com.github.dakusui.pcond.core.fluent2;

/**
 * A base interface for a transformer that defines operations common to all object types.
 *
 * @param <OIN> The type of original input value.
 */
public interface AbstractObjectTransformer<TX extends AbstractObjectTransformer<TX, V, OIN, T>, V extends Checker<V, OIN, T>, OIN, T> {

}
