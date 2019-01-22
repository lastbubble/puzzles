package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares5 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue('4'))
      .add(Cell.at(4, 0).withValue('2'))
      .add(Cell.at(2, 2).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(3, 2).withValue('3'))
      .add(Cell.at(0, 3).withValue('1'))
      .add(Cell.at(4, 4));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(3, 1);
    minesInColumn(1, 3);
    minesInColumn(2, 4);
    minesInRow(3, 1);
    minesInRow(2, 3);
    minesInRow(1, 4);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(0, 0), Pos.at(1, 0), Pos.at(0, 1)),
      Stream.of(Pos.at(3, 3), Pos.at(4, 3), Pos.at(2, 4), Pos.at(3, 4), Pos.at(4, 4))
    );
  }
}
