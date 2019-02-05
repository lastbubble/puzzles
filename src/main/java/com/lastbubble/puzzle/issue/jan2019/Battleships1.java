package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships1 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 1, 2, 1, 1, 0, 5, 2, 5, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(6, 0, 4, 1, 3, 1, 2, 0, 3, 0); }

  @Override protected void addValues() {
    addShipStartAt(2, 0);
    addShipStartAt(2, 8);
    addWaterAt(0, 6);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 3, 0) && isFilled(grid, 2, 9);
  }
}
