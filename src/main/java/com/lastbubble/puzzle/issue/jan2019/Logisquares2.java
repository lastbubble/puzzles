package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares2 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue('2'))
      .add(Cell.at(3, 0).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(2, 1).withValue('3'))
      .add(Cell.at(2, 3).withValue('2'));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(3, 0);
    minesInColumn(2, 1);
    minesInColumn(2, 3);
    minesInRow(2, 1);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(1, 2), Pos.at(2, 2), Pos.at(3, 2), Pos.at(0, 3), Pos.at(1, 3))
    );
  }
}
