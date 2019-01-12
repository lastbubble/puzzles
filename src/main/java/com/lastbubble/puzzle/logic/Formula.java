package com.lastbubble.puzzle.logic;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Formula {

  public static <T> Var<T> var(T data) { return new Var<T>(data); }

  public static Not not(Formula target) { return new Not(target); }

  public static <T> NotVar<T> not(Var<T> var) { return new NotVar<T>(var); }

  public static And and(Formula left, Formula right) { return new And(left, right); }

  public static Or or(Formula left, Formula right) { return new Or(left, right); }

  public static Implies implies(Formula left, Formula right) { return new Implies(left, right); }

  public static AllOf allOf(Formula... targets) { return allOf(Stream.of(targets)); }

  public static AllOf allOf(Stream<Formula> targets) { return new AllOf(targets); }

  public static AnyOf anyOf(Formula... targets) { return anyOf(Stream.of(targets)); }

  public static AnyOf anyOf(Stream<Formula> targets) { return new AnyOf(targets); }

  public static boolean evaluate(Formula formula, Predicate<Formula> truth) {
    return formula.match(
      var -> truth.test(var),
      not -> !evaluate(not.target(), truth),
      and -> evaluate(and.left(), truth) && evaluate(and.right(), truth),
      or -> evaluate(or.left(), truth) || evaluate(or.right(), truth),
      implies -> evaluate(implies.left(), truth) ? evaluate(implies.right(), truth) : true,
      allOf -> allOf.targets().allMatch(x -> evaluate(x, truth)),
      anyOf -> anyOf.targets().anyMatch(x -> evaluate(x, truth))
    );
  }

  private Formula() { }

  public abstract <T> T match(
    Function<Var<?>, T> var,
    Function<Not, T> not,
    Function<And, T> and,
    Function<Or, T> or,
    Function<Implies, T> implies,
    Function<AllOf, T> allOf,
    Function<AnyOf, T> anyOf
  );

  public interface AtomicFormula<D> {
    Var<D> var();
    <T> T match(Function<Var<?>, T> var, Function<NotVar<?>, T> not);
  }

  public static class Var<D> extends Formula implements AtomicFormula<D> {
    private final D data;

    private Var(D data) { this.data = data; }

    public D data() { return data; }

    @Override public Var<D> var() { return this; }

    @Override public <T> T match(
      Function<Var<?>, T> var,
      Function<Not, T> not,
      Function<And, T> and,
      Function<Or, T> or,
      Function<Implies, T> implies,
      Function<AllOf, T> allOf,
      Function<AnyOf, T> anyOf
    ) {
      return var.apply(this);
    }

    @Override public <T> T match(Function<Var<?>, T> var, Function<NotVar<?>, T> not) {
      return var.apply(this);
    }
  }

  public static class Not extends Formula {
    private final Formula target;

    private Not(Formula target) { this.target = target; }

    public Formula target() { return target; }

    @Override public <T> T match(
      Function<Var<?>, T> var,
      Function<Not, T> not,
      Function<And, T> and,
      Function<Or, T> or,
      Function<Implies, T> implies,
      Function<AllOf, T> allOf,
      Function<AnyOf, T> anyOf
    ) {
      return not.apply(this);
    }
  }

  public static class NotVar<D> extends Not implements AtomicFormula<D> {
    private final Var<D> var;

    private NotVar(Var<D> var) { super(var); this.var = var; }

    @Override public Var<D> var() { return var; }

    @Override public <T> T match(Function<Var<?>, T> var, Function<NotVar<?>, T> not) {
      return not.apply(this);
    }
  }

  private static abstract class BinaryFormula extends Formula {
    private final Formula left;
    private final Formula right;

    protected BinaryFormula(Formula left, Formula right) {
      this.left = left;
      this.right = right;
    }

    public final Formula left() { return left; }
    public final Formula right() { return right; }
  }

  public static class And extends BinaryFormula {
    private And(Formula left, Formula right) { super(left, right); }

    @Override public <T> T match(
        Function<Var<?>, T> var,
        Function<Not, T> not,
        Function<And, T> and,
        Function<Or, T> or,
        Function<Implies, T> implies,
        Function<AllOf, T> allOf,
        Function<AnyOf, T> anyOf
    ) {
      return and.apply(this);
    }
  }

  public static class Or extends BinaryFormula {
    private Or(Formula left, Formula right) { super(left, right); }

    @Override public <T> T match(
      Function<Var<?>, T> var,
      Function<Not, T> not,
      Function<And, T> and,
      Function<Or, T> or,
      Function<Implies, T> implies,
      Function<AllOf, T> allOf,
      Function<AnyOf, T> anyOf
    ) {
      return or.apply(this);
    }
  }

  public static class Implies extends BinaryFormula {
    private Implies(Formula left, Formula right) { super(left, right); }

    @Override public <T> T match(
      Function<Var<?>, T> var,
      Function<Not, T> not,
      Function<And, T> and,
      Function<Or, T> or,
      Function<Implies, T> implies,
      Function<AllOf, T> allOf,
      Function<AnyOf, T> anyOf
    ) {
      return implies.apply(this);
    }
  }

  private static abstract class NaryFormula extends Formula {
    private final List<Formula> targets;

    protected NaryFormula(Stream<Formula> targets) { this.targets = targets.collect(toList()); }

    public Stream<Formula> targets() { return targets.stream(); }
  }

  public static class AllOf extends NaryFormula {
    private AllOf(Stream<Formula> targets) { super(targets); }

    @Override public <T> T match(
      Function<Var<?>, T> var,
      Function<Not, T> not,
      Function<And, T> and,
      Function<Or, T> or,
      Function<Implies, T> implies,
      Function<AllOf, T> allOf,
      Function<AnyOf, T> anyOf
    ) {
      return allOf.apply(this);
    }
  }

  public static class AnyOf extends NaryFormula {
    private AnyOf(Stream<Formula> targets) { super(targets); }

    @Override public <T> T match(
      Function<Var<?>, T> var,
      Function<Not, T> not,
      Function<And, T> and,
      Function<Or, T> or,
      Function<Implies, T> implies,
      Function<AllOf, T> allOf,
      Function<AnyOf, T> anyOf
    ) {
      return anyOf.apply(this);
    }
  }
}
