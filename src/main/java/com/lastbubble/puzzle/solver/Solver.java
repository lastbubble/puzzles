package com.lastbubble.puzzle.solver;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.toSet;

import com.lastbubble.puzzle.logic.CNF;
import com.lastbubble.puzzle.logic.Formula;
import com.lastbubble.puzzle.logic.VarSet;

import java.util.Arrays;
import java.util.Set;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;

public class Solver<T> {
  private final ISolver solver;
  private final VarSet<T> vars = new VarSet<T>();

  private final ToIntFunction<AtomicFormula<?>> atomicFormulaToInt = x -> x.match(
    var -> vars.idFor(var),
    not -> -1 * vars.idFor((Var<?>) not.target())
  );

  public Solver() { this(SolverFactory.instance().defaultSolver()); }

  Solver(ISolver solver) { this.solver = solver; }

  public Var<T> varFor(T data) { return vars.add(data); }

  public void add(Formula formula) {
    CNF.convert(formula).forEach(x -> addClauseSafely(toVecInt(x.stream())));
  }

  private VecInt toVecInt(Stream<? extends AtomicFormula<?>> vars) {
    return new VecInt(vars.mapToInt(atomicFormulaToInt).toArray());
  }

  public void addAtMost(int n, Stream<? extends AtomicFormula<?>> vars) {
    try { solver.addAtMost(toVecInt(vars), n); }
    catch (ContradictionException e) { handleContradiction("addAtMost()", e); }
  }

  public void addExactly(int n, Stream<? extends AtomicFormula<?>> vars) {
    try { solver.addExactly(toVecInt(vars), n); }
    catch (ContradictionException e) { handleContradiction("addExactly()", e); }
  }

  private void addClauseSafely(VecInt literals) {
    try { solver.addClause(literals); }
    catch (ContradictionException e) { handleContradiction("addClause()", e); }
  }

  private void handleContradiction(String message, ContradictionException e) {
    throw new RuntimeException(message + ": " + e, e);
  }

  public Set<T> solve() throws Exception {
    int[] model = solver.findModel();

    if (model == null) { model = new int[0]; }

    return Arrays.stream(model)
      .filter(x -> x > 0)
      .mapToObj(vars::varFor)
      .map(Var::data)
      .collect(toSet());
  }
}
