package com.lastbubble.puzzle.issue.feb2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships6 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(1, 1, 3, 6, 0, 4, 0, 2, 2, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(2, 4, 1, 2, 0, 1, 4, 2, 1, 3); }

  @Override protected void addValues() {
    addShipStartAt(7, 5);
    addWaterAt(0, 7);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 8, 5);
  }
}
