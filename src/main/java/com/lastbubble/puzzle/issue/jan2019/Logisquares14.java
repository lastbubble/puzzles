package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares14 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(3, 0).withValue('3'))
      .add(Cell.at(4, 0).withValue('1'))
      .add(Cell.at(6, 1).withValue('2'))
      .add(Cell.at(2, 2).withValue('5'))
      .add(Cell.at(4, 2).withValue('3'))
      .add(Cell.at(0, 3).withValue('3'))
      .add(Cell.at(1, 3).withValue(Arrow.SOUTHEAST.symbol()))
      .add(Cell.at(5, 3).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(0, 5).withValue('1'))
      .add(Cell.at(1, 5).withValue(Arrow.EAST.symbol()))
      .add(Cell.at(3, 5).withValue('4'))
      .add(Cell.at(5, 5).withValue('2'))
      .add(Cell.at(5, 6).withValue('1'))
      .add(Cell.at(6, 6));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(3, 0);
    minesInColumn(2, 3);
    minesInColumn(2, 4);
    minesInColumn(2, 5);
    minesInColumn(2, 6);
    minesInRow(4, 0);
    minesInRow(4, 1);
    minesInRow(3, 2);
    minesInRow(3, 3);
    minesInRow(1, 6);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(5, 0), Pos.at(4, 1), Pos.at(5, 1)),
      Stream.of(Pos.at(2, 5), Pos.at(2, 6)),
      Stream.of(Pos.at(4, 4), Pos.at(4, 5), Pos.at(4, 6)),
      Stream.of(Pos.at(6, 3), Pos.at(6, 4), Pos.at(6, 5), Pos.at(6, 6))
    );
  }
}
