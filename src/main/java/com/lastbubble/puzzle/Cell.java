package com.lastbubble.puzzle;

import java.util.Optional;

public class Cell<V> {

  public static <V> Cell<V> at(int x, int y) { return at(Pos.at(x, y)); }

  public static <V> Cell<V> at(Pos pos) { return new Cell<V>(pos); }

  private final Pos pos;
  private final Optional<V> value;

  private Cell(Pos pos) { this(pos, null); }

  private Cell(Pos pos, V value) { this.pos = pos; this.value = Optional.ofNullable(value); }

  public Pos pos() { return pos; }

  public Optional<V> value() { return value; }

  public <D> Cell<D> withValue(D value) { return new Cell<D>(pos(), value); }
}
