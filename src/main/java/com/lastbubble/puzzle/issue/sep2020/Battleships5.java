package com.lastbubble.puzzle.issue.sep2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships5 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 2, 3, 1, 0, 1, 4, 2, 3, 2); }

  @Override protected IntStream columnCounts() { return IntStream.of(1, 4, 2, 3, 1, 2, 2, 3, 0, 2); }

  @Override protected void addValues() {
    addShipStartAt(4, 8);
    addShipStartAt(9, 1);
    addWaterAt(1, 0);
    addWaterAt(3, 0);
  }
}
