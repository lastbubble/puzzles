package com.lastbubble.puzzle.issue.jan2019;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.GridPrinter;
import com.lastbubble.puzzle.Pos;
import com.lastbubble.puzzle.solver.Solver;

import java.io.PrintWriter;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class Logisquares implements Runnable {

  protected Solver<Pos> solver = new Solver<>();

  protected Grid<Character> grid;

  @Override public void run() {
    grid = definePuzzle().build();
    print(grid);
    addConstraints();
    solvePuzzle();
  }

  protected abstract Grid.Builder<Character> definePuzzle();

  protected abstract void addRowAndColumnCounts();

  protected abstract Stream<Stream<Pos>> regions();

  protected void addConstraints() {

    grid.filledCells().forEach(c -> addConstraintsFor(c));

    addRowAndColumnCounts();

    regions().forEach(r -> solver.addExactly(1, r.map(solver::varFor)));
  }

  protected void addConstraintsFor(Cell<Character> c) {
    char value = c.value().orElse(' ');

    if (Character.isDigit(value)) {
      solver.addExactly(value - '0',
        grid.neighborsOf(c.pos()).filter(p -> !grid.valueAt(p).isPresent()).map(solver::varFor)
      );

    } else {

      Stream.of(Arrow.values()).filter(a -> a.symbol() == value).forEach(a ->
        solver.add(anyOf(grid.positions().filter(a.pointsAt(c.pos())).filter(p -> !grid.valueAt(p).isPresent()).map(solver::varFor)))
      );
    }
  }

  protected void minesInColumn(int count, int column) {
    solver.addExactly(count,
      grid.positions()
        .filter(p -> p.x() == column)
        .filter(p -> !grid.valueAt(p).isPresent())
        .map(solver::varFor)
    );
  }

  protected void minesInRow(int count, int row) {
    solver.addExactly(count,
      grid.positions()
        .filter(p -> p.y() == row)
        .filter(p -> !grid.valueAt(p).isPresent())
        .map(solver::varFor)
    );
  }

  protected void solvePuzzle() {

    while (true) {

      Grid.Builder<Character> gridBuilder = definePuzzle();

      try {
        Set<Pos> solution = solver.solve();

        if (solution.isEmpty()) { break; }

        solution.stream().forEach(p -> gridBuilder.add(Cell.at(p).withValue('O')));

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

  protected static Cell<Character> cell(int x, int y) { return Cell.<Character>at(x, y); }

  protected boolean isValid(Grid<Character> grid) { return true; }

  private final GridPrinter<Character> gridPrinter = new GridPrinter<>(c -> c);

  protected void print(Grid<Character> grid) {
    gridPrinter.printTo( new PrintWriter(System.out, true), grid);
  }

  public enum Arrow {

    WEST('\u2190') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> x.x() < start.x() && x.y() == start.y();
      }
    },
    NORTH('\u2191') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> x.x() == start.x() && x.y() < start.y();
      }
    },
    EAST('\u2192') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> x.x() > start.x() && x.y() == start.y();
      }
    },
    SOUTH('\u2193') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> x.x() == start.x() && x.y() > start.y();
      }
    },
    NORTHWEST('\u2196') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> Math.abs(x.x() - start.x()) == Math.abs(x.y() - start.y()) && x.x() < start.x() && x.y() < start.y();
      }
    },
    NORTHEAST('\u2197') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> Math.abs(x.x() - start.x()) == Math.abs(x.y() - start.y()) && x.x() > start.x() && x.y() < start.y();
      }
    },
    SOUTHEAST('\u2198') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> Math.abs(x.x() - start.x()) == Math.abs(x.y() - start.y()) && x.x() > start.x() && x.y() > start.y();
      }
    },
    SOUTHWEST('\u2199') {
      @Override public Predicate<Pos> pointsAt(Pos start) {
        return x -> Math.abs(x.x() - start.x()) == Math.abs(x.y() - start.y()) && x.x() < start.x() && x.y() > start.y();
      }
    };

    private final char symbol;

    private Arrow(char symbol) { this.symbol = symbol; }

    public char symbol() { return symbol; }

    abstract public Predicate<Pos> pointsAt(Pos start);
  }
}
