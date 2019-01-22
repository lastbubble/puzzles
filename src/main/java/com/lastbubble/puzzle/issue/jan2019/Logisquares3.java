package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares3 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue('2'))
      .add(Cell.at(1, 1).withValue(Arrow.EAST.symbol()))
      .add(Cell.at(2, 2).withValue('4'))
      .add(Cell.at(1, 3).withValue(Arrow.EAST.symbol()))
      .add(Cell.at(3, 3));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(2, 0);
    minesInRow(2, 0);
    minesInRow(2, 2);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(0, 0), Pos.at(0, 1), Pos.at(0, 2)),
      Stream.of(Pos.at(3, 0), Pos.at(3, 1), Pos.at(3, 2), Pos.at(3, 3))
    );
  }
}
