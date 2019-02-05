package com.lastbubble.puzzle.issue.feb2019;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships3 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(1, 1, 0, 1, 1, 2, 3, 4, 5, 2); }

  @Override protected IntStream columnCounts() { return IntStream.of(2, 3, 0, 4, 0, 5, 0, 3, 0, 3); }

  @Override protected void addValues() {
    addSubmarineAt(3, 4);
    addSubmarineAt(3, 9);
    addWaterAt(7, 6);
  }
}
