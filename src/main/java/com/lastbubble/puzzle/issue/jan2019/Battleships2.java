package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships2 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(1, 3, 2, 1, 4, 0, 1, 4, 1, 3); }

  @Override protected IntStream columnCounts() { return IntStream.of(1, 2, 1, 2, 5, 2, 3, 1, 2, 1); }

  @Override protected void addValues() {
    addShipStartAt(3, 1);
    addShipStartAt(1, 2);
    addSubmarineAt(9, 0);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 3, 2) && isFilled(grid, 1, 3);
  }
}
