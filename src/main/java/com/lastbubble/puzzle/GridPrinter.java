package com.lastbubble.puzzle;

import java.io.PrintWriter;
import java.util.function.Function;

public class GridPrinter<V> {

  private final Function<V, Character> valueAsChar;

  public GridPrinter(Function<V, Character> valueAsChar) { this.valueAsChar = valueAsChar; }

  public void printTo(PrintWriter writer, Grid<V> grid) {
    int width = 2 * grid.width() + 1, height = 2 * grid.height() + 1;

    char[] row = new char[width];

    for (int y = 0; y < height; y++) {

      for (int j = 0; j < row.length; j++) { row[j] = ' '; }

      for (int x = 0; x < width; x++) {

        if (y % 2 == 0) {

          if (x % 2 == 0) {

            if (y == 0) {

              row[x] =
                (x == 0) ? SINGLE_DOWN_RIGHT : ((x == (width - 1)) ? SINGLE_DOWN_LEFT : SINGLE_DOWN_HORIZONTAL);

            } else if (y == (height - 1)) {

              row[x] =
                (x == 0) ? SINGLE_UP_RIGHT : ((x == (width - 1)) ? SINGLE_UP_LEFT : SINGLE_UP_HORIZONTAL);

            } else {

              row[x] =
                (x == 0) ? SINGLE_VERTICAL_RIGHT : ((x == (width - 1)) ? SINGLE_VERTICAL_LEFT : SINGLE_VERTICAL_HORIZONTAL);
            }

          } else {

            row[x] = SINGLE_HORIZONTAL;
          }

        } else {

          if (x % 2 == 0) { row[x] = SINGLE_VERTICAL; }
          else { row[x] = grid.valueAt((x - 1) / 2, (y - 1) / 2).map(valueAsChar).orElse(' '); }
        }
      }

      writer.println( new String(row));
    }
  }

  private static final char SINGLE_HORIZONTAL = '\u2500';
  private static final char SINGLE_VERTICAL = '\u2502';
  private static final char SINGLE_DOWN_RIGHT = '\u250C';
  private static final char SINGLE_DOWN_LEFT = '\u2510';
  private static final char SINGLE_UP_RIGHT = '\u2514';
  private static final char SINGLE_UP_LEFT = '\u2518';
  private static final char SINGLE_VERTICAL_RIGHT = '\u251c';
  private static final char SINGLE_VERTICAL_LEFT = '\u2524';
  private static final char SINGLE_DOWN_HORIZONTAL = '\u252c';
  private static final char SINGLE_UP_HORIZONTAL = '\u2534';
  private static final char SINGLE_VERTICAL_HORIZONTAL = '\u253c';
}
