package com.lastbubble.puzzle.issue.sep2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships2 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 1, 6, 1, 3, 1, 5, 0, 0, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(2, 4, 0, 2, 2, 1, 1, 2, 1, 5); }

  @Override protected void addValues() {
    addSubmarineAt(4, 9);
    addShipStartAt(9, 3);
    addWaterAt(3, 2);
  }
}
