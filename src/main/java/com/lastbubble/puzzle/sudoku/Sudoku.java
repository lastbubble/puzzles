package com.lastbubble.puzzle.sudoku;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.GridPrinter;
import com.lastbubble.puzzle.Pos;
import com.lastbubble.puzzle.solver.Solver;

import java.io.PrintWriter;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Sudoku implements Runnable {

  private static final Grid<Character> blankGrid = Grid.builder(Character.class).add(Cell.at(8, 8)).build();

  protected Solver<Cell<Character>> solver = new Solver<>();

  @Override public void run() {
    addConstraints();
    solvePuzzle();
  }

  protected void addConstraints() {

    blankGrid.positions().forEach(p ->
      solver.addExactly(1, allValues().map(c -> cell(p, c)).map(solver::varFor))
    );

    IntStream.range(0, 9).forEach(x ->
      allValues().forEach(c ->
        solver.addExactly(1, IntStream.range(0, 9).mapToObj(y -> cell(x, y, c)).map(solver::varFor))
      )
    );

    IntStream.range(0, 9).forEach(y ->
      allValues().forEach(c ->
        solver.addExactly(1, IntStream.range(0, 9).mapToObj(x -> cell(x, y, c)).map(solver::varFor))
      )
    );

    allValues().forEach(c -> {
      solver.addExactly(1, regionFor(0, 0).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(3, 0).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(6, 0).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(0, 3).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(3, 3).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(6, 3).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(0, 6).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(3, 6).map(p -> cell(p, c)).map(solver::varFor));
      solver.addExactly(1, regionFor(6, 6).map(p -> cell(p, c)).map(solver::varFor));
    });

    Stream.of(
      cell(0, 0, '5'),
      cell(1, 0, '3'),
      cell(4, 0, '7'),
      cell(0, 1, '6'),
      cell(3, 1, '1'),
      cell(4, 1, '9'),
      cell(5, 1, '5'),
      cell(1, 2, '9'),
      cell(2, 2, '8'),
      cell(7, 2, '6'),
      cell(0, 3, '8'),
      cell(4, 3, '6'),
      cell(8, 3, '3'),
      cell(0, 4, '4'),
      cell(3, 4, '8'),
      cell(5, 4, '3'),
      cell(8, 4, '1'),
      cell(0, 5, '7'),
      cell(4, 5, '2'),
      cell(8, 5, '6'),
      cell(1, 6, '6'),
      cell(6, 6, '2'),
      cell(7, 6, '8'),
      cell(3, 7, '4'),
      cell(4, 7, '1'),
      cell(5, 7, '9'),
      cell(8, 7, '5'),
      cell(4, 8, '8'),
      cell(7, 8, '7'),
      cell(8, 8, '9')
    ).map(solver::varFor).forEach(solver::add);
  }

  protected static Cell<Character> cell(int x, int y, char c) { return cell(Pos.at(x, y), c); }

  protected static Cell<Character> cell(Pos p, char c) { return Cell.at(p).withValue(c); }

  protected Stream<Pos> regionFor(int x, int y) {
    return Stream.concat(
      Stream.concat(
        IntStream.range(0, 3).mapToObj(i -> Pos.at(x + i, y)),
        IntStream.range(0, 3).mapToObj(i -> Pos.at(x + i, y + 1))
      ),
      IntStream.range(0, 3).mapToObj(i -> Pos.at(x + i, y + 2))
    );
  }

  protected static Stream<Character> allValues() {
    return IntStream.rangeClosed(1, 9).mapToObj(n -> Character.forDigit(n, 10));
  }

  protected void solvePuzzle() {

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

  protected boolean isValid(Grid<Character> grid) { return true; }

  private final GridPrinter<Character> gridPrinter = new GridPrinter<>(c -> c);

  protected void print(Grid<Character> grid) {
    gridPrinter.printTo( new PrintWriter(System.out, true), grid);
  }
}
