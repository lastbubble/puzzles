package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships4 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(0, 2, 1, 3, 3, 2, 1, 1, 2, 5); }

  @Override protected IntStream columnCounts() { return IntStream.of(3, 0, 3, 0, 3, 2, 2, 1, 2, 4); }

  @Override protected void addValues() {
    addShipStartAt(8, 2);
    addSubmarineAt(4, 5);
    addWaterAt(0, 3);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 8, 3);
  }
}
