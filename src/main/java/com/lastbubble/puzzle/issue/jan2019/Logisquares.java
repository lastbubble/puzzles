package com.lastbubble.puzzle.issue.jan2019;

import static com.lastbubble.puzzle.logic.Formula.*;

import com.lastbubble.puzzle.common.Cell;
import com.lastbubble.puzzle.common.CharRaster;
import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.GridPrinter;
import com.lastbubble.puzzle.common.Mover;
import com.lastbubble.puzzle.common.Pos;
import com.lastbubble.puzzle.solver.Solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Logisquares implements Runnable {

  public static Logisquares load(Iterable<String> lines) {
    Iterator<String> lineIter = lines.iterator();

    Grid.Builder<Character> gridBuilder = Grid.builder(Character.class);
    Map<Integer, Integer> minesInRow = new HashMap<>();

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
        if ((pipe + 1) < line.length()) {
          minesInRow.put(y, Integer.valueOf(line.substring(pipe + 1)));
        }
        y++;
      }
    }
    Grid<Character> loadedGrid = gridBuilder.build();

    Map<Integer, Integer> minesInColumn = new HashMap<>();
    while (lineIter.hasNext()) {
      String line = lineIter.next();
      if (line.startsWith("---")) { break; }
      for (int x = 0; x <= line.length() / 2; x++) {
        char c = line.charAt(x * 2);
        if (c != ' ') { minesInColumn.put(x, c - '0'); }
      }
    }

    Pattern posPtn = Pattern.compile("(\\d+),(\\d+)");
    List<Set<Pos>> regions = new ArrayList<>();
    while (lineIter.hasNext()) {
      Set<Pos> region = new HashSet<>();
      Matcher m = posPtn.matcher(lineIter.next());
      while (m.find()) {
        region.add(Pos.at(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2))));
      }
      regions.add(region);
    }

    return new Logisquares(loadedGrid, regions, minesInRow, minesInColumn);
  }

  private final Grid<Character> grid;
  private final Mover move;
  private final BiPredicate<Pos, Pos> sameRegion;

  private final Solver<Pos> solver = new Solver<>();

  private Logisquares(
    Grid<Character> grid,
    Iterable<Set<Pos>> regions,
    Map<Integer, Integer> minesInRow,
    Map<Integer, Integer> minesInColumn
  ) {
    this.grid = grid;
    move = grid.mover();
    grid.cellsMatching(c -> c.value().isPresent()).forEach(c -> addConstraintsFor(c));

    Map<Pos, Set<Pos>> regionForPos = new HashMap<>();
    for (Set<Pos> region : regions) {
      solver.addExactly(1, region.stream().map(solver::varFor));
      region.forEach(pos -> regionForPos.put(pos, region));
    }
    sameRegion = (a, b) -> regionForPos.get(a) == regionForPos.get(b);

    minesInRow.forEach((row, count) -> minesInRow(count, row));
    minesInColumn.forEach((column, count) -> minesInColumn(count, column));
  }

  @Override public void run() {
    print(grid);
    solvePuzzle();
  }

  protected void addConstraintsFor(Cell<Character> c) {
    char value = c.value().orElse(' ');

    if (Character.isDigit(value)) {
      solver.addExactly(value - '0',
        move.neighborsOf(c.pos()).filter(p -> grid.valueAt(p).isEmpty()).map(solver::varFor));

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

      Grid.Builder<Character> gridBuilder = grid.copy();

      try {
        Set<Pos> solution = solver.solve();

        if (solution.isEmpty()) { break; }

        solution.stream().forEach(p -> gridBuilder.add(Cell.at(p).withValue('\u25CF')));

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

  protected void print(Grid<Character> grid) {
    CharRaster raster = CharRaster.builder().ofWidth(2 * grid.width() + 1).ofHeight(2 * grid.height() + 1).build();
    GridPrinter<Character> printer = new GridPrinter<Character>(raster::set, c -> c);
    printer.print(grid, sameRegion);
    raster.lines().forEach(System.out::println);
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
