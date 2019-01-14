package com.lastbubble.puzzle.issue.oct2017;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.GridPrinter;
import com.lastbubble.puzzle.Pos;
import com.lastbubble.puzzle.solver.Solver;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

abstract class OddLetterOut implements Runnable {

  protected Solver<Cell<Character>> solver = new Solver<>();

  @Override public void run() {
    addConstraints();
    solvePuzzle();
  }

  private final void solvePuzzle() {

    while (true) {

      Grid.Builder<Character> gridBuilder = Grid.builder(Character.class);

      try {
        Set<Cell<Character>> solution = solver.solve();

        if (solution.isEmpty()) { break; }

        solution.stream().forEach(c -> gridBuilder.add(c));

        solver.add(not(allOf(solution.stream().map(solver::varFor))));

      } catch (Exception e) {
        System.out.println("Caught " + e);
        e.printStackTrace(System.out);
        break;
      }

      Grid<Character> grid = gridBuilder.build();

      if (isValid(grid)) { print(grid); }
    }
  }

  protected abstract void addConstraints();

  protected abstract boolean isValid(Grid<Character> grid);

  protected Optional<Pos> findValueIn(Grid<Character> grid, char c) {
    return posStream().filter(p -> grid.valueAt(p).orElse(' ') == c).findFirst();
  }

  protected Cell<Character> cell(Pos pos, char c) { return Cell.at(pos).withValue(c); }

  protected Cell<Character> cell(int x, int y, char c) { return Cell.at(x, y).withValue(c); }

  protected Stream<Pos> posStream() {
    return IntStream.range(0, 25).mapToObj(n -> {
      int row = n / 5;
      return Pos.at(n - (5 * row), row);
    });
  }

  protected Stream<Character> alphabetStream() {
    return IntStream.range(0, 26).mapToObj(n -> (char) ('A' + n));
  }

  // move to Pos
  protected Optional<Pos> validPos(int x, int y) {
    return (x >= 0 && x <= 4 && y >= 0 && y <= 4) ? Optional.of(Pos.at(x, y)) : Optional.<Pos>empty();
  }

  // move to Pos
  protected Stream<Pos> neighborsOf(Pos pos) {
    int x = pos.x(), y = pos.y();
    return Stream.of(
        validPos(x - 1, y - 1),
        validPos(    x, y - 1),
        validPos(x + 1, y - 1),
        validPos(x - 1,     y),
        validPos(x + 1,     y),
        validPos(x - 1, y + 1),
        validPos(    x, y + 1),
        validPos(x + 1, y + 1)
      ).filter(Optional::isPresent).map(Optional::get);
  }
  
  private final GridPrinter<Character> gridPrinter = new GridPrinter<Character>(c -> c);

  protected void print(Grid<Character> grid) {
    gridPrinter.printTo( new PrintWriter(System.out, true), grid);
  }
}
