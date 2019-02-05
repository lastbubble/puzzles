package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships5 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(3, 1, 5, 1, 1, 2, 1, 1, 4, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(3, 0, 1, 3, 3, 1, 2, 4, 1, 2); }

  @Override protected void addValues() {
    addShipSectionAt(4, 0);
    addSubmarineAt(8, 7);
    addWaterAt(0, 2);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 5, 0);
  }
}
