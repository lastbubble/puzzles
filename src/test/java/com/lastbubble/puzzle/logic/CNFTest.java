package com.lastbubble.puzzle.logic;

import static com.lastbubble.puzzle.logic.Formula.*;
import static com.lastbubble.puzzle.logic.FormulaMatchers.matching;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertThat;

import java.util.function.Function;

import co.unruly.matchers.StreamMatchers;
import org.junit.Test;

public class CNFTest {

  @Test public void create_forCompleteTestCoverage() { new CNF(); }

  @Test public void implicationsOut() {

    Function<Formula, Formula> transform = CNF::implicationsOut;

    assertThatFormula(transform, a, "a");
    assertThatFormula(transform, implies(a, b), "(-(a) | b)");
    assertThatFormula(transform, not(implies(a, b)), "-((-(a) | b))");
    assertThatFormula(transform, and(implies(a, b), implies(c, d)), "((-(a) | b) & (-(c) | d))");
    assertThatFormula(transform, or(implies(a, b), implies(c, d)), "((-(a) | b) | (-(c) | d))");
    assertThatFormula(transform, allOf(implies(a, b), implies(c, d), implies(e, f)), "(-(a) | b) & (-(c) | d) & (-(e) | f)");
    assertThatFormula(transform, anyOf(implies(a, b), implies(c, d), implies(e, f)), "(-(a) | b) | (-(c) | d) | (-(e) | f)");
  }

  @Test public void negationsIn() {

    Function<Formula, Formula> transform = CNF::negationsIn;

    assertThatFormula(transform, a, "a");
    assertThatFormula(transform, and(a, b), "(a & b)");
    assertThatFormula(transform, or(a, b), "(a | b)");
    assertThatFormula(transform, allOf(a, b, c), "a & b & c");
    assertThatFormula(transform, anyOf(a, b, c), "a | b | c");
    assertThatFormula(transform, not(a), "-(a)");
    assertThatFormula(transform, not(not(a)), "a");
    assertThatFormula(transform, not(and(a, b)), "(-(a) | -(b))");
    assertThatFormula(transform, not(or(a, b)), "(-(a) & -(b))");
    assertThatFormula(transform, not(allOf(a, b, c)), "-(a) | -(b) | -(c)");
    assertThatFormula(transform, not(anyOf(a, b, c)), "-(a) & -(b) & -(c)");
  }

  @Test public void distributeOr() {

    Function<Formula, Formula> transform = CNF::distributeOr;

    assertThatFormula(transform, a, "a");
    assertThatFormula(transform, not(a), "-(a)");
    assertThatFormula(transform, and(a, b), "a & b");
    assertThatFormula(transform, or(a, b), "a | b");
    assertThatFormula(transform, or(and(a, b), c), "(a | c) & (b | c)");
    assertThatFormula(transform, or(a, and(b, c)), "(a | b) & (a | c)");
    assertThatFormula(transform, allOf(a, b, c), "a & b & c");
    assertThatFormula(transform, anyOf(a, b, c), "a | b | c");
  }

  @Test public void convert() {
    assertThatConvert(a, "a");
    assertThatConvert(not(a), "-a");
    assertThatConvert(or(a, b), "a b");
    assertThatConvert(and(a, b), "a", "b");
    assertThatConvert(implies(a, b), "-a b");
    assertThatConvert(implies(a, allOf(b, c, d)), "-a b", "-a c", "-a d");
    assertThatConvert(implies(a, allOf(not(b), not(c), not(d))), "-a -b", "-a -c", "-a -d");
    assertThatConvert(implies(a, anyOf(b, c, d)), "-a b c d");
    assertThatConvert(implies(a, not(anyOf(b, c, d))), "-a -b", "-a -c", "-a -d");
  }

  private static void assertThatFormula(Function<Formula, Formula> f, Formula formula, String s) {
    assertThat(f.apply(formula), matching(s));
  }

  private static void assertThatConvert(Formula formula, String... clauses) {
    assertThat(
      CNF.convert(formula).map(x -> x.stream().map(asString).collect(joining(" "))),
      StreamMatchers.contains(clauses)
    );
  }

  private static Function<AtomicFormula<?>, String> asString = f -> f.match(
    var -> var.data().toString(),
    not -> "-" + not.var().data()
  );

  private static final Var<?> a = var("a");
  private static final Var<?> b = var("b");
  private static final Var<?> c = var("c");
  private static final Var<?> d = var("d");
  private static final Var<?> e = var("e");
  private static final Var<?> f = var("f");
}
