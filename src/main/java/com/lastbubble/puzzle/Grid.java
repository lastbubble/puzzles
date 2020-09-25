package com.lastbubble.puzzle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Grid<V> {

  private final Class<V> valueClass;
  private final V[][] values;

  private Grid(Class<V> valueClass, V[][] values) {
    this.valueClass = valueClass;
    this.values = values;
  }

  public int width() { return values.length; }

  public int height() { return values.length > 0 ? values[0].length : 0; }

  public Stream<Pos> positions() {
    int width = width();
    return IntStream.range(0, width * height()).mapToObj(n -> {
      int row = n / width;
      return Pos.at(n - (width * row), row);
    });
  }

  public Stream<Pos> neighborsOf(Pos pos) {
    int x = pos.x(), y = pos.y();
    return Stream.of(
        validPos(x - 1, y - 1),
        validPos(    x, y - 1),
        validPos(x + 1, y - 1),
        validPos(x + 1,     y),
        validPos(x + 1, y + 1),
        validPos(    x, y + 1),
        validPos(x - 1, y + 1),
        validPos(x - 1,     y)
      ).filter(Optional::isPresent).map(Optional::get);
  }

  public Optional<Pos> validPos(int x, int y) {
    return (x >= 0 && x < width() && y >= 0 && y < height()) ? Optional.of(Pos.at(x, y)) : Optional.<Pos>empty();
  }

  public Optional<V> valueAt(Pos pos) { return valueAt(pos.x(), pos.y()); }

  public Optional<V> valueAt(int x, int y) { return Optional.ofNullable(values[x][y]); }

  public Stream<Cell<V>> filledCells() {
    return positions().filter(p -> values[p.x()][p.y()] != null).map(p -> Cell.at(p).withValue(values[p.x()][p.y()]));
  }

  public static <V> Builder<V> builder(Class<V> valueClass) { return new Builder<V>(valueClass); }

  public static class Builder<V> {

    private final Class<V> valueClass;
    private final List<Cell<V>> cells = new ArrayList<>();

    private int xMax = -1;
    private int yMax = -1;

    private Builder(Class<V> valueClass) { this.valueClass = valueClass; }

    public Builder<V> add(Cell<V> cell) {
      xMax = Math.max(xMax, cell.pos().x());
      yMax = Math.max(yMax, cell.pos().y());

      cells.add(cell);

      return this;
    }

    public Builder<V> copyOf(Grid<V> grid) {
      add(Cell.at(grid.width() - 1, grid.height() - 1));
      grid.filledCells().forEach(c -> add(c));
      return this;
    }

    @SuppressWarnings("unchecked")
    public Grid<V> build() {
      V[][] values = (V[][]) Array.newInstance(valueClass, xMax + 1, yMax + 1);

      cells.stream().forEach(c -> values[c.pos().x()][c.pos().y()] = c.value().orElse(null));

      return new Grid<V>(valueClass, values);
    }
  }

  public Overlay<V> overlay() { return new Overlay<V>(this); }

  public static class Overlay<V> {

    private Grid<V> grid;
    private final List<Cell<V>> addedCells = new ArrayList<>();

    private Overlay(Grid<V> grid) { this.grid = grid; }

    public Overlay<V> add(Cell<V> cell) {
      if (cell.value().isEmpty()) {
        throw new IllegalArgumentException("Can only overlay if cell has a value");
      }
      V value = cell.value().get();
      if (!value.equals(grid.valueAt(cell.pos()).orElse(value))) {
        throw new IllegalArgumentException(
          String.format("Cannot overlay existing value with different value (%s != %s)",
            grid.valueAt(cell.pos()), value
          )
        );
      }
      addedCells.add(cell);
      return this;
    }

    public Grid<V> finish() {
      if (addedCells.isEmpty()) { return grid; }
      Grid.Builder<V> gridBuilder = Grid.builder(grid.valueClass).copyOf(grid);
      addedCells.forEach(cell -> gridBuilder.add(cell));
      return gridBuilder.build();
    }
  }
}
