package com.lastbubble.puzzle.issue.oct2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships4 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(5, 0, 3, 2, 4, 0, 1, 2, 2, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(1, 3, 0, 6, 1, 3, 1, 2, 2, 1); }

  @Override protected void addValues() {
    addShipSectionAt(1, 7);
    addWaterAt(3, 2);
    addWaterAt(5, 4);
    addWaterAt(9, 2);
  }
}
