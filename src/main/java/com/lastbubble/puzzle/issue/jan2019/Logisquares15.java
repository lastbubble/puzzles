package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares15 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(4, 0).withValue('2'))
      .add(Cell.at(1, 1).withValue('5'))
      .add(Cell.at(0, 3).withValue('3'))
      .add(Cell.at(2, 3).withValue('5'))
      .add(Cell.at(5, 3).withValue('4'))
      .add(Cell.at(5, 4).withValue('3'))
      .add(Cell.at(6, 4).withValue(Arrow.NORTHWEST.symbol()))
      .add(Cell.at(5, 5).withValue('1'))
      .add(Cell.at(3, 6).withValue('1'))
      .add(Cell.at(6, 6));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(3, 1);
    minesInColumn(2, 6);
    minesInRow(5, 1);
    minesInRow(3, 2);
    minesInRow(4, 4);
    minesInRow(1, 5);
    minesInRow(2, 6);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(4, 1), Pos.at(3, 2), Pos.at(4, 2), Pos.at(5, 2), Pos.at(6, 2)),
      Stream.of(Pos.at(1, 2), Pos.at(1, 3), Pos.at(1, 4), Pos.at(1, 5), Pos.at(2, 5), Pos.at(3, 5), Pos.at(4, 5))
    );
  }
}
