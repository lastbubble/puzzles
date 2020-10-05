package com.lastbubble.puzzle.issue.mar2014;

import static java.util.stream.Collectors.toList;

import com.lastbubble.puzzle.common.Cell;
import com.lastbubble.puzzle.common.CharRaster;
import com.lastbubble.puzzle.common.Direction;
import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.GridPrinter;
import com.lastbubble.puzzle.common.Mover;
import com.lastbubble.puzzle.common.Pos;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabyrinthSudoku implements Runnable {

  private static final Pattern SIZE_PTN = Pattern.compile("size=(\\d)");
  private static final Pattern FILLED_PTN = Pattern.compile("filled=(.*)");
  private static final Pattern CELL_PTN = Pattern.compile("\\((\\d),(\\d)\\)=(\\d)");
  private static final Pattern PATH_PTN = Pattern.compile("path=\\((\\d),(\\d)\\)([UDLR]+)");

  public static LabyrinthSudoku load(Iterable<String> lines) {
    Grid.Builder<Integer> gridBuilder = Grid.builder(Integer.class);
    Grid<Integer> grid = null;
    List<Pos> path = new ArrayList<>();

    Matcher m;
    for (String line : lines) {
      if ((m = SIZE_PTN.matcher(line)).matches()) {
        gridBuilder.squareWithSize(Integer.valueOf(m.group(1)));

      } else if ((m = FILLED_PTN.matcher(line)).matches()) {
        m = CELL_PTN.matcher(m.group(1));
        while (m.find()) {
          int x = Integer.valueOf(m.group(1));
          int y = Integer.valueOf(m.group(2));
          int value = Integer.valueOf(m.group(3));
          gridBuilder.add(Cell.at(x, y).withValue(value));
        }

      } else if ((m = PATH_PTN.matcher(line)).matches()) {
        int x = Integer.valueOf(m.group(1));
        int y = Integer.valueOf(m.group(2));
        String directions = m.group(3);
        grid = gridBuilder.build();
        Mover move = grid.mover();
        Pos pos = Pos.at(x, y);
        path.add(pos);
        for (int i = 0; i < directions.length(); i++) {
          Pos nextPos = move.move(directionFor(directions.charAt(i)), pos, 1).get();
          path.add(nextPos);
          pos = nextPos;
        }
      }
    }
    return new LabyrinthSudoku(grid, path);
  }

  private static Direction directionFor(char c) {
    for (Direction d : Direction.values()) {
      if (d.name().charAt(0) == c) { return d; }
    }
    return null;
  }

  private final Grid<Integer> grid;
  private final List<Pos> path;

  private LabyrinthSudoku(Grid<Integer> grid, List<Pos> path) {
    this.grid = grid;
    this.path = path;
  }

  public void run() {
    Solution solution = initialSolution();

    System.out.println("Puzzle:");
    display(solution);

    Solution forcedSolution = solution.force();

    System.out.println("Forced solution:");
    display(forcedSolution);

    Solution completeSolution = reduce(forcedSolution);

    System.out.println("Complete solution:");
    display(completeSolution);
  }

  protected Solution initialSolution() {
    return new Solution(grid, path);
  }

  protected Solution reduce(Solution solution) {
    if (solution.isComplete()) { return solution; }

    for (Solution s : solution.reductions()) {

      Solution reduction = reduce(s);
      if (reduction != null) { return reduction; }
    }

    return null;
  }

  protected void display(Solution solution) {
    solution.toRaster().lines().forEach(System.out::println);
  }

  private static class Solution {
    private final Grid<Integer> grid;
    private final List<Pos> path;

    private Solution(Grid<Integer> grid, List<Pos> path) {
      this.grid = grid;
      this.path = path;
    }

    private boolean isComplete() {
      if (grid.cellsMatching(c -> c.value().isEmpty()).count() != 0) { return false; }

      int size = grid.width();

      return (
        (grid.cellsMatching(c -> c.value().orElse(0) == 1).count() == size) &&
        (grid.cellsMatching(c -> c.value().orElse(0) == 2).count() == size) &&
        (grid.cellsMatching(c -> c.value().orElse(0) == 3).count() == size)
      );
    }

    private Solution force() { return this; }

    private Iterable<Solution> reductions() {

      Optional<Cell<Integer>> ifNextCell = grid.cellsMatching(c -> c.value().isEmpty()).findFirst();

      if (ifNextCell.isEmpty()) { return List.of(); }

      Pos nextPos = ifNextCell.get().pos();

      List<Integer> row = rowContaining(nextPos);
      List<Integer> column = columnContaining(nextPos);

      List<Solution> reductions = new ArrayList<>();

      for (int i = 3; i >= 0; i--) {

        if (i > 0 && (row.contains(i) || column.contains(i))) { continue; }

        // mutating row and column here, but it's okay
        row.set(nextPos.x(), i);
        column.set(nextPos.y(), i);

        Grid<Integer> nextGrid = grid.copy().add(Cell.at(nextPos).withValue(i)).build();

        if (pathSatisfied(nextGrid)) {
          reductions.add( new Solution(nextGrid, path));
        }
      }

      return reductions;
    }

    private List<Integer> rowContaining(Pos pos) {
      return grid.cellsMatching(c -> c.pos().y() == pos.y()).map(c -> c.value().orElse(0)).collect(toList());
    }

    private List<Integer> columnContaining(Pos pos) {
      return grid.cellsMatching(c -> c.pos().x() == pos.x()).map(c -> c.value().orElse(0)).collect(toList());
    }

    private boolean pathSatisfied(Grid<Integer> grid) {
      int expected = 1;

      for (Pos pos : path) {
        Optional<Integer> ifValue = grid.valueAt(pos);
        if (ifValue.isEmpty()) { break; }
        int value = ifValue.get();
        if (value > 0) {
          if (value == expected) {
            expected++; if (expected > 3) { expected = 1; }
          } else {
            return false;
          }
        }
      }
      return true;
    }

    private CharRaster toRaster() {
      int size = 2 * grid.width() + 1;

      CharRaster raster = CharRaster.builder().ofWidth(size).ofHeight(size).build();

      GridPrinter<Integer> gridPrinter = new GridPrinter<Integer>(raster::set, n -> (n > 0) ? (char) (n + '0') : '-');

      BiPredicate<Pos, Pos> connected = (a, b) -> Math.abs(path.indexOf(a) - path.indexOf(b)) == 1;

      gridPrinter.print(grid, connected);

      return raster;
    }
  }
}
