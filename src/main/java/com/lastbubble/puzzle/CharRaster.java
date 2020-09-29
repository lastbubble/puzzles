package com.lastbubble.puzzle;

import java.util.Arrays;
import java.util.stream.Stream;

public class CharRaster {

  private final char[][] lines;

  private CharRaster(Builder builder) {
    int width = Math.max(0, builder.width);
    int height = (width > 0) ? Math.max(0, builder.height) : 0;

    lines = new char[height][width];

    for (int y = 0; y < height; y++) { Arrays.fill(lines[y], ' '); }
  }

  public void set(Pos pos, char value) { lines[pos.y()][pos.x()] = value; }

  public Stream<String> lines() { return Arrays.stream(lines).map(chars -> new String(chars)); }

  public static Builder builder() { return new Builder(); }

  public static class Builder {

    private int width;
    private int height;

    public Builder ofWidth(int n) { width = n; return this; }

    public Builder ofHeight(int n) { height = n; return this; }

    public CharRaster build() { return new CharRaster(this); }
  }
}
