package com.github.dakusui.pcond.internals;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public enum InternalUtils {
  ;

  public static String formatObject(Object value) {
    if (value instanceof String)
      return format("\"%s\"", value);
    if (value instanceof Character)
      return format("'%s'", value);
    return format("%s", value);
  }

  public static String summarize(Object value) {
    if (value == null)
      return "null";
    if (value instanceof Collection) {
      Collection<?> collection = (Collection<?>) value;
      if (collection.size() < 4)
        return format("(%s)",
            String.join(
                ",",
                (List<String>) collection.stream().map(InternalUtils::summarize).collect(toList())
            ));
      Iterator<?> i = collection.iterator();
      return format("(%s,%s,%s...;%s)",
          summarize(i.next()),
          summarize(i.next()),
          summarize(i.next()),
          collection.size()
      );
    }
    if (value instanceof Object[])
      return summarize(asList((Object[]) value));
    if (value instanceof String) {
      String s = (String) value;
      if (s.length() > 20)
        s = s.substring(0, 12) + "..." + s.substring(s.length() - 5);
      return format("\"%s\"", s);
    }
    String ret = value.toString();
    ret = ret.contains("$")
        ? ret.substring(ret.lastIndexOf("$") + 1)
        : ret;
    return ret;
  }
}
