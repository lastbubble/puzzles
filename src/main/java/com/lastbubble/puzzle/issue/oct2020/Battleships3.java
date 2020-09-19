package com.lastbubble.puzzle.issue.oct2020;

import com.lastbubble.puzzle.Battleships;

import java.util.stream.IntStream;

public class Battleships3 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(0, 0, 1, 0, 1, 6, 2, 5, 1, 4); }

  @Override protected IntStream columnCounts() { return IntStream.of(6, 0, 4, 0, 2, 2, 2, 2, 2, 0); }

  @Override protected void addValues() {
    addWaterAt(4, 5);
  }
}
