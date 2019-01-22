package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares12 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue('3'))
      .add(Cell.at(4, 0).withValue('3'))
      .add(Cell.at(0, 1).withValue('3'))
      .add(Cell.at(5, 1).withValue('4'))
      .add(Cell.at(2, 3).withValue('4'))
      .add(Cell.at(0, 4).withValue(Arrow.SOUTHEAST.symbol()))
      .add(Cell.at(2, 4).withValue('4'))
      .add(Cell.at(3, 5).withValue(Arrow.NORTHWEST.symbol()))
      .add(Cell.at(4, 5).withValue(Arrow.NORTHEAST.symbol()))
      .add(Cell.at(5, 5).withValue('4'))
      .add(Cell.at(0, 6).withValue('1'))
      .add(Cell.at(3, 6).withValue(Arrow.WEST.symbol()))
      .add(Cell.at(6, 6));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(4, 1);
    minesInColumn(2, 3);
    minesInColumn(3, 4);
    minesInRow(2, 0);
    minesInRow(3, 3);
    minesInRow(2, 6);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(0, 0), Pos.at(1, 0)),
      Stream.of(Pos.at(6, 0), Pos.at(6, 1)),
      Stream.of(Pos.at(0, 3), Pos.at(1, 3)),
      Stream.of(Pos.at(0, 5), Pos.at(1, 5)),
      Stream.of(Pos.at(3, 2), Pos.at(4, 2), Pos.at(3, 3), Pos.at(4, 3), Pos.at(5, 3), Pos.at(3, 4), Pos.at(4, 4)),
      Stream.of(Pos.at(6, 5), Pos.at(6, 6))
    );
  }
}
