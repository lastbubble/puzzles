package com.lastbubble.puzzle.logic;

import static com.lastbubble.puzzle.logic.Formula.*;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.function.Predicate;
import java.util.stream.Stream;

import co.unruly.matchers.StreamMatchers;
import org.junit.Test;

public class FormulaTest {

  @Test public void assignVar() {
    String data = "data";
    Var<String> var = var(data);

    assertThat(var.data(), sameInstance(data));
  }

  @Test public void varIsAtomic() {
    String data = "data";
    Var<String> v = var(data);

    assertThat(v.var(), sameInstance(v));
    assertTrue(v.match(
      var -> { return var.data().equals(data); },
      not -> { return false; }
    ));
  }

  @Test public void assignNot() {
    Not not = not(formula1);
    
    assertThat(not.target(), sameInstance(formula1));
  }

  @Test public void assignNotVar() {
    String data = "data";
    NotVar<String> var = not(var(data));

    assertThat(var.var().data(), sameInstance(data));
  }

  @Test public void notVarIsAtomic() {
    String data = "data";

    assertTrue(not(var(data)).match(
      var -> { return true; },
      not -> { return not.var().data().equals(data); }
    ));
  }

  @Test public void assignAnd() {
    And and = and(formula1, formula2);

    assertThat(and.left(), sameInstance(formula1));
    assertThat(and.right(), sameInstance(formula2));
  }

  @Test public void assignOr() {
    Or or = or(formula1, formula2);

    assertThat(or.left(), sameInstance(formula1));
    assertThat(or.right(), sameInstance(formula2));
  }

  @Test public void assignImplies() {
    Implies implies = implies(formula1, formula2);

    assertThat(implies.left(), sameInstance(formula1));
    assertThat(implies.right(), sameInstance(formula2));
  }

  @Test public void assignAllOf() {
    AllOf allOf = allOf(formula1, formula2, formula3);

    assertThat(allOf.targets(), StreamMatchers.contains(formula1, formula2, formula3));
  }

  @Test public void assignAllOf_usingStream() {
    AllOf allOf = allOf(Stream.of(formula1, formula2, formula3));

    assertThat(allOf.targets(), StreamMatchers.contains(formula1, formula2, formula3));
  }

  @Test public void assignAnyOf() {
    AnyOf anyOf = anyOf(formula1, formula2, formula3);

    assertThat(anyOf.targets(), StreamMatchers.contains(formula1, formula2, formula3));
  }

  @Test public void assignAnyOf_usingStream() {
    AnyOf anyOf = anyOf(Stream.of(formula1, formula2, formula3));

    assertThat(anyOf.targets(), StreamMatchers.contains(formula1, formula2, formula3));
  }

  @Test public void evaluateLogicUsingMatch() {
    Predicate<Formula> truth = formula -> formula == t;

    assertTrue(evaluate(t, truth));
    assertFalse(evaluate(f, truth));

    assertTrue(evaluate(not(f), truth));
    assertFalse(evaluate(not(t), truth));

    assertTrue(evaluate(and(t, t), truth));
    assertFalse(evaluate(and(t, f), truth));
    assertFalse(evaluate(and(f, t), truth));
    assertFalse(evaluate(and(f, f), truth));

    assertTrue(evaluate(not(and(f, f)), truth));
    assertFalse(evaluate(not(and(t, t)), truth));

    assertTrue(evaluate(or(t, t), truth));
    assertTrue(evaluate(or(t, f), truth));
    assertTrue(evaluate(or(f, t), truth));
    assertFalse(evaluate(or(f, f), truth));

    assertTrue(evaluate(implies(t, t), truth));
    assertFalse(evaluate(implies(t, f), truth));
    assertTrue(evaluate(implies(f, t), truth));
    assertTrue(evaluate(implies(f, f), truth));

    assertTrue(evaluate(allOf(t, t, t), truth));
    assertFalse(evaluate(allOf(f, t, t), truth));
    assertFalse(evaluate(allOf(t, f, t), truth));
    assertFalse(evaluate(allOf(t, t, f), truth));
    assertFalse(evaluate(allOf(f, f, f), truth));

    assertTrue(evaluate(anyOf(t, t, t), truth));
    assertTrue(evaluate(anyOf(f, t, t), truth));
    assertTrue(evaluate(anyOf(t, f, t), truth));
    assertTrue(evaluate(anyOf(t, t, f), truth));
    assertFalse(evaluate(allOf(f, f, f), truth));
  }

  private static final Formula t = var(true);
  private static final Formula f = var(false);

  private static final Formula formula1 = mock(Formula.class, "formula1");
  private static final Formula formula2 = mock(Formula.class, "formula2");
  private static final Formula formula3 = mock(Formula.class, "formula3");
}
