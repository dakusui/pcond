package com.github.dakusui.shared;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

public class ReportParser {
  private final String   message;
  private final String[] lines;

  public ReportParser(String message) {
    this.message = message;
    this.lines = this.message().split("\n");
  }

  public String message() {
    return this.message;
  }

  public String[] lines() {
    return this.lines;
  }

  public List<String> summaryText() {
    return extractSummaryFrom(this.lines());
  }

  public Summary summary() {
    return new Summary(summaryText().toArray(new String[0]));
  }

  public List<Detail> details() {
    return Detail.parseDetails(copyOfRange(lines, summaryText().size(), lines.length));
  }

  @Override
  public String toString() {
    return summary().toString();
  }

  private static List<String> extractSummaryFrom(String[] lines) {
    List<String> ret = new ArrayList<>(lines.length);
    for (String each : lines) {
      if ("".equals(each))
        break;
      ret.add(each);
    }
    return ret;
  }

  public static class Detail {
    final String   subject;
    final String[] body;

    public Detail(String subject, String[] body) {
      this.subject = subject;
      this.body = body;
    }

    public String subject() {
      return this.subject;
    }

    public List<String> body() {
      return asList(this.body);
    }

    public static List<Detail> parseDetails(String[] details) {
      final int SEARCHING_SUBJECT = 0;
      final int BODY = 1;

      List<Detail> ret = new LinkedList<>();
      int state = SEARCHING_SUBJECT;

      String subject = null;
      List<String> body = null;
      int numSeparators = 0;
      for (String each : details) {
        //noinspection ConstantConditions
        assert state == SEARCHING_SUBJECT || state == BODY;
        if (state == SEARCHING_SUBJECT) {
          assert "".equals(each) || each.startsWith(".Detail of failure");
          if ("".equals(each))
            continue;
          subject = each;
          body = new LinkedList<>();
          state = BODY;
        } else {
          if (numSeparators == 0) {
            assert "----".equals(each);
            numSeparators++;
          } else if (numSeparators == 1) {
            if ("----".equals(each)) {
              ret.add(new Detail(subject, body.toArray(new String[0])));
              state = SEARCHING_SUBJECT;
              subject = null;
              body = null;
              numSeparators = 0;
            } else {
              body.add(each);
            }
          } else
            assert false;
        }
      }
      return ret;
    }
  }

  public static class Summary {
    private final String[] text;

    public Summary(String[] summary) {
      this.text = summary;
    }

    public List<Record> records() {
      return asList(this.text().stream().map(Record::new).toArray(Record[]::new));
    }

    public List<String> text() {
      return asList(this.text);
    }

    public String toString() {
      return records().toString().replaceAll(" +", "");
    }

    public static class Record {
      static final  Pattern INDEX_PATTERN = Pattern.compile("^\\[(\\d+)].+");
      private final String  line;
      private final String  in;
      private final String  op;
      private final String  out;

      private final int detailIndex;

      Record(String line) {
        this.line = line;
        this.detailIndex = extractIndex(this.line());
        String[] fields = Arrays.stream(this.line().replaceAll("^\\[\\d+]", "").split("->")).map(String::trim).toArray(String[]::new);
        System.out.println(Arrays.toString(fields));
        assert fields.length == 2 || fields.length == 3;
        if (fields.length == 2) {
          this.in = null;
          this.op = fields[0];
          this.out = fields[1];
        } else {
          this.in = fields[0];
          this.op = fields[1];
          this.out = fields[0];
        }
      }

      private int extractIndex(String line) {
        Matcher m = INDEX_PATTERN.matcher(line);
        if (m.matches())
          return Integer.parseInt(m.group(1));
        return -1;
      }

      Optional<String> in() {
        return Optional.ofNullable(this.in);
      }

      String op() {
        return this.op;
      }

      String out() {
        return this.out;
      }

      String line() {
        return this.line;
      }

      OptionalInt detailIndex() {
        return detailIndex >= 0 ? OptionalInt.of(detailIndex) : OptionalInt.empty();
      }

      @Override
      public String toString() {
        return String.format("Summary.Record:[in:%s; op:%s; out:%s; detailIndex:%s]", in(), op(), out(), detailIndex());
      }
    }
  }
}
