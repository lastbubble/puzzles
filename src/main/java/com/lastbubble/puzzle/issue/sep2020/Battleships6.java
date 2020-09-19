package com.lastbubble.puzzle.issue.sep2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships6 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 2, 0, 1, 2, 2, 1, 2, 6, 2); }

  @Override protected IntStream columnCounts() { return IntStream.of(3, 1, 2, 1, 2, 1, 4, 1, 0, 5); }

  @Override protected void addValues() {
    addShipStartAt(0, 0);
    addSubmarineAt(9, 9);
    addWaterAt(2, 7);
    addWaterAt(4, 1);
  }
}
