package com.lastbubble.puzzle.issue.oct2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships6 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 2, 1, 2, 4, 1, 2, 1, 1, 4); }

  @Override protected IntStream columnCounts() { return IntStream.of(3, 0, 1, 1, 1, 4, 0, 4, 1, 5); }

  @Override protected void addValues() {
    addSubmarineAt(5, 1);
    addShipStartAt(0, 1);
    addShipStartAt(2, 0);
  }
}
