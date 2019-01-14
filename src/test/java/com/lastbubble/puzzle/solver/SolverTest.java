package com.lastbubble.puzzle.solver;

import static com.lastbubble.puzzle.logic.Formula.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SolverTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  private final Solver<String> solver = new Solver<>();

  private final Var<String> a = solver.varFor("a");
  private final Var<String> b = solver.varFor("b");
  private final Var<String> c = solver.varFor("c");
  private final Var<String> d = solver.varFor("d");
  private final Var<String> e = solver.varFor("e");

  private final ISolver mockSatSolver = mock(ISolver.class);

  @Test public void addClause_whenClauseInvalid() throws Exception {
    when(mockSatSolver.addClause(any())).thenThrow( new ContradictionException());

    thrown.expect(RuntimeException.class);

    new Solver<String>(mockSatSolver).add(a);
  }

  @Test public void addAtMost_whenClauseInvalid() throws Exception {
    when(mockSatSolver.addAtMost(any(),anyInt())).thenThrow( new ContradictionException());

    thrown.expect(RuntimeException.class);

    new Solver<String>(mockSatSolver).addAtMost(1, Stream.of(a, b, c));
  }

  @Test public void addExactly_whenClauseInvalid() throws Exception {
    when(mockSatSolver.addExactly(any(), anyInt())).thenThrow( new ContradictionException());

    thrown.expect(RuntimeException.class);

    new Solver<String>(mockSatSolver).addExactly(1, Stream.of(a, b, c));
  }

  @Test public void solve_singleVar() throws Exception {
    solver.add(a);

    assertThat(solver.solve(), contains("a"));
  }

  @Test public void solve_withNot() throws Exception {
    solver.add(a);
    solver.add(not(b));

    assertThat(solver.solve(), contains("a"));
  }

  @Test public void solve_withAnd() throws Exception {
    solver.add(and(a, b));

    assertThat(solver.solve(), contains("a", "b"));
  }

  @Test public void solve_implies() throws Exception {
    solver.add(a);
    solver.add(implies(a, b));

    assertThat(solver.solve(), contains("a", "b"));
  }

  @Test public void solve_impliesAnyOf() throws Exception {
    solver.add(a);
    solver.add(implies(a, anyOf(b, c, d)));
    solver.add(and(not(c), not(d)));

    assertThat(solver.solve(), contains("a", "b"));
  }

  @Test public void solve_impliesNotAnyOf() throws Exception {
    solver.add(a);
    solver.add(implies(a, not(anyOf(b, c, d))));
    solver.add(or(b, e));

    assertThat(solver.solve(), contains("a", "e"));
  }

  @Test public void solve_addAtMost() throws Exception {
    solver.add(a);
    solver.add(implies(a, b));
    solver.addAtMost(1, Stream.of(b, c, d));

    assertThat(solver.solve(), contains("a", "b"));
  }

  @Test public void solve_addExactly() throws Exception {
    solver.add(a);
    solver.add(implies(a, b));
    solver.addExactly(1, Stream.of(b, c, d));

    assertThat(solver.solve(), contains("a", "b"));
  }

  @Test public void solve_unsolvable() throws Exception {
    solver.add(or(a, b));
    solver.add(not(a));
    solver.add(not(b));

    assertTrue(solver.solve().isEmpty());
  }
}
