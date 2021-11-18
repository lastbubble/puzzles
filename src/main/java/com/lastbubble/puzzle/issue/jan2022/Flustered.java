package com.lastbubble.puzzle.issue.jan2022;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.common.Cell;
import com.lastbubble.puzzle.common.CharRaster;
import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.GridPrinter;
import com.lastbubble.puzzle.common.Mover;
import com.lastbubble.puzzle.common.Pos;
import com.lastbubble.puzzle.solver.Solver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

abstract class Flustered implements Runnable {

  private final Map<Character, Set<Character>> adjacentLetters = new HashMap<>();

  protected Solver<Cell<Character>> solver = new Solver<>();

  protected abstract Stream<String> words();

  protected abstract Stream<Cell<Character>> knownCells();

  @Override public void run() {
    addConstraints();
    solvePuzzle();
  }

  private void addConstraints() {

    words().forEach(word -> {
      for (int i = 0; i < word.length() - 1; i++) {
        lettersAreAdjacent(word.charAt(i), word.charAt(i + 1));
      }
    });

    Mover move = blankGrid.mover();

    adjacentLetters.keySet().stream().forEach(c -> {
      // each letter appears exactly once in the grid
      solver.addExactly(1, blankGrid.positions().map(p -> cell(p, c)).map(solver::varFor));

      // each position in the grid contains only one letter
      blankGrid.positions().forEach(p -> {
        solver.add(
          implies(
            solver.varFor(cell(p, c)),
            allOf(
              adjacentLetters.keySet().stream().filter(x -> c != x).map(
                x -> not(solver.varFor(cell(p, x)))
              )
            )
          )
        );
      });

      // each letter can reach all of its adjacent letters
      adjacentLetters.get(c).stream().forEach(neighbor -> {
        blankGrid.positions().forEach(p ->
          solver.add(
            implies(
              solver.varFor(cell(p, c)),
              anyOf(move.neighborsOf(p).map(n -> cell(n, neighbor)).map(solver::varFor))
            )
          )
        );
      });
    });

    knownCells().map(solver::varFor).forEach(solver::add);
  }

  private void lettersAreAdjacent(char c1, char c2) {
    adjacentLetters.computeIfAbsent(c1, k -> new HashSet<Character>()).add(c2);
    adjacentLetters.computeIfAbsent(c2, k -> new HashSet<Character>()).add(c1);
  }

  private void solvePuzzle() {

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

      if(grid.height() == 4 && grid.width() == 4) {
        print(grid);
      }
    }
  }

  protected Cell<Character> cell(Pos pos, char c) { return Cell.at(pos).withValue(c); }

  protected static final Grid<Character> blankGrid = Grid.builder(Character.class).add(Cell.at(4, 4)).build();

  protected void print(Grid<Character> grid) {
    CharRaster raster = CharRaster.builder().ofWidth(2 * grid.width() + 1).ofHeight(2 * grid.height() + 1).build();
    GridPrinter<Character> printer = new GridPrinter<Character>(raster::set, c -> c);
    printer.print(grid);
    raster.lines().forEach(System.out::println);
  }
}
