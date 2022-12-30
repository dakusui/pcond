package com.github.dakusui.pcond.propertybased.tests.suites;

import com.github.dakusui.pcond.propertybased.tests.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
    AllOfPredicateTest.class,
    AnyOfPredicateTest.class,
    NegatedPredicateTest.class,
    SimplePredicateTest.class,
    StreamAllMatchPredicateTest.class,
    StreamNoneMatchPredicateTest.class,
    TransformAndCheckPredicateTest.class,
    VariableBundlePredicateTest.class
})
public class PropertyBasedTestSuite {
}
