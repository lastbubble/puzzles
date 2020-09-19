package com.lastbubble.puzzle.issue.oct2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships5 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(3, 3, 1, 3, 0, 1, 2, 3, 3, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(5, 0, 5, 0, 4, 1, 2, 1, 0, 2); }

  @Override protected void addValues() {
    addSubmarineAt(0, 2);
    addShipSectionAt(4, 8);
    addWaterAt(2, 0);
    addWaterAt(6, 1);
  }
}
