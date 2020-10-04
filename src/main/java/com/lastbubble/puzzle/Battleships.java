package com.lastbubble.puzzle;

import com.lastbubble.puzzle.common.CharRaster;
import com.lastbubble.puzzle.common.Cell;
import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.GridPrinter;
import com.lastbubble.puzzle.common.Mover;
import com.lastbubble.puzzle.common.Pos;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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

  private final Grid<Character> grid;
  private final List<Integer> rowCounts;
  private final List<Integer> columnCounts;

  private Battleships(Grid<Character> grid, List<Integer> rowCounts, List<Integer> columnCounts) {
    this.grid = grid;
    this.rowCounts = rowCounts;
    this.columnCounts = columnCounts;
  }

  @Override public void run() {
    Solution solution = solveRecursive(Solution.builder(forceFill(extendShipStarts(grid))).build(), 0);

    System.out.format("Solution is complete = %s%n", solution.isComplete());
    print(solution.grid());
  }

  private Solution solveRecursive(Solution solution, int depth) {
    if (depth == 10) {
      System.out.println("MAX DEPTH");
      return solution;
    }
    Solution nextSolution = solution;
    if (!solution.isComplete()) {
      nextSolution = Solution.builder(forceFill(solution.grid())).build();
      if (nextSolution.battleships.size() < 1) {
        for (Placement placement : nextSolution.placementsFor(Ship.BATTLESHIP, rowCounts, columnCounts)) {
          Solution battleshipSolution = Solution.builder(forceFill(nextSolution.placeShipAt(Ship.BATTLESHIP, placement).grid())).build();
          if (!battleshipSolution.isValid()) { continue; }
          battleshipSolution = solveRecursive(battleshipSolution, depth + 1);
          if (battleshipSolution.isComplete()) {
            if (!battleshipSolution.countsMatch(rowCounts, columnCounts)) {
              continue;
            }
            return battleshipSolution;
          }
        }
      }
      else if (nextSolution.cruisers.size() < 2) {
        for (Placement placement : nextSolution.placementsFor(Ship.CRUISER, rowCounts, columnCounts)) {
          Solution cruiserSolution = Solution.builder(forceFill(nextSolution.placeShipAt(Ship.CRUISER, placement).grid())).build();
          if (!cruiserSolution.isValid()) { continue; }
          cruiserSolution = solveRecursive(cruiserSolution, depth + 1);
          if (cruiserSolution.isComplete()) {
            if (!cruiserSolution.countsMatch(rowCounts, columnCounts)) {
              continue;
            }
            return cruiserSolution;
          }
        }
      }
      else if (nextSolution.destroyers.size() < 3) {
        for (Placement placement : nextSolution.placementsFor(Ship.DESTROYER, rowCounts, columnCounts)) {
          Solution destroyerSolution = Solution.builder(forceFill(nextSolution.placeShipAt(Ship.DESTROYER, placement).grid())).build();
          if (!destroyerSolution.isValid()) { continue; }
          destroyerSolution = solveRecursive(destroyerSolution, depth + 1);
          if (destroyerSolution.isComplete()) {
            if (!destroyerSolution.countsMatch(rowCounts, columnCounts)) { continue; }
            return destroyerSolution;
          }
        }
      }
      else if (nextSolution.submarines.size() < 4) {
        for (Placement placement : nextSolution.placementsFor(Ship.SUBMARINE, rowCounts, columnCounts)) {
          Solution submarineSolution = Solution.builder(forceFill(nextSolution.placeShipAt(Ship.SUBMARINE, placement).grid())).build();
          if (!submarineSolution.isValid()) { continue; }
          submarineSolution = solveRecursive(submarineSolution, depth + 1);
          if (submarineSolution.isComplete()) {
            if (!submarineSolution.countsMatch(rowCounts, columnCounts)) { continue; }
            return submarineSolution;
          }
        }
      }
    }
    return nextSolution;
  }

  private Grid<Character> forceFill(Grid<Character> grid) {
    Grid<Character> nextGrid = applySteps(grid);
    while (nextGrid != grid) {
      grid = nextGrid;
      nextGrid = applySteps(grid);
    }
    return nextGrid;
  }

  private Grid<Character> applySteps(Grid<Character> grid) {
    return surroundShipsWithWater(completeColumns(completeRows(fillCompletedRowsAndColumnsWithWater(grid))));
  }

  private Grid<Character> extendShipStarts(Grid<Character> grid) {
    Grid.Builder<Character> gridOverlay = grid.copy();
    grid.cellsMatching(c -> c.value().isPresent()).forEach(cell -> {
      switch (cell.value().get()) {
        case SHIP_START_NORTH:
          gridOverlay.add(Cell.at(cell.pos().x(), cell.pos().y() + 1).withValue(SOME_SHIP_PART));
          break;
        case SHIP_START_EAST:
          gridOverlay.add(Cell.at(cell.pos().x() - 1, cell.pos().y()).withValue(SOME_SHIP_PART));
          break;
        case SHIP_START_SOUTH:
          gridOverlay.add(Cell.at(cell.pos().x(), cell.pos().y() - 1).withValue(SOME_SHIP_PART));
          break;
        case SHIP_START_WEST:
          gridOverlay.add(Cell.at(cell.pos().x() + 1, cell.pos().y()).withValue(SOME_SHIP_PART));
          break;
        }
      }
    );
    return gridOverlay.addedCells() ? gridOverlay.build() : grid;
  }

  private Grid<Character> fillCompletedRowsAndColumnsWithWater(Grid<Character> grid) {
    Grid.Builder<Character> gridOverlay = grid.copy();
    IntStream.range(0, rowCounts.size()).forEach(col -> {
      int count = rowCounts.get(col);
      int filled = (int) IntStream.range(0, columnCounts.size()).mapToObj(row -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).orElse(WATER) != WATER).count();
      if (filled == count) {
        IntStream.range(0, columnCounts.size()).mapToObj(row -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).isEmpty()).forEach(pos -> gridOverlay.add(Cell.at(pos).withValue(WATER)));
      }
    });
    IntStream.range(0, columnCounts.size()).forEach(row -> {
      int count = columnCounts.get(row);
      int filled = (int) IntStream.range(0, rowCounts.size()).mapToObj(col -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).orElse(WATER) != WATER).count();
      if (filled == count) {
        IntStream.range(0, rowCounts.size()).mapToObj(col -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).isEmpty()).forEach(pos -> gridOverlay.add(Cell.at(pos).withValue(WATER)));
      }
    });
    return gridOverlay.addedCells() ? gridOverlay.build() : grid;
  }

  private Grid<Character> surroundShipsWithWater(Grid<Character> grid) {
    Grid.Builder<Character> gridOverlay = grid.copy();
    grid.cellsMatching(c -> c.value().isPresent())
      .filter(cell -> cell.value().orElse(WATER) != WATER)
      .forEach(cell -> {
        waterSurrounding(grid, cell.pos(), cell.value().get()).filter(pos -> grid.valueAt(pos).isEmpty()).forEach(pos -> gridOverlay.add(Cell.at(pos).withValue(WATER)));
      });
    return gridOverlay.addedCells() ? gridOverlay.build() : grid;
    }

  private Stream<Pos> waterSurrounding(Grid<Character> grid, Pos pos, char section) {
    Mover move = grid.mover();
    switch (section) {
      case SUBMARINE: return move.neighborsOf(pos);
      case SOME_SHIP_PART: {
        return Stream.of(
          move.up(pos).flatMap(move::left),
          move.up(pos).flatMap(move::right),
          move.down(pos).flatMap(move::right),
          move.down(pos).flatMap(move::left)
        ).flatMap(Optional::stream);
      }
      case SHIP_SECTION: {
        List<Optional<Pos>> ifNeighbors = Stream.of(
          move.up(pos).flatMap(move::left),
          move.up(pos).flatMap(move::right),
          move.down(pos).flatMap(move::right),
          move.down(pos).flatMap(move::left)
        ).collect(Collectors.toList());
        if (move.up(pos).filter(p -> grid.valueAt(p).orElse(' ') == WATER).isPresent()) {
          ifNeighbors.add(move.down(pos));
        }
        if (move.down(pos).filter(p -> grid.valueAt(p).orElse(' ') == WATER).isPresent()) {
          ifNeighbors.add(move.up(pos));
        }
        if (move.left(pos).filter(p -> grid.valueAt(p).orElse(' ') == WATER).isPresent()) {
          ifNeighbors.add(move.right(pos));
        }
        if (move.right(pos).filter(p -> grid.valueAt(p).orElse(' ') == WATER).isPresent()) {
          ifNeighbors.add(move.left(pos));
        }
        return ifNeighbors.stream().filter(Optional::isPresent).map(Optional::get);
      }
      case SHIP_START_NORTH: {
        return Stream.of(
          move.up(pos).flatMap(move::left),
          move.up(pos),
          move.up(pos).flatMap(move::right),
          move.right(pos),
          move.down(pos).flatMap(move::right),
          move.down(pos, 2).flatMap(move::right),
          move.down(pos, 2).flatMap(move::left),
          move.down(pos).flatMap(move::left),
          move.left(pos)
        ).flatMap(Optional::stream);
      }
      case SHIP_START_WEST: {
        return Stream.of(
          move.right(pos, 2).flatMap(move::up),
          move.right(pos).flatMap(move::up),
          move.up(pos),
          move.up(pos).flatMap(move::left),
          move.left(pos),
          move.down(pos).flatMap(move::left),
          move.right(pos),
          move.right(pos).flatMap(move::down),
          move.right(pos, 2).flatMap(move::down)
        ).flatMap(Optional::stream);
      }
      case SHIP_START_SOUTH: {
        return Stream.of(
          move.up(pos, 2).flatMap(move::right),
          move.up(pos).flatMap(move::right),
          move.right(pos),
          move.down(pos).flatMap(move::right),
          move.down(pos),
          move.down(pos).flatMap(move::left),
          move.left(pos),
          move.up(pos).flatMap(move::left),
          move.up(pos, 2).flatMap(move::left)
        ).flatMap(Optional::stream);
      }
      case SHIP_START_EAST: {
        return Stream.of(
          move.left(pos, 2).flatMap(move::up),
          move.left(pos).flatMap(move::up),
          move.up(pos),
          move.left(pos).flatMap(move::up),
          move.right(pos),
          move.right(pos).flatMap(move::down),
          move.right(pos),
          move.left(pos).flatMap(move::down),
          move.left(pos, 2).flatMap(move::down)
        ).flatMap(Optional::stream);
      }
    }
    return Stream.<Pos>of();
  }

  private Grid<Character> completeRows(Grid<Character> grid) {
    Grid.Builder<Character> gridOverlay = grid.copy();
    IntStream.range(0, rowCounts.size()).forEach(col -> {
      int count = rowCounts.get(col);
      int water = (int) IntStream.range(0, columnCounts.size()).mapToObj(row -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).orElse(' ') == WATER).count();
      if ((water + count) == columnCounts.size()) {
        IntStream.range(0, columnCounts.size()).mapToObj(row -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).isEmpty()).forEach(pos -> gridOverlay.add(Cell.at(pos).withValue(SOME_SHIP_PART)));
      }
    });
    return gridOverlay.addedCells() ? gridOverlay.build() : grid;
  }

  private Grid<Character> completeColumns(Grid<Character> grid) {
    Grid.Builder<Character> gridOverlay = grid.copy();
    IntStream.range(0, columnCounts.size()).forEach(row -> {
      int count = columnCounts.get(row);
      int water = (int) IntStream.range(0, rowCounts.size()).mapToObj(col -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).orElse(' ') == WATER).count();
      if ((water + count) == rowCounts.size()) {
        IntStream.range(0, rowCounts.size()).mapToObj(col -> Pos.at(row, col)).filter(pos -> grid.valueAt(pos).isEmpty()).forEach(pos -> gridOverlay.add(Cell.at(pos).withValue(SOME_SHIP_PART)));
      }
    });
    return gridOverlay.addedCells() ? gridOverlay.build() : grid;
  }

  private void print(Grid<Character> grid) {
    int rasterSize = (2 * grid.width() + 1) + 1;

    CharRaster raster = CharRaster.builder().ofWidth(rasterSize).ofHeight(rasterSize).build();

    GridPrinter<Character> gridPrinter = new GridPrinter<Character>(raster::set, Function.identity());

    gridPrinter.print(grid);

    for (int row = 0; row < rowCounts.size(); row++) {
      raster.set(Pos.at(rasterSize - 1, 2 * row + 1), (char) ('0' + rowCounts.get(row)));
    }

    for (int col = 0; col < columnCounts.size(); col++) {
      raster.set(Pos.at(2 * col + 1, rasterSize - 1), (char) ('0' + columnCounts.get(col)));
    }

    raster.lines().forEach(System.out::println);
  }

  public static class Solution {

    private final Grid<Character> grid;
    private final List<Pos> submarines;
    private final List<List<Pos>> destroyers;
    private final List<List<Pos>> cruisers;
    private final List<List<Pos>> battleships;

    private Solution(Builder builder) {
      grid = builder.gridBuilder.build();
      submarines = builder.submarines;
      destroyers = builder.destroyers;
      cruisers = builder.cruisers;
      battleships = builder.battleships;
    }

    public Grid<Character> grid() { return grid; }

    public boolean isValid() {
      return (
        battleships.size() <= 1 &&
        cruisers.size() <= 2 &&
        destroyers.size() <= 3 &&
        submarines.size() <= 4
      );
    }

    public boolean isComplete() {
      return (
        battleships.size() == 1 &&
        cruisers.size() == 2 &&
        destroyers.size() == 3 &&
        submarines.size() == 4
      );
    }

    public boolean countsMatch(List<Integer> rowCounts, List<Integer> columnCounts) {
      for (int y = 0; y < rowCounts.size(); y++) {
        int count = rowCounts.get(y);
        int actual = (int) grid.cellsMatching(c -> c.value().isPresent())
          .filter(inColumn(y))
          .filter(cell -> !grid.valueAt(cell.pos()).get().equals(WATER))
          .count();
        if (count != actual) { return false; }
      }
      for (int x = 0; x < columnCounts.size(); x++) {
        int count = columnCounts.get(x);
        int actual = (int) grid.cellsMatching(c -> c.value().isPresent())
          .filter(inRow(x))
          .filter(cell -> !grid.valueAt(cell.pos()).get().equals(WATER))
          .count();
        if (count != actual) { return false; }
      }
      return true;
    }

    private Predicate<Cell<?>> inRow(int x) { return (cell -> cell.pos().x() == x); }

    private Predicate<Cell<?>> inColumn(int y) { return (cell -> cell.pos().y() == y); }

    public List<Placement> placementsFor(Ship ship, List<Integer> rowCounts, List<Integer> columnCounts) {
      List<Placement> placements = new ArrayList<>();
      for (int y = 0; y < rowCounts.size(); y++) {
        if (rowCounts.get(y) < ship.length()) { continue; }
        for (int x = 0; x < columnCounts.size(); x++) {
          Placement placement = Placement.at(Pos.at(x, y), Direction.RIGHT);
          if (canPlaceShipAt(ship, placement)) { placements.add(placement); }
        }
      }
      for (int x = 0; x < columnCounts.size(); x++) {
        if (columnCounts.get(x) < ship.length()) { continue; }
        for (int y = 0; y < rowCounts.size(); y++) {
          Placement placement = Placement.at(Pos.at(x, y), Direction.DOWN);
          if (canPlaceShipAt(ship, placement)) { placements.add(placement); }
        }
      }
      return placements;
    }
  
    public boolean canPlaceShipAt(Ship ship, Placement placement) {
      char start = maybeShip(placement, 0);
      if (ship.length() == 1) { return (start == SOME_SHIP_PART); }
      if (!(start == SOME_SHIP_PART || start == placement.direction.start())) { return false; }

      char end = maybeShip(placement, ship.length() - 1);
      if (!(end == SOME_SHIP_PART || end == placement.direction.end())) { return false; }

      if (ship.length() > 2) {
        for (int i = 1; i < ship.length() - 1; i++) {
          char middle = maybeShip(placement, i);
          if (!(middle == SOME_SHIP_PART || middle == SHIP_SECTION)) { return false; }
        }
      }

      if (!isBoundary(placement.move(ship.length()))) { return false; }

      // is there already a ship there?
      Pos shipStart = placement.move(0).get();
      switch (ship) {
        case SUBMARINE:
          if (submarines.contains(shipStart)) { return false; }
          break;
        case DESTROYER:
          for (List<Pos> s : destroyers) { if (s.get(0).equals(shipStart)) { return false; }}
          break;
        case CRUISER:
          for (List<Pos> s : cruisers) { if (s.get(0).equals(shipStart)) { return false; }}
          break;
        case BATTLESHIP:
          for (List<Pos> s : battleships) { if (s.get(0).equals(shipStart)) { return false; }}
          break;
      }
      return true;
    }

    private boolean isBoundary(Optional<Pos> pos) {
      return pos.map(p -> grid.valueAt(p).orElse(WATER)).orElse(WATER).equals(WATER);
    }

    private Solution placeShipAt(Ship ship, Placement placement) {
      Grid.Builder<Character> gridBuilder = grid.copy();
      switch (ship) {
        case SUBMARINE:
          gridBuilder.add(Cell.at(placement.move(0).get()).withValue(SUBMARINE));
          break;
        default: {
          gridBuilder.add(Cell.at(placement.move(0).get()).withValue(placement.direction.start()));
          for (int i = 1; i < ship.length() - 1; i++) {
            gridBuilder.add(Cell.at(placement.move(i).get()).withValue(SHIP_SECTION));
          }
          gridBuilder.add(Cell.at(placement.move(ship.length() - 1).get()).withValue(placement.direction.end()));
        }
      }
      return builder(gridBuilder.build()).build();
    }
  
    public char maybeShip(Placement placement, int n) {
      return placement.move(n).map(p -> grid.valueAt(p).orElse(SOME_SHIP_PART)).orElse(WATER);
    }

    public static Builder builder(Grid<Character> grid) { return new Builder(grid); }

    public static class Builder {

      private final Grid<Character> grid;
      private final Grid.Builder<Character> gridBuilder;

      private final Set<Pos> visited = new HashSet<>();
      private final List<Pos> submarines = new ArrayList<>();
      private final List<List<Pos>> destroyers = new ArrayList<>();
      private final List<List<Pos>> cruisers = new ArrayList<>();
      private final List<List<Pos>> battleships = new ArrayList<>();

      private Builder(Grid<Character> grid) {
        this.grid = grid;
        gridBuilder = Grid.builder(Character.class).add(Cell.at(grid.width() - 1, grid.height() - 1));
        grid.cellsMatching(c -> c.value().isPresent()).forEach(cell -> {
          Pos pos = cell.pos();
          if (!visited.contains(pos)) {
            gridBuilder.add(cell);
            switch (cell.value().get()) {
              case SUBMARINE:
                addSubmarine(pos);
                break;
              case SHIP_START_NORTH: {
                Placement down = Placement.at(pos, Direction.DOWN);
                if (isBattleship(down)) { addBattleship(down); }
                else if (isCruiser(down)) { addCruiser(down); }
                else if (isDestroyer(down)) { addDestroyer(down); }
                break;
              }
              case SHIP_START_WEST: {
                Placement right = Placement.at(pos, Direction.RIGHT);
                if (isBattleship(right)) { addBattleship(right); }
                else if (isCruiser(right)) { addCruiser(right); }
                else if (isDestroyer(right)) { addDestroyer(right); }
                break;
              }
              case SOME_SHIP_PART: {
                Placement down = Placement.at(pos, Direction.DOWN);
                Placement right = Placement.at(pos, Direction.RIGHT);
                if (isSubmarine(pos)) { addSubmarine(pos); }
                else if (isBattleship(down)) { addBattleship(down); }
                else if (isCruiser(down)) { addCruiser(down); }
                else if (isDestroyer(down)) { addDestroyer(down); }
                else if (isBattleship(right)) { addBattleship(right); }
                else if (isCruiser(right)) { addCruiser(right); }
                else if (isDestroyer(right)) { addDestroyer(right); }
                break;
              }
            }
          }
        });
      }

      private boolean isSubmarine(Pos pos) {
        return (
          isBoundary(Direction.DOWN.move(pos, -1)) &&
          isBoundary(Direction.DOWN.move(pos, 1)) &&
          isBoundary(Direction.RIGHT.move(pos, -1)) &&
          isBoundary(Direction.RIGHT.move(pos, 1))
        );
      }

      private void addSubmarine(Pos pos) {
        gridBuilder.add(Cell.at(pos).withValue(SUBMARINE));
        visited.add(pos);
        submarines.add(pos);
      }

      private boolean shipAt(Ship ship, Placement placement) {
        return (
          isBoundary(placement.move(-1)) &&
          isBoundary(placement.move(ship.length())) &&
          IntStream.range(1, ship.length()).allMatch(n -> isShip(placement.move(n)))
        );
      }

      private boolean isBattleship(Placement placement) {
        return shipAt(Ship.BATTLESHIP, placement);
      }

      private void addBattleship(Placement placement) {
        List<Pos> battleship = List.of(
          placement.move(0).get(),
          placement.move(1).get(),
          placement.move(2).get(),
          placement.move(3).get()
        );
        gridBuilder.add(Cell.at(battleship.get(0)).withValue(placement.direction.start()));
        gridBuilder.add(Cell.at(battleship.get(1)).withValue(SHIP_SECTION));
        gridBuilder.add(Cell.at(battleship.get(2)).withValue(SHIP_SECTION));
        gridBuilder.add(Cell.at(battleship.get(3)).withValue(placement.direction.end()));
        battleship.forEach(p -> visited.add(p));
        battleships.add(battleship);
      }

      private boolean isCruiser(Placement placement) {
        return shipAt(Ship.CRUISER, placement);
      }

      private void addCruiser(Placement placement) {
        List<Pos> cruiser = List.of(
          placement.move(0).get(),
          placement.move(1).get(),
          placement.move(2).get()
        );
        gridBuilder.add(Cell.at(cruiser.get(0)).withValue(placement.direction.start()));
        gridBuilder.add(Cell.at(cruiser.get(1)).withValue(SHIP_SECTION));
        gridBuilder.add(Cell.at(cruiser.get(2)).withValue(placement.direction.end()));
        cruiser.forEach(p -> visited.add(p));
        cruisers.add(cruiser);
      }

      private boolean isDestroyer(Placement placement) {
        return shipAt(Ship.DESTROYER, placement);
      }

      private void addDestroyer(Placement placement) {
        List<Pos> destroyer = List.of(
          placement.move(0).get(),
          placement.move(1).get()
        );
        gridBuilder.add(Cell.at(destroyer.get(0)).withValue(placement.direction.start()));
        gridBuilder.add(Cell.at(destroyer.get(1)).withValue(placement.direction.end()));
        destroyer.forEach(p -> visited.add(p));
        destroyers.add(destroyer);
      }

      private boolean isBoundary(Optional<Pos> pos) {
        return pos.isEmpty() || grid.valueAt(pos.get()).orElse(SOME_SHIP_PART).equals(WATER);
      }

      private boolean isShip(Optional<Pos> pos) {
        char value = flatten(pos.map(p -> grid.valueAt(p))).orElse(WATER);
        return value != WATER;
      }

      private Solution build() { return new Solution(this); }
    }
  }

  public enum Direction {
    DOWN {
      @Override public Optional<Pos> move(Pos pos, int n) { return validPos(pos.x(), pos.y() + n); }
      @Override public char start() { return SHIP_START_NORTH; }
      @Override public char end() { return SHIP_START_SOUTH; }
    },
    RIGHT {
      @Override public Optional<Pos> move(Pos pos, int n) { return validPos(pos.x() + n, pos.y()); }
      @Override public char start() { return SHIP_START_WEST; }
      @Override public char end() { return SHIP_START_EAST; }
    };
    public abstract Optional<Pos> move(Pos pos, int n);
    public abstract char start();
    public abstract char end();
    protected Optional<Pos> validPos(int x, int y) {
      return (x >= 0 && x < 10 && y >= 0 && y < 10) ? Optional.of(Pos.at(x, y)) : Optional.empty();
    }
  }

  public static class Placement {
    public static Placement at(Pos pos, Direction direction) { return new Placement(pos, direction); }
    private final Pos pos;
    private final Direction direction;
    private Placement(Pos pos, Direction direction) {
      this.pos = pos;
      this.direction = direction;
    }
    public Optional<Pos> move(int n) { return direction.move(pos, n); }
    @Override public String toString() {
      return String.format("Placement[%s %s]", pos, direction.name());
    }
  }

  public enum Ship {
    SUBMARINE(1),
    DESTROYER(2),
    CRUISER(3),
    BATTLESHIP(4);
    private final int length;
    private Ship(int length) { this.length = length; }
    public int length() { return length; }
  }

  public static final char SOME_SHIP_PART = '\u25A3';
  public static final char SHIP_START_NORTH = '\u25B2';
  public static final char SHIP_START_EAST = '\u25B6';
  public static final char SHIP_START_SOUTH = '\u25BC';
  public static final char SHIP_START_WEST = '\u25C0';
  public static final char SUBMARINE = '\u25CF';
  public static final char WATER = '\u25FB';
  public static final char SHIP_SECTION = '\u25FC';

  public static <T> Optional<T> flatten(Optional<Optional<T>> v) {
    return v.isEmpty() ? Optional.empty() : v.get();
  }
}
