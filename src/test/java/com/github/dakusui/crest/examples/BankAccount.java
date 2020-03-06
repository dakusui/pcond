package com.github.dakusui.crest.examples;

import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static com.github.dakusui.crest.Crest.*;
import static java.util.Arrays.asList;

@SuppressWarnings("WeakerAccess")
public class BankAccount {
  public static class Record {
    public enum Type {
      WITHDRAW,
      DEPOSIT,
      TRANSFER
    }

    private final Record.Type type;
    private final int         amount;

    public Record(Record.Type type, int amount) {
      this.type = type;
      this.amount = amount;
    }

    public Record.Type getType() {
      return this.type;
    }

    public int getAmount() {
      return this.amount;
    }

    @Override
    public String toString() {
      return String.format("%s:%s", this.getType(), this.getAmount());
    }
  }

  final String name;
  int          balance;
  List<Record> history = new LinkedList<>();

  public BankAccount(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public int getBalance() {
    return balance;
  }

  public void deposit(int amount) {
    this.balance += amount;
    this.history.add(new Record(Record.Type.TRANSFER /* BUG! */, amount));
  }

  public void withdraw(int amount) {
    this.balance -= amount;
    this.history.add(new Record(Record.Type.WITHDRAW, amount));
  }

  public List<Record> getHistory() {
    return Collections.unmodifiableList(this.history);
  }

  @Override
  public String toString() {
    return "BankAcccount:" + this.name;
  }
}
