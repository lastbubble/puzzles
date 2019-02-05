package com.lastbubble.puzzle.issue.feb2019;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships4 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 1, 1, 2, 1, 3, 1, 2, 4, 3); }

  @Override protected IntStream columnCounts() { return IntStream.of(4, 0, 1, 5, 1, 2, 1, 1, 3, 2); }

  @Override protected void addValues() {
    addShipSectionAt(8, 2);
    addShipSectionAt(8, 3);
    addShipSectionAt(3, 9);
    addShipSectionAt(4, 9);
    addShipStartAt(9, 7);
    addShipStartAt(8, 1);
    addShipStartAt(2, 9);
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 9, 8) && isFilled(grid, 4, 9);
  }
}
