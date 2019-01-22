package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares4 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(2, 0).withValue(Arrow.SOUTHWEST.symbol()))
      .add(Cell.at(3, 1).withValue('2'))
      .add(Cell.at(1, 3).withValue('1'))
      .add(Cell.at(3, 3).withValue('1'));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(1, 0);
    minesInColumn(1, 1);
    minesInRow(2, 0);
    minesInRow(1, 2);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(2, 1), Pos.at(1, 2), Pos.at(2, 2), Pos.at(3, 2), Pos.at(2, 3))
    );
  }
}