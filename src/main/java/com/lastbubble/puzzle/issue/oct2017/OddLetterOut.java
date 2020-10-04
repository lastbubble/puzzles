package com.lastbubble.puzzle.issue.oct2017;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.common.Cell;
import com.lastbubble.puzzle.common.CharRaster;
import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.GridPrinter;
import com.lastbubble.puzzle.common.Pos;
import com.lastbubble.puzzle.solver.Solver;

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
    return grid.positions().filter(p -> grid.valueAt(p).orElse(' ') == c).findFirst();
  }

  protected Cell<Character> cell(Pos pos, char c) { return Cell.at(pos).withValue(c); }

  protected Cell<Character> cell(int x, int y, char c) { return Cell.at(x, y).withValue(c); }

  protected static final Grid<Character> blankGrid = Grid.builder(Character.class).add(Cell.at(4, 4)).build();

  protected static Stream<Character> allValues() {
    return IntStream.range(0, 26).mapToObj(n -> (char) ('A' + n));
  }

  protected void print(Grid<Character> grid) {
    CharRaster raster = CharRaster.builder().ofWidth(2 * grid.width() + 1).ofHeight(2 * grid.height() + 1).build();
    GridPrinter<Character> printer = new GridPrinter<Character>(raster::set, c -> c);
    printer.print(grid);
    raster.lines().forEach(System.out::println);
  }
}
