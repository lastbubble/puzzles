package com.lastbubble.puzzle.issue.sep2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships3 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(5, 2, 4, 2, 1, 2, 1, 1, 1, 1); }

  @Override protected IntStream columnCounts() { return IntStream.of(4, 2, 2, 2, 2, 1, 2, 0, 3, 2); }

  @Override protected void addValues() {
    addShipStartAt(2, 2);
    addSubmarineAt(0, 5);
    addSubmarineAt(3, 7);
    addSubmarineAt(6, 4);
    addWaterAt(0, 0);
  }
}
