package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares1 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(0, 0).withValue(Arrow.EAST.symbol()))
      .add(Cell.at(3, 1).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(0, 2).withValue('3'))
      .add(Cell.at(3, 3).withValue('2'));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(1, 1);
    minesInRow(2, 1);
    minesInRow(1, 2);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(1, 0), Pos.at(2, 0), Pos.at(1, 1), Pos.at(2, 1), Pos.at(2, 2))
    );
  }
}
