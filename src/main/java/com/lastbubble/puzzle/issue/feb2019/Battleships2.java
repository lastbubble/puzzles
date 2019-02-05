package com.lastbubble.puzzle.issue.feb2019;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships2 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(2, 4, 2, 1, 1, 2, 2, 1, 1, 4); }

  @Override protected IntStream columnCounts() { return IntStream.of(3, 0, 2, 1, 0, 3, 4, 0, 6, 1); }

  @Override protected void addValues() {
    addSubmarineAt(8, 2);
    addShipSectionAt(5, 6);
  }
}
