package com.lastbubble.puzzle.issue.sep2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships1 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(0, 3, 4, 4, 2, 3, 0, 1, 2, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(3, 0, 1, 0, 4, 2, 0, 6, 0, 4); }

  @Override protected void addValues() {
    addShipStartAt(0, 4);
    addShipStartAt(5, 8);
    addWaterAt(7, 5);
  }
}
