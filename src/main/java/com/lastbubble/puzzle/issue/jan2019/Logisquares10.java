package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares10 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue('3'))
      .add(Cell.at(4, 0).withValue('3'))
      .add(Cell.at(5, 0).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(2, 2).withValue(Arrow.WEST.symbol()))
      .add(Cell.at(3, 2).withValue(Arrow.NORTH.symbol()))
      .add(Cell.at(4, 2).withValue('4'))
      .add(Cell.at(5, 2).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(1, 3).withValue('1'))
      .add(Cell.at(2, 3).withValue(Arrow.SOUTH.symbol()))
      .add(Cell.at(3, 3).withValue(Arrow.EAST.symbol()))
      .add(Cell.at(1, 4).withValue(Arrow.EAST.symbol()))
      .add(Cell.at(1, 5).withValue('1'))
      .add(Cell.at(5, 5));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(2, 0);
    minesInColumn(1, 1);
    minesInColumn(1, 3);
    minesInColumn(3, 4);
    minesInRow(4, 1);
    minesInRow(1, 3);
    minesInRow(2, 5);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(0, 0), Pos.at(0, 1), Pos.at(1, 1), Pos.at(1, 2)),
      Stream.of(Pos.at(4, 3), Pos.at(4, 4), Pos.at(5, 4), Pos.at(5, 5))
    );
  }
}
