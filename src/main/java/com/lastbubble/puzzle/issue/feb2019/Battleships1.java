package com.lastbubble.puzzle.issue.feb2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships1 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 6, 2, 2, 2, 0, 0, 2, 3, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(4, 1, 3, 1, 3, 0, 3, 1, 1, 3); }

  @Override protected void addValues() {
    addShipStartAt(7, 7);
    addWaterAt(0, 1);
    addWaterAt(4, 3);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 8, 7);
  }
}
