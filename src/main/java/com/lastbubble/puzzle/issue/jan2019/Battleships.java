package com.lastbubble.puzzle.issue.jan2019;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.toSet;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.GridPrinter;
import com.lastbubble.puzzle.Pos;
import com.lastbubble.puzzle.solver.Solver;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public abstract class Battleships implements Runnable {

  protected Solver<Cell<Value>> solver = new Solver<>();

  protected Grid<Value> grid;

  @Override public void run() {
    grid = definePuzzle().build();
    print(grid);
    addConstraints();
    addValues();
    solvePuzzle();
  }

  protected abstract Grid.Builder<Value> definePuzzle();

  protected abstract void addRowAndColumnCounts();

  protected abstract void addValues();

  protected void addConstraints() {
    grid.positions().forEach(p -> {
      Set<Value> values = Stream.of(Value.values()).collect(toSet());
      if (p.x() > 6 && p.y() > 6) { values.remove(Value.BATTLESHIP_START); }
      if (p.x() > 7 && p.y() > 7) { values.remove(Value.CRUISER_START); }
      if (p.x() > 8 && p.y() > 8) { values.remove(Value.DESTROYER_START); }
      solver.addExactly(1, values.stream().map(v -> cell(p, v)).map(solver::varFor));
    });

    solver.addExactly(1, grid.positions().map(p -> cell(p, Value.BATTLESHIP_START)).map(solver::varFor));
    solver.addExactly(2, grid.positions().map(p -> cell(p, Value.CRUISER_START)).map(solver::varFor));
    solver.addExactly(3, grid.positions().map(p -> cell(p, Value.DESTROYER_START)).map(solver::varFor));
    solver.addExactly(4, grid.positions().map(p -> cell(p, Value.SUBMARINE)).map(solver::varFor));
    solver.addExactly(10, grid.positions().map(p -> cell(p, Value.SHIP_SECTION)).map(solver::varFor));

    grid.positions().forEach(p -> {
      Pos toRight = Pos.at(p.x() + 1, p.y());
      Pos below = Pos.at(p.x(), p.y() + 1);
      if (p.x() <= 6 && p.y() <= 6) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.BATTLESHIP_START)),
            or(
              solver.varFor(cell(toRight, Value.SHIP_SECTION)),
              solver.varFor(cell(below, Value.SHIP_SECTION))
            )
          )
        );
        solver.add(
          implies(
            and(
              solver.varFor(cell(p, Value.BATTLESHIP_START)),
              solver.varFor(cell(toRight, Value.SHIP_SECTION))
            ),
            allOf(shipFor(p, toRight, Pos.at(toRight.x() + 1, toRight.y()), Pos.at(toRight.x() + 2, toRight.y())).map(solver::varFor))
          )
        );
        solver.add(
          implies(
            and(
              solver.varFor(cell(p, Value.BATTLESHIP_START)),
              solver.varFor(cell(below, Value.SHIP_SECTION))
            ),
            allOf(shipFor(p, below, Pos.at(below.x(), below.y() + 1), Pos.at(below.x(), below.y() + 2)).map(solver::varFor))
          )
        );
      } else if (p.x() <= 6) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.BATTLESHIP_START)),
            allOf(shipFor(p, toRight, Pos.at(toRight.x() + 1, toRight.y()), Pos.at(toRight.x() + 2, toRight.y())).map(solver::varFor))
          )
        );
      } else if (p.y() <= 6) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.BATTLESHIP_START)),
            allOf(shipFor(p, below, Pos.at(below.x(), below.y() + 1), Pos.at(below.x(), below.y() + 2)).map(solver::varFor))
          )
        );
      }
    });

    grid.positions().forEach(p -> {
      Pos toRight = Pos.at(p.x() + 1, p.y());
      Pos below = Pos.at(p.x(), p.y() + 1);
      if (p.x() <= 7 && p.y() <= 7) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.CRUISER_START)),
            or(
              solver.varFor(cell(toRight, Value.SHIP_SECTION)),
              solver.varFor(cell(below, Value.SHIP_SECTION))
            )
          )
        );
        solver.add(
          implies(
            and(
              solver.varFor(cell(p, Value.CRUISER_START)),
              solver.varFor(cell(toRight, Value.SHIP_SECTION))
            ),
            allOf(shipFor(p, toRight, Pos.at(toRight.x() + 1, toRight.y())).map(solver::varFor))
          )
        );
        solver.add(
          implies(
            and(
              solver.varFor(cell(p, Value.CRUISER_START)),
              solver.varFor(cell(below, Value.SHIP_SECTION))
            ),
            allOf(shipFor(p, below, Pos.at(below.x(), below.y() + 1)).map(solver::varFor))
          )
        );
      } else if (p.x() <= 7) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.CRUISER_START)),
            allOf(shipFor(p, toRight, Pos.at(toRight.x() + 1, toRight.y())).map(solver::varFor))
          )
        );
      } else if (p.y() <= 7) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.CRUISER_START)),
            allOf(shipFor(p, below, Pos.at(below.x(), below.y() + 1)).map(solver::varFor))
          )
        );
      }
    });

    grid.positions().forEach(p -> {
      Pos toRight = Pos.at(p.x() + 1, p.y());
      Pos below = Pos.at(p.x(), p.y() + 1);
      if (p.x() <= 8 && p.y() <= 8) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.DESTROYER_START)),
            or(
              solver.varFor(cell(toRight, Value.SHIP_SECTION)),
              solver.varFor(cell(below, Value.SHIP_SECTION))
            )
          )
        );
        solver.add(
          implies(
            and(
              solver.varFor(cell(p, Value.DESTROYER_START)),
              solver.varFor(cell(toRight, Value.SHIP_SECTION))
            ),
            allOf(shipFor(p, toRight).map(solver::varFor))
          )
        );
        solver.add(
          implies(
            and(
              solver.varFor(cell(p, Value.DESTROYER_START)),
              solver.varFor(cell(below, Value.SHIP_SECTION))
            ),
            allOf(shipFor(p, below).map(solver::varFor))
          )
        );
      } else if (p.x() <= 8) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.DESTROYER_START)),
            allOf(shipFor(p, toRight).map(solver::varFor))
          )
        );
      } else if (p.y() <= 8) {
        solver.add(
          implies(
            solver.varFor(cell(p, Value.DESTROYER_START)),
            allOf(shipFor(p, below).map(solver::varFor))
          )
        );
      }
    });

    grid.positions().forEach(p ->
      solver.add(
        implies(
          solver.varFor(cell(p, Value.SUBMARINE)),
          allOf(shipFor(p).map(solver::varFor))
        )
      )
    );

    addRowAndColumnCounts();
  }

  protected Stream<Cell<Value>> shipFor(Pos... ship) {
    Set<Pos> region = new HashSet<>();
    Stream.of(ship).forEach(p -> grid.neighborsOf(p).forEach(n -> region.add(n)));
    Stream.of(ship).forEach(p -> region.remove(p));
    Set<Cell<Value>> cells = region.stream().map(p -> cell(p, Value.WATER)).collect(toSet());
    for (int i = 1; i < ship.length; i++) {
      cells.add(cell(ship[i], Value.SHIP_SECTION));
    }
    return cells.stream();
  }

  protected void shipSectionsInColumn(int count, int column) {
    solver.addExactly(10 - count,
      grid.positions()
        .filter(p -> p.x() == column)
        .map(p -> cell(p, Value.WATER))
        .map(solver::varFor)
    );
  }

  protected void shipSectionsInRow(int count, int row) {
    solver.addExactly(10 - count,
      grid.positions()
        .filter(p -> p.y() == row)
        .map(p -> cell(p, Value.WATER))
        .map(solver::varFor)
    );
  }

  protected void solvePuzzle() {

    while (true) {

      Grid.Builder<Value> gridBuilder = definePuzzle();

      try {
        Set<Cell<Value>> solution = solver.solve();

        if (solution.isEmpty()) { break; }

        solution.stream().forEach(c -> gridBuilder.add(c));

        solver.add(not(allOf(solution.stream().map(solver::varFor))));

      } catch (Exception e) {
        System.out.println("Caught " + e);
        e.printStackTrace(System.out);
        break;
      }

      Grid<Value> grid = gridBuilder.build();

      if (isValid(grid)) { print(grid); }
    }
  }

  protected static Cell<Value> cell(int x, int y) { return Cell.<Value>at(x, y); }

  protected static Cell<Value> cell(Pos p, Value v) { return cell(p.x(), p.y()).withValue(v); }

  protected boolean isValid(Grid<Value> grid) { return true; }

  private final GridPrinter<Value> gridPrinter = new GridPrinter<>(v -> v.toChar());

  protected void print(Grid<Value> grid) {
    gridPrinter.printTo( new PrintWriter(System.out, true), grid);
  }

  public enum Value {

    WATER(' '),
    SHIP_SECTION('\u25a0'),
    SUBMARINE('\u25cf'),
    DESTROYER_START('D'/*'\u25a0'*/),
    CRUISER_START('C'/*'\u25a0'*/),
    BATTLESHIP_START('B'/*'\u25a0'*/);

    private final char c;

    private Value(char c) { this.c = c; }

    public char toChar() { return c; }
  }
}
