package com.lastbubble.puzzle.issue.jan2022;

import java.util.stream.Stream;

import com.lastbubble.puzzle.common.Cell;

public class Flustered3 extends Flustered {

  @Override protected Stream<String> words() {
    return Stream.of(
      "BEHOLDS", "CLONED", "COEDITS", "HEADS", "HOAR", "HUED", "IDOLS", "JOLTS", "RAIDS", "RAPIDS",
      "RIDS", "STAB", "TAPED", "TARP"
    );
  }

  @Override protected Stream<Cell<Character>> knownCells() {
    return Stream.of(Cell.at(2, 0).withValue('P'));
  }
}
