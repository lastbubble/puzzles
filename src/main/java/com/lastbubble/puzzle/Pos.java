package com.lastbubble.puzzle;

import java.util.Objects;

public class Pos {

  public static Pos at(int x, int y) {
    checkArgument(x >= 0, "x cannot be negative");
    checkArgument(y >= 0, "y cannot be negative");

    return new Pos(x, y);
  }

  private static void checkArgument(boolean b, String reason) {
    if (!b) { throw new IllegalArgumentException(reason); }
  }

  private final int x;
  private final int y;

  private Pos(int x, int y) { this.x = x; this.y = y; }

  public int x() { return x; }
  public int y() { return y; }

  @Override public int hashCode() { return Objects.hash(x, y); }

  @Override public boolean equals(Object obj) {
    if (obj == this) { return true; }

    if (obj instanceof Pos) {
      Pos that = (Pos) obj;
      return (this.x() == that.x() && this.y() == that.y());
    }

    return false;
  }

  @Override public String toString() { return String.format("(%d,%d)", x(), y()); }
}