package com.lastbubble.puzzle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Grid<V> {

  private final V[][] values;

  private Grid(V[][] values) { this.values = values; }

  public int width() { return values.length; }

  public int height() { return values.length > 0 ? values[0].length : 0; }

  public Optional<V> valueAt(Pos pos) { return valueAt(pos.x(), pos.y()); }

  public Optional<V> valueAt(int x, int y) { return Optional.ofNullable(values[x][y]); }

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

    @SuppressWarnings("unchecked")
    public Grid<V> build() {
      V[][] values = (V[][]) Array.newInstance(valueClass, xMax + 1, yMax + 1);

      cells.stream().forEach(c -> values[c.pos().x()][c.pos().y()] = c.value().orElse(null));

      return new Grid<V>(values);
    }
  }
}
