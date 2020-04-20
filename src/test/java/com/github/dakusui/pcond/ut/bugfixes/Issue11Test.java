package com.github.dakusui.pcond.ut.bugfixes;

import com.github.dakusui.pcond.utils.TestBase;
import org.junit.Test;

import static com.github.dakusui.pcond.Preconditions.requireArgument;
import static com.github.dakusui.pcond.functions.Functions.elementAt;
import static com.github.dakusui.pcond.functions.Functions.size;
import static com.github.dakusui.pcond.functions.Predicates.and;
import static com.github.dakusui.pcond.functions.Predicates.isEqualTo;
import static com.github.dakusui.pcond.functions.Predicates.isInstanceOf;
import static com.github.dakusui.pcond.functions.Predicates.isNotNull;
import static com.github.dakusui.pcond.functions.Predicates.when;
import static com.github.dakusui.pcond.utils.TestUtils.lineAt;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class Issue11Test extends TestBase {
	@Test(expected = IllegalArgumentException.class)
	public void whenReproduceIssue() {
		Object[] args = new Object[]{1};
		try {
			requireArgument(asList(args),
					and(when(size()).then(isEqualTo(1)),
							when(elementAt(0)).then(and(isNotNull(), isInstanceOf(String.class)))));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			assertThat(lineAt(e.getMessage(), 1), containsString("&&"));
			assertThat(lineAt(e.getMessage(), 2), allOf(containsString("size("), containsString(")")));
			assertThat(lineAt(e.getMessage(), 3), containsString("isEqualTo[1]"));
			assertThat(lineAt(e.getMessage(), 4), containsString("at[0]"));
			assertThat(lineAt(e.getMessage(), 5), containsString("&&"));
			assertThat(lineAt(e.getMessage(), 6), containsString("isNotNull"));
			assertThat(lineAt(e.getMessage(), 7), containsString("isInstanceOf"));
			throw e;
		}
	}
}
