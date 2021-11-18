package com.lastbubble.puzzle.issue.jan2022;

import java.util.stream.Stream;

import com.lastbubble.puzzle.common.Cell;

public class Flustered1 extends Flustered {

  @Override protected Stream<String> words() {
    return Stream.of(
      "BLAND", "BYES", "CALF", "CALM", "CANTORS", "DAMN", "ENACT", "FEND", "FIEND", "LAMB", "LEIS",
      "MACROS", "MANLY", "SORELY", "SORT", "SOURLY", "TOES", "TURN");
  }

  @Override protected Stream<Cell<Character>> knownCells() {
    return Stream.of(Cell.at(2, 0).withValue('O'));
  }
}
