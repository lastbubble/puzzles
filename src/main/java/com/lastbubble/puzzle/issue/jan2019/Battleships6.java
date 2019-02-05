package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships6 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(0, 2, 2, 1, 1, 3, 2, 3, 4, 2); }

  @Override protected IntStream columnCounts() { return IntStream.of(0, 4, 0, 4, 0, 2, 4, 1, 4, 1); }

  @Override protected void addValues() {
    addShipStartAt(5, 5);
    addSubmarineAt(7, 8);
    addWaterAt(3, 4);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 6, 5);
  }
}
