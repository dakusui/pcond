package com.github.dakusui.crest.matcherbuilders;

import com.github.dakusui.crest.Crest;
import com.github.dakusui.crest.utils.InternalUtils;
import com.github.dakusui.pcond.functions.TransformingPredicate;
import com.github.dakusui.pcond.functions.Predicates;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ListMatcherBuilder<IN, ENTRY, SELF extends ListMatcherBuilder<IN, ENTRY, SELF>> extends ObjectMatcherBuilder<IN, List<ENTRY>, SELF> {
  protected ListMatcherBuilder(Function<? super IN, ? extends List<ENTRY>> function) {
    super(function);
  }

  public SELF containsAll(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsAll%s", InternalUtils.summarize(collection)),
            Predicates.isEmpty(),
            Crest.function(
                String.format("notCovered%s", InternalUtils.summarize(collection)),
                objects -> collection.stream(
                ).filter(
                    each -> !objects.contains(each)
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF containsOnly(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsOnly%s", InternalUtils.summarize(collection)),
            Predicates.isEmpty(),
            Crest.function(
                String.format("extra%s", InternalUtils.summarize(collection)),
                objects -> objects.stream(
                ).filter(
                    each -> !collection.contains(each)
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF containsExactly(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsExactly%s", InternalUtils.summarize(collection)),
            Predicates.isEmpty(),
            Crest.function(
                String.format("difference%s", InternalUtils.summarize(collection)),
                objects -> Stream.concat(
                    objects.stream(), collection.stream()
                ).filter(
                    each -> !(collection.contains(each) && objects.contains(each))
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF containsNone(Collection<?> collection) {
    return this.check(
        new TransformingPredicate<Collection<?>, Collection<?>>(
            String.format("containsNone%s", InternalUtils.summarize(collection)),
            Predicates.isEmpty(),
            Crest.function(
                String.format("contained%s", InternalUtils.summarize(collection)),
                objects -> objects.stream(
                ).filter(
                    collection::contains
                ).collect(
                    toList()
                ))
        )
    );
  }

  public SELF contains(Object entry) {
    return this.check(Predicates.contains(entry));
  }

  public SELF isEmpty() {
    return this.check(Predicates.isEmpty());
  }

  public SELF isNotEmpty() {
    return this.check(Predicates.isEmpty().negate());
  }

  public SELF allMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Crest.predicate(
            String.format("allMatch[%s]", predicate),
            entries -> entries.stream().allMatch(predicate)
        ));
  }

  public SELF anyMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Crest.predicate(
            String.format("anyMatch[%s]", predicate),
            entries -> entries.stream().anyMatch(predicate)
        ));
  }

  public SELF noneMatch(Predicate<? super ENTRY> predicate) {
    return this.check(
        Crest.predicate(
            String.format("noneMatch[%s]", predicate),
            entries -> entries.stream().noneMatch(predicate)
        ));
  }

}
