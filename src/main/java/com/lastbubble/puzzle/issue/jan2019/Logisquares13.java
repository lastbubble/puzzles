package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares13 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue(Arrow.SOUTH.symbol()))
      .add(Cell.at(3, 0).withValue('3'))
      .add(Cell.at(5, 0).withValue('2'))
      .add(Cell.at(6, 0).withValue('1'))
      .add(Cell.at(0, 2).withValue('2'))
      .add(Cell.at(4, 2).withValue('4'))
      .add(Cell.at(0, 3).withValue(Arrow.SOUTH.symbol()))
      .add(Cell.at(4, 3).withValue(Arrow.WEST.symbol()))
      .add(Cell.at(5, 3).withValue('4'))
      .add(Cell.at(0, 4).withValue('2'))
      .add(Cell.at(2, 4).withValue(Arrow.NORTHEAST.symbol()))
      .add(Cell.at(6, 4).withValue('2'))
      .add(Cell.at(2, 5).withValue('3'))
      .add(Cell.at(5, 5).withValue('5'))
      .add(Cell.at(2, 6).withValue(Arrow.NORTHEAST.symbol()))
      .add(Cell.at(3, 6).withValue('2'))
      .add(Cell.at(6, 6));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(1, 0);
    minesInColumn(5, 1);
    minesInColumn(4, 3);
    minesInColumn(3, 4);
    minesInColumn(2, 5);
    minesInRow(2, 0);
    minesInRow(4, 1);
    minesInRow(2, 3);
    minesInRow(2, 4);
    minesInRow(3, 5);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(4, 1), Pos.at(5, 1), Pos.at(5, 2)),
      Stream.of(Pos.at(2, 2), Pos.at(2, 3), Pos.at(3, 3), Pos.at(3, 4)),
      Stream.of(Pos.at(0, 5), Pos.at(1, 5), Pos.at(1, 6))
    );
  }
}
