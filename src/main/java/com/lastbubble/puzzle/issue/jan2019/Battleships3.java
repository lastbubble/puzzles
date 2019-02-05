package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships3 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 3, 1, 3, 1, 1, 3, 2, 2, 2); }

  @Override protected IntStream columnCounts() { return IntStream.of(4, 4, 0, 2, 0, 1, 2, 0, 0, 7); }

  @Override protected void addValues() {
    addShipStartAt(6, 0);
    addSubmarineAt(0, 5);
    addWaterAt(9, 4);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 6, 1);
  }
}
