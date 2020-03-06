package com.github.dakusui.crest.examples;

import com.github.dakusui.crest.utils.TestBase;
import com.github.dakusui.pcond.functions.Predicates;
import org.junit.Test;

import java.util.stream.Stream;

import static com.github.dakusui.crest.Crest.*;
import static java.util.Arrays.asList;

public class BankAccountExample extends TestBase {
  @Test
  public void givenAccountOfJohnDoe$whenDoDepositAndWithdraw$thenLookingAllRight$thenFail() {
    BankAccount bankAccount = new BankAccount("John Doe");
    bankAccount.deposit(1000);
    bankAccount.withdraw(110);
    Stream.of(
        asList("name", bankAccount.getName()),
        asList("balance", bankAccount.getBalance()),
        asList("history", bankAccount.getHistory())
    ).forEach(
        System.out::println
    );

    assertThat(
        bankAccount,
        allOf(
            asString("getName").equalTo("John Doe").$(),
            asInteger("getBalance").equalTo(890).$(),
            asListOf(BankAccount.Record.class, call("getHistory").$())
                .check(
                    call("get", 0).andThen("getType").$(),
                    Predicates.equalTo(BankAccount.Record.Type.DEPOSIT)
                ).$()
        )
    );
  }
}
