package com.lastbubble.puzzle.solver;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.logic.CNF;
import com.lastbubble.puzzle.logic.Formula;
import com.lastbubble.puzzle.logic.VarSet;

import java.util.Arrays;
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

  protected Var<T> varFor(T data) { return vars.add(data); }

  public void add(Formula formula) {
    CNF.convert(formula).forEach(x ->
      addClauseSafely( new VecInt(x.stream().mapToInt(atomicFormulaToInt).toArray()))
    );
  }

  private void addClauseSafely(VecInt literals) {
    try { solver.addClause(literals); }
    catch (ContradictionException e) { throw new RuntimeException("addClause() failed: " + e, e); }
  }

  public Stream<T> solve() throws Exception {
    int[] model = solver.findModel();

    return Arrays.stream(model)
      .filter(x -> x > 0)
      .mapToObj(vars::varFor)
      .map(Var::data);
  }
}
