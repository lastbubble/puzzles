package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares11 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(4, 0).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(1, 1).withValue('3'))
      .add(Cell.at(3, 1).withValue('2'))
      .add(Cell.at(4, 1).withValue('3'))
      .add(Cell.at(0, 2).withValue('3'))
      .add(Cell.at(2, 3).withValue(Arrow.NORTHWEST.symbol()))
      .add(Cell.at(3, 3).withValue('3'))
      .add(Cell.at(1, 4).withValue('2'))
      .add(Cell.at(4, 4).withValue('4'))
      .add(Cell.at(5, 4).withValue('2'))
      .add(Cell.at(0, 5).withValue(Arrow.NORTHEAST.symbol()))
      .add(Cell.at(1, 5).withValue(Arrow.NORTHEAST.symbol()))
      .add(Cell.at(2, 5).withValue('2'))
      .add(Cell.at(5, 5));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(2, 0);
    minesInColumn(1, 2);
    minesInColumn(2, 3);
    minesInColumn(2, 4);
    minesInRow(2, 1);
    minesInRow(2, 2);
    minesInRow(1, 4);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(2, 0), Pos.at(3, 0), Pos.at(2, 1), Pos.at(1, 2), Pos.at(2, 2)),
      Stream.of(Pos.at(5, 1), Pos.at(5, 2), Pos.at(5, 3))
    );
  }
}
