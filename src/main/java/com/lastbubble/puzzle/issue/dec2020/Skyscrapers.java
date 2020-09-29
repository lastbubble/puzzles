package com.lastbubble.puzzle.issue.dec2020;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.CharRaster;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

public class Skyscrapers implements Runnable{

  public static Skyscrapers load(Iterable<String> lines) {
    EnumMap<Direction, List<Optional<Integer>>> directionCounts = new EnumMap<>(Direction.class);

    Pattern linePattern = Pattern.compile("([UDRL])=([0-9, ]+)");
    for (String line : lines) {
      Matcher m = linePattern.matcher(line);
      if (m.matches()) {
        directionCounts.put(
          Direction.beginningWith(m.group(1)).get(),
          Stream.of(m.group(2).split(",")).map(Skyscrapers::maybeInt).collect(toList())
        );
      }
    }

    if (directionCounts.size() != Direction.values().length) {
      throw new IllegalArgumentException("Not all directions have counts");
    }

    if (directionCounts.values().stream().map(l -> l.size()).distinct().limit(2).count() == 2) {
      throw new IllegalArgumentException("Not all direction counts have the same size");
    }

    return new Skyscrapers(directionCounts);
  }

  private static Optional<Integer> maybeInt(String s) {
    String trimmed = s.trim();
    Integer n = null;
    if (trimmed.length() > 0) {
      try { n = Integer.valueOf(trimmed); } catch (NumberFormatException e) { /* ignore */ }
    }
    return Optional.ofNullable(n);
  }

  private final EnumMap<Direction, List<Optional<Integer>>> directionCounts;

  private Skyscrapers(EnumMap<Direction, List<Optional<Integer>>> directionCounts) {
    this.directionCounts = directionCounts;
  }

  @Override public void run() {

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
    return new Solution(directionCounts, directionCounts.values().iterator().next().size());
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
    solution.toRaster().lines().forEach(line -> System.out.println(line));
  }

  public enum Direction {
    UP('\u2191',
      size -> Pos.at(0, size - 1),
      (pos, n) -> Pos.at(pos.x(), pos.y() - n)
    ),
    DOWN('\u2193',
      size -> Pos.at(0, 0),
      (pos, n) -> Pos.at(pos.x(), pos.y() + n)
    ),
    RIGHT('\u2192',
      size -> Pos.at(0, 0),
      (pos, n) -> Pos.at(pos.x() + n, pos.y())
    ),
    LEFT('\u2190',
      size -> Pos.at(size - 1, 0),
      (pos, n) -> Pos.at(pos.x() - n, pos.y())
    );

    private final char symbol;
    private final Function<Integer, Pos> firstPosition;
    private final BiFunction<Pos, Integer, Pos> move;

    private Direction(
      char symbol,
      Function<Integer, Pos> firstPosition,
      BiFunction<Pos, Integer, Pos> move
    ) {
      this.symbol = symbol;
      this.firstPosition = firstPosition;
      this.move = move;
    }

    public Pos firstPosition(int size) { return firstPosition.apply(size); }

    public Pos move(Pos pos, int n) { return move.apply(pos, n); }

    public Stream<Pos> view(int size, int i) {
      Pos firstPos = advance().move(firstPosition(size), i);
      return IntStream.range(0, size).mapToObj(x -> move(firstPos, x));
    }

    public Direction advance() {

      Pos testMove = move(Pos.at(1, 1), 1);
      return (testMove.x() != 1) ? DOWN : RIGHT;
    }

    public void printTo(List<Optional<Character>> values, BiConsumer<Pos, Character> consumer) {
      Pos firstPos = firstPosition(values.size());
      Pos adjustedFirstPos = Pos.at(2 + 2 * firstPos.x() + 1, 2 + 2 * firstPos.y() + 1);
      Pos startPos = move(adjustedFirstPos, -3);
      Direction advance = advance();
      IntStream.range(0, values.size()).forEach(i -> values.get(i).ifPresent(
        c -> {
          consumer.accept(advance.move(startPos, 2 * i), c);
          consumer.accept(move(advance.move(startPos, 2 * i), 1), symbol);
        }
      ));
    }

    public static Optional<Direction> beginningWith(String start) {
      for (Direction direction : values()) {
        if (direction.name().startsWith(start)) { return Optional.of(direction); }
      }
      return Optional.empty();
    }
  }

  private static class Solution {

    private final EnumMap<Direction, List<Optional<Integer>>> directionCounts;
    private final int size;
    private final Grid<Integer> grid;

    private Solution(EnumMap<Direction, List<Optional<Integer>>> directionCounts, int size) {
      this(directionCounts, Grid.builder(Integer.class, size).build());
    }

    private Solution(EnumMap<Direction, List<Optional<Integer>>> directionCounts, Grid<Integer> grid) {
      this.directionCounts = directionCounts;
      this.size = directionCounts.values().iterator().next().size();
      this.grid = grid;
    }

    private boolean isComplete() {
      if (grid.cellsMatching(c -> c.value().isEmpty()).count() != 0) { return false; }

      for (Direction direction : directionCounts.keySet()) {
        List<Optional<Integer>> counts = directionCounts.get(direction);
        for (int i = 0; i < counts.size(); i++) {
          Optional<Integer> ifView = counts.get(i);
          if (ifView.isPresent()) {
            List<Integer> values = direction.view(size, i).map(pos -> grid.valueAt(pos).get()).collect(toList());
            if (visibleIn(values) != ifView.get()) { return false; }
          }
        }
      }
      return true;
    }

    private Solution force() {
      Grid.Builder<Integer> gridBuilder = grid.copy();
      directionCounts.forEach((direction, counts) -> {
        IntStream.range(0, size).forEach(i ->
          counts.get(i).ifPresent(count -> {
            if (count == 1) {
              Pos pos = direction.advance().move(direction.firstPosition(size), i);
              gridBuilder.add(Cell.at(pos).withValue(size));
            } else if (count == size) {
              Pos pos = direction.advance().move(direction.firstPosition(size), i);
              IntStream.range(0, size).forEach(j ->
                gridBuilder.add(Cell.at(direction.move(pos, j)).withValue(j + 1))
              );
            }
          })
        );
      });
      return new Solution(directionCounts, gridBuilder.build());
    }

    private Iterable<Solution> reductions() {
      Optional<Cell<Integer>> ifNextCell = grid.cellsMatching(c -> c.value().isEmpty()).findFirst();

      if (ifNextCell.isEmpty()) { return List.of(); }

      Pos nextPos = ifNextCell.get().pos();

      List<Integer> row = rowContaining(nextPos);
      List<Integer> column = columnContaining(nextPos);

      List<Solution> reductions = new ArrayList<>();

      for (int i = size; i >= 1; i--) {

        if (row.contains(i) || column.contains(i)) { continue; }

        // mutating row and column here, but it's okay
        row.set(nextPos.x(), i);
        column.set(nextPos.y(), i);

        if (validViews(row, column, nextPos)) {
          reductions.add(
            new Solution(
              directionCounts,
              grid.copy().add(Cell.at(nextPos).withValue(i)).build()
            )
          );
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

    private boolean validViews(List<Integer> row, List<Integer> column, Pos pos) {

      if (isSatisfied(Direction.DOWN, column, pos) && isSatisfied(Direction.RIGHT, row, pos)) {
        List<Integer> reversedRow = new ArrayList<>(row);
        Collections.reverse(reversedRow);
        if (isSatisfied(Direction.LEFT, reversedRow, pos)) {
          List<Integer> reversedColumn = new ArrayList<>(column);
          Collections.reverse(reversedColumn);
          return isSatisfied(Direction.UP, reversedColumn, pos);
        }
      }
      return false;
    }

    private boolean isSatisfied(Direction direction, List<Integer> values, Pos pos) {

      switch (direction) {
        case DOWN:
          return directionCounts.get(direction).get(pos.x()).map(view -> viewIsPossible(values, view)).orElse(true);
        case RIGHT:
          return directionCounts.get(direction).get(pos.y()).map(view -> viewIsPossible(values, view)).orElse(true);
        case UP:
          return directionCounts.get(direction).get(pos.x()).map(view -> viewIsPossible(values, view)).orElse(true);
        case LEFT:
          return directionCounts.get(direction).get(pos.y()).map(view -> viewIsPossible(values, view)).orElse(true);
      }
      return true;
    }

    private boolean viewIsPossible(List<Integer> values, int view) {
      if (values.get(0) > 0 && visibleIn(values) > view) { return false; }

      for (int i = 0; i < values.size(); i++) {
        int value = values.get(i);
        if (view > (size - value + i + 1)) { return false; }
      }

      return true;
    }

    private int visibleIn(List<Integer> values) {
      int highest = 0, visible = 0;
      for (int value : values) {
        if (value > highest) { visible++; highest = value; }
      }
      return visible;
    }

    private CharRaster toRaster() {
      int rasterSize = 2 + (2 * size + 1) + 2;

      CharRaster raster = CharRaster.builder().ofWidth(rasterSize).ofHeight(rasterSize).build();
  
      Function<Integer, Character> toChar = n -> (char)('0' + n);
  
      directionCounts.forEach((direction, counts) ->
        direction.printTo(counts.stream().map(n -> n.map(toChar)).collect(toList()), raster::set)
      );
  
      grid.printTo((pos, c) -> { raster.set(Pos.at(pos.x() + 2, pos.y() + 2), c); }, toChar);

      return raster;
    }
  }
}
