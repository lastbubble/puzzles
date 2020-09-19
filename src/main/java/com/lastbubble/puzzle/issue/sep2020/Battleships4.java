package com.lastbubble.puzzle.issue.sep2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships4 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(6, 0, 1, 4, 1, 1, 1, 2, 1, 3); }

  @Override protected IntStream columnCounts() { return IntStream.of(2, 4, 2, 2, 0, 2, 1, 1, 6, 0); }

  @Override protected void addValues() {
    addShipStartAt(7, 7);
    addWaterAt(0, 0);
    addWaterAt(3, 3);
    addWaterAt(5, 9);
  }
}
