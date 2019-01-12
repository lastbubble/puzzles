package com.lastbubble.puzzle.solver;

import static com.lastbubble.puzzle.logic.Formula.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

import co.unruly.matchers.StreamMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SolverTest {
  @Rule public ExpectedException thrown = ExpectedException.none();

  private final Solver<String> solver = new Solver<>();
  private final Var<String> a = solver.varFor("a");
  private final Var<String> b = solver.varFor("b");

  @Test public void solve_whenClauseInvalid() throws Exception {
    ISolver mockSatSolver = mock(ISolver.class);

    when(mockSatSolver.addClause(any())).thenThrow( new ContradictionException());

    thrown.expect(RuntimeException.class);

    new Solver<String>(mockSatSolver).add(a);
  }

  @Test public void solve_singleVar() throws Exception {
    solver.add(a);

    assertThat(solver.solve(), StreamMatchers.contains("a"));
  }

  @Test public void solve_withNot() throws Exception {
    solver.add(a);
    solver.add(not(b));

    assertThat(solver.solve(), StreamMatchers.contains("a"));
  }

  @Test public void solve_withAnd() throws Exception {
    solver.add(and(a, b));

    assertThat(solver.solve(), StreamMatchers.contains("a", "b"));
  }

  @Test public void solve_implies() throws Exception {
    solver.add(a);
    solver.add(implies(a, b));

    assertThat(solver.solve(), StreamMatchers.contains("a", "b"));
  }
}
