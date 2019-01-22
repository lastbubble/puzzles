package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares9 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue('3'))
      .add(Cell.at(5, 0).withValue('1'))
      .add(Cell.at(2, 1).withValue(Arrow.SOUTHEAST.symbol()))
      .add(Cell.at(2, 2).withValue('4'))
      .add(Cell.at(1, 3).withValue('4'))
      .add(Cell.at(1, 5).withValue('3'))
      .add(Cell.at(3, 5).withValue(Arrow.WEST.symbol()))
      .add(Cell.at(4, 5).withValue('2'))
      .add(Cell.at(5, 5));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(2, 0);
    minesInColumn(2, 3);
    minesInColumn(2, 4);
    minesInColumn(2, 5);
    minesInRow(1, 0);
    minesInRow(4, 1);
    minesInRow(2, 2);
    minesInRow(2, 5);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(0, 2), Pos.at(1, 2), Pos.at(0, 3), Pos.at(0, 4), Pos.at(0, 5)),
      Stream.of(Pos.at(4, 1), Pos.at(5, 1), Pos.at(4, 2), Pos.at(5, 2), Pos.at(3, 3), Pos.at(4, 3))
    );
  }
}
