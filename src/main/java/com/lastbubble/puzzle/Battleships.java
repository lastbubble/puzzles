package com.lastbubble.puzzle;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import com.lastbubble.puzzle.solver.Solver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Battleships implements Runnable {

  public static Battleships load(Iterable<String> lines) {
    Iterator<String> lineIter = lines.iterator();

    List<Integer> rowCounts = new ArrayList<>();
    Grid.Builder<Character> gridBuilder = Grid.builder(Character.class);

    int y = 0;
    while (lineIter.hasNext()) {
      String line = lineIter.next();
      if (line.startsWith("---")) { break; }
      int pipe = line.indexOf('|');
      if (pipe > -1) {
        String[] cells = line.substring(0, pipe).split(",");
        gridBuilder.add(Cell.at(cells.length - 1, y));
        for (int x = 0; x < cells.length; x++) {
          if (!cells[x].equals(" ")) { gridBuilder.add(Cell.at(x, y).withValue(cells[x].charAt(0))); }
        }
        rowCounts.add(line.charAt(pipe + 1) - '0');
        y++;
      }
    }
    Grid<Character> loadedGrid = gridBuilder.build();

    List<Integer> columnCounts = new ArrayList<>();
    while (lineIter.hasNext()) {
      String line = lineIter.next();
      if (line.startsWith("---")) { break; }
      for (int x = 0; x <= line.length() / 2; x++) {
        columnCounts.add(line.charAt(x * 2) - '0');
      }
    }

    return new Battleships(loadedGrid, rowCounts, columnCounts);
  }

  private Solver<Cell<Value>> solver = new Solver<>();

  private final Grid<Value> grid;
  private final List<Integer> rowCounts;
  private final List<Integer> columnCounts;

  private final Set<Pos> mustBeFilled = new HashSet<>();

  private Battleships(Grid<Character> loadedGrid, List<Integer> rowCounts, List<Integer> columnCounts) {
    grid = Grid.builder(Value.class)
      .add(Cell.at(loadedGrid.width() - 1, loadedGrid.height() - 1))
      .build();
    this.rowCounts = rowCounts;
    this.columnCounts = columnCounts;
    loadedGrid.filledCells().forEach(cell -> {
      switch (cell.value().get()) {
        case '\u25B2':
          addShipStartAt(cell.pos());
          mustBeFilled.add(Pos.at(cell.pos().x(), cell.pos().y() + 1));
          break;
        case '\u25B6':
          addShipStartAt(cell.pos());
          mustBeFilled.add(Pos.at(cell.pos().x() - 1, cell.pos().y()));
          break;
        case '\u25BC':
          addShipStartAt(cell.pos());
          mustBeFilled.add(Pos.at(cell.pos().x(), cell.pos().y() - 1));
          break;
        case '\u25C0':
          addShipStartAt(cell.pos());
          mustBeFilled.add(Pos.at(cell.pos().x() + 1, cell.pos().y()));
          break;
        case '\u25CF':
          addSubmarineAt(cell.pos());
          break;
        case '\u25FB':
          addWaterAt(cell.pos());
          break;
        case '\u25FC':
          addShipSectionAt(cell.pos());
          break;
      }
    });
  }

  @Override public void run() {
    addConstraints();
    solvePuzzle();
  }

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

    for (int col = 0; col < columnCounts.size(); col++) { shipSectionsInColumn(columnCounts.get(col), col); }

    for (int row = 0; row < rowCounts.size(); row++) { shipSectionsInRow(rowCounts.get(row), row); }
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

  protected void addShipStartAt(Pos pos) { addShipStartAt(pos.x(), pos.y()); }

  protected void addShipStartAt(int x, int y) {
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(x, y).withValue(v))
        .map(solver::varFor)
    );
  }

  protected void addShipSectionAt(Pos pos) { addShipSectionAt(pos.x(), pos.y()); }

  protected void addShipSectionAt(int x, int y) {
    addValueAt(x, y, Value.SHIP_SECTION);
    if (y == 0 || y == (grid.height() - 1)) {
      solver.addExactly(1,
        Stream.of(Value.CRUISER_START, Value.BATTLESHIP_START, Value.SHIP_SECTION)
          .map(v -> cell(x - 1, y).withValue(v))
          .map(solver::varFor)
      );
      solver.addExactly(1,
        Stream.of(Value.CRUISER_START, Value.BATTLESHIP_START, Value.SHIP_SECTION)
          .map(v -> cell(x + 1, y).withValue(v))
          .map(solver::varFor)
      );
    }
    if (x == 0 || x == (grid.width() - 1)) {
      solver.addExactly(1,
        Stream.of(Value.CRUISER_START, Value.BATTLESHIP_START, Value.SHIP_SECTION)
          .map(v -> cell(x, y - 1).withValue(v))
          .map(solver::varFor)
      );
      solver.addExactly(1,
        Stream.of(Value.CRUISER_START, Value.BATTLESHIP_START, Value.SHIP_SECTION)
          .map(v -> cell(x, y + 1).withValue(v))
          .map(solver::varFor)
      );
    }
  }

  protected void addSubmarineAt(Pos pos) { addSubmarineAt(pos.x(), pos.y()); }

  protected void addSubmarineAt(int x, int y) { addValueAt(x, y, Value.SUBMARINE); }

  protected void addWaterAt(Pos pos) { addWaterAt(pos.x(), pos.y()); }

  protected void addWaterAt(int x, int y) { addValueAt(x, y, Value.WATER); }

  private void addValueAt(int x, int y, Value value) { solver.add(solver.varFor(cell(x, y).withValue(value))); }

  protected void solvePuzzle() {

    while (true) {

      Grid.Builder<Value> gridBuilder = Grid.builder(Value.class).copyOf(grid);

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

  protected boolean isValid(Grid<Value> grid) {
    for (Pos pos : mustBeFilled) {
      if (!isFilled(grid, pos.x(), pos.y())) { return false; }
    }
    return true;
  }

  protected final boolean isFilled(Grid<Value> grid, int x, int y) {
    return grid.valueAt(x, y).orElse(null) == Value.SHIP_SECTION;
  }

  private final GridPrinter<Value> gridPrinter = new GridPrinter<>(v -> v.toChar());

  protected void print(Grid<Value> grid) {
    StringWriter out = new StringWriter();

    gridPrinter.printTo( new PrintWriter(out), grid);

    String[] lines = out.toString().split("\\n");

    for (int row = 0; row < rowCounts.size(); row++) {
      lines[2 * row + 1] += " " + rowCounts.get(row);
    }

    for(String line : lines) { System.out.println(line); }

    System.out.println(columnCounts.stream().map(x -> String.valueOf(x)).collect(joining(" ", " ", "")));
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
