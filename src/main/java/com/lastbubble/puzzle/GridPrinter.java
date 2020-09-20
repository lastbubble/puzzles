package com.lastbubble.puzzle;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class GridPrinter<V> {

  private final Function<V, Character> valueAsChar;
  private Weight intraRegionBorderWeight = Weight.LIGHT;

  public GridPrinter(Function<V, Character> valueAsChar) { this.valueAsChar = valueAsChar; }

  public void suppressIntraRegionBorders() { intraRegionBorderWeight = Weight.NONE; }

  public void printTo(PrintWriter writer, Grid<V> grid) {
    printTo(writer, grid, (a, b) -> true);
  }

  public void printTo(PrintWriter writer, Grid<V> grid, BiPredicate<Pos, Pos> sameRegion) {
    int width = 2 * grid.width() + 1, height = 2 * grid.height() + 1;

    char[] row = new char[width];

    BiFunction<Integer, Integer, Character> charAt = new CharAt(grid, sameRegion);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        row[x] = charAt.apply(x, y);
      }
      writer.println( new String(row));
    }
  }

  public enum Weight { LIGHT, HEAVY, NONE; }

  public static class BorderBuilder {
    private Weight top, left, bottom, right;

    public BorderBuilder() { reset(); }

    public BorderBuilder reset() {
      return top(Weight.NONE).left(Weight.NONE).bottom(Weight.NONE).right(Weight.NONE);
    }

    public BorderBuilder top(Weight w) { top = w; return this; }
    public BorderBuilder bottom(Weight w) { bottom = w; return this; }
    public BorderBuilder left(Weight w) { left = w; return this; }
    public BorderBuilder right(Weight w) { right = w; return this; }
    public BorderBuilder horizontal(Weight w) { return left(w).right(w); }
    public BorderBuilder vertical(Weight w) { return top(w).bottom(w); }

    public char build() {
      String key = String.format("%c%c%c%c",
        top.name().charAt(0),
        left.name().charAt(0),
        bottom.name().charAt(0),
        right.name().charAt(0)
      );
      return borderChars.getOrDefault(key, ' ');
    }
  }

  private static final Map<String, Character> borderChars = new HashMap<>();

  static {
    borderChars.put("NLNL", '\u2500'); // light horizontal
    borderChars.put("NHNH", '\u2501'); // heavy horizontal
    borderChars.put("LNLN", '\u2502'); // light vertical
    borderChars.put("HNHN", '\u2503'); // heavy vertical
    borderChars.put("NNLL", '\u250C'); // light down and right
    borderChars.put("NNLH", '\u250D'); // down light and right heavy
    borderChars.put("NNHL", '\u250E'); // down heavy and right light
    borderChars.put("NNHH", '\u250F'); // down heavy and right heavy
    borderChars.put("NLLN", '\u2510'); // light down and left
    borderChars.put("NHLN", '\u2511'); // down light and left heavy
    borderChars.put("NLHN", '\u2512'); // down heavy and left light
    borderChars.put("NHHN", '\u2513'); // down heavy and left heavy
    borderChars.put("LNNL", '\u2514'); // light up and right
    borderChars.put("LNNH", '\u2515'); // up light and right heavy
    borderChars.put("HNNL", '\u2516'); // up heavy and right light
    borderChars.put("HNNH", '\u2517'); // up heavy and right heavy
    borderChars.put("LLNN", '\u2518'); // light up and left
    borderChars.put("LHNN", '\u2519'); // up light and left heavy
    borderChars.put("HLNN", '\u251A'); // up heavy and left light
    borderChars.put("HHNN", '\u251B'); // up heavy and left heavy
    borderChars.put("LNLL", '\u251C'); // light vertical and right
    borderChars.put("LNLH", '\u251D'); // vertical right and right heavy
    borderChars.put("HNLL", '\u251E'); // up heavy and right down light
    borderChars.put("LNHL", '\u251F'); // down heavy and right up light
    borderChars.put("HNHL", '\u2520'); // vertical heavy and right light
    borderChars.put("HNLH", '\u2521'); // down light and right up heavy
    borderChars.put("LNHH", '\u2522'); // up light and right down heavy
    borderChars.put("HNHH", '\u2523'); // heavy vertical and right
    borderChars.put("LLLN", '\u2524'); // light vertical and left
    borderChars.put("LHLN", '\u2525'); // vertical light and left heavy
    borderChars.put("HLLN", '\u2526'); // up heavy and left down light
    borderChars.put("LLHN", '\u2527'); // down heavy and left up light
    borderChars.put("HLHN", '\u2528'); // vertical heavy and left light
    borderChars.put("HHLN", '\u2529'); // down light and left up heavy
    borderChars.put("LHHN", '\u252A'); // up light and left down heavy
    borderChars.put("HHHN", '\u252B'); // heavy vertical and left
    borderChars.put("NLLL", '\u252C'); // light down and horizontal
    borderChars.put("NHLL", '\u252D'); // left heavy and right down light
    borderChars.put("NLLH", '\u252E'); // right heavy and left down light
    borderChars.put("NHLH", '\u252F'); // down light and horizontal heavy
    borderChars.put("NLHL", '\u2530'); // down heavy and horizontal light
    borderChars.put("NHHL", '\u2531'); // right light and left down heavy
    borderChars.put("NLHH", '\u2532'); // left light and right down heavy
    borderChars.put("NHHH", '\u2533'); // heavy down and horizontal
    borderChars.put("LLNL", '\u2534'); // light up and horizontal
    borderChars.put("LHNL", '\u2535'); // left heavy and right up light
    borderChars.put("LLNH", '\u2536'); // right heavy and left up light
    borderChars.put("LHNH", '\u2537'); // up light and horizontal heavy
    borderChars.put("HLNL", '\u2538'); // up heavy and horizontal light
    borderChars.put("HHNL", '\u2539'); // right light and left up heavy
    borderChars.put("HLNH", '\u253A'); // left light and right up heavy
    borderChars.put("HHNH", '\u253B'); // heavy up and horizontal
    borderChars.put("LLLL", '\u253C'); // light vertical and horizontal
    borderChars.put("LHLL", '\u253D'); // left heavy and right vertical light
    borderChars.put("LLLH", '\u253E'); // right heavy and left vertical light
    borderChars.put("LHLH", '\u253F'); // vertical light and horizontal heavy
    borderChars.put("HLLL", '\u2540'); // up heavy and down horizontal light
    borderChars.put("LHLL", '\u2541'); // down heavy and up horizontal light
    borderChars.put("HLHL", '\u2542'); // vertical heavy and horizontal light
    borderChars.put("HHLL", '\u2543'); // left up heavy and right down light
    borderChars.put("HLLH", '\u2544'); // right up heavy and left down light
    borderChars.put("LHHL", '\u2545'); // left down heavy and right up light
    borderChars.put("LLHH", '\u2546'); // right down heavy and left up light
    borderChars.put("HHLH", '\u2547'); // down light and up horizontal heavy
    borderChars.put("LHHH", '\u2548'); // up light and down horizontal heavy
    borderChars.put("HHHL", '\u2549'); // right light and left vertical heavy
    borderChars.put("HLHH", '\u254A'); // left light and right vertical heavy
    borderChars.put("HHHH", '\u254B'); // heavy vertical and horizontal
  }

  private class CharAt implements BiFunction<Integer, Integer, Character> {

    private final Grid<V> grid;
    private final BiPredicate<Pos, Pos> sameRegion;

    private final BorderBuilder border = new BorderBuilder();

    private CharAt(Grid<V> grid, BiPredicate<Pos, Pos> sameRegion) {
      this.grid = grid;
      this.sameRegion = sameRegion;
    }

    @Override public Character apply(Integer x, Integer y) {
      int gridX = x / 2;
      int gridY = y / 2;
      border.reset();
      if (y % 2 == 0) {
        if (x % 2 == 0) {
          return borderFor(gridX, gridY);
        } else {
          Optional<Pos> top = pos(gridX, gridY - 1);
          Optional<Pos> bottom = pos(gridX, gridY);
          return border.horizontal(weightFor(top, bottom)).build();
        }
      } else {
        if (x % 2 == 0) {
          Optional<Pos> left = pos(gridX - 1, gridY);
          Optional<Pos> right = pos(gridX, gridY);
          return border.vertical(weightFor(left, right)).build();
        } else {
          return grid.valueAt(gridX, gridY).map(valueAsChar).orElse(' ');
        }
      }
    }

    private char borderFor(int x, int y) {
      Optional<Pos> topLeft = pos(x - 1, y - 1);
      Optional<Pos> bottomLeft = pos(x - 1, y);
      Optional<Pos> topRight = pos(x, y - 1);
      Optional<Pos> bottomRight = pos(x, y);

      return border
        .top(weightFor(topLeft, topRight))
        .left(weightFor(topLeft, bottomLeft))
        .bottom(weightFor(bottomLeft, bottomRight))
        .right(weightFor(topRight, bottomRight))
        .build();
    }

    private Weight weightFor(Optional<Pos> a, Optional<Pos> b) {
      if (a.isPresent()) {
        if (b.isPresent()) {
          return sameRegion.test(a.get(), b.get()) ? intraRegionBorderWeight : Weight.HEAVY;
        } else {
          return Weight.HEAVY;
        }
      } else if (b.isPresent()) {
        return Weight.HEAVY;
      } else {
        return Weight.NONE;
      }
    }

    private Optional<Pos> pos(int x, int y) { return grid.validPos(x, y); }
  }
}
