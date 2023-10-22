package com.github.dakusui.ut.thincrest.sadbox;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
public class PersonAssert extends AbstractAssert<PersonAssert, Person> {

    public PersonAssert(Person actual) {
        super(actual, PersonAssert.class);
    }

    // Static entry point
    public static PersonAssert assertThat(Person actual) {
        return new PersonAssert(actual);
    }

    // Leverage existing String assertions for the name property
    public PersonAssert hasName(String expectedName) {
        Assertions.assertThat(actual.getName()).isEqualTo(expectedName);
        return this;
    }

    // Leverage existing Integer assertions for the age property
    public PersonAssert hasAgeLessThanOrEqualTo(int maxAge) {
        Assertions.assertThat(actual.getAge()).isLessThanOrEqualTo(maxAge);
        return this;
    }
}


