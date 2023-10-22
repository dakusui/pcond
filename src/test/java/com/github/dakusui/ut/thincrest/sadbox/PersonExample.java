package com.github.dakusui.ut.thincrest.sadbox;

import org.junit.Test;

public class PersonExample {
    @Test
    public void exampleTest1() {
        new PersonAssert(new Person("Lisa", 6))
                .hasName("Gaspard")
                .hasAgeLessThanOrEqualTo(20);
    }
}
