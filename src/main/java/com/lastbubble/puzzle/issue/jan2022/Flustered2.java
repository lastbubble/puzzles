package com.lastbubble.puzzle.issue.jan2022;

import java.util.stream.Stream;

import com.lastbubble.puzzle.common.Cell;

public class Flustered2 extends Flustered {

  @Override protected Stream<String> words() {
    return Stream.of(
      "BYTES", "CLOY", "COBRA", "COTE", "DAMNS", "DAMP", "DELTA", "DENT", "DENY", "IRATE", "KAYO",
      "LENDS", "NARY", "OCTANES", "PRAY", "SNAP"
    );
  }

  @Override protected Stream<Cell<Character>> knownCells() {
    return Stream.of(Cell.at(2, 0).withValue('D'));
  }
}
