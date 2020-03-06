package com.github.dakusui.crest.utils;

import com.github.dakusui.crest.utils.ut.FunctionsTest;
import com.github.dakusui.crest.utils.ut.ParameterizedFunctionsTest;
import com.github.dakusui.crest.utils.ut.ParameterizedPredicatesTest;
import com.github.dakusui.crest.utils.ut.PredicatesTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    FunctionsTest.class,
    ParameterizedFunctionsTest.class,
    ParameterizedPredicatesTest.class,
    PredicatesTest.class,
})
public class UtilsAll {
}
