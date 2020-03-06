package com.github.dakusui.crest;

import com.github.dakusui.crest.examples.BankAccountExample;
import com.github.dakusui.crest.examples.ExamplesTest;
import com.github.dakusui.crest.ut.*;
import com.github.dakusui.crest.utils.UtilsAll;
import com.github.dakusui.crest.utils.ut.FunctionsTest;
import com.github.dakusui.crest.utils.ut.PredicatesTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BankAccountExample.class,
    CrestFunctionsTest.class,
    CrestPredicatesTest.class,
    CrestTest.class,
    ExamplesTest.class,
    FunctionsTest.class,
    PredicatesTest.class,
    PrintableTest.class,
    InternalUtilsTest.class,
    MatcherTest.class,
    UtilsAll.class,
    Issue27Test.class,
    Issue29Test.class
})
public class All {
}
