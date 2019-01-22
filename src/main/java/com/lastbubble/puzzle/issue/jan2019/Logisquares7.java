package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares7 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(4, 0).withValue('1'))
      .add(Cell.at(1, 1).withValue('3'))
      .add(Cell.at(4, 2).withValue('4'))
      .add(Cell.at(1, 3).withValue('5'))
      .add(Cell.at(2, 4).withValue(Arrow.WEST.symbol()))
      .add(Cell.at(4, 4).withValue('2'));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(1, 0);
    minesInColumn(3, 3);
    minesInRow(2, 1);
    minesInRow(3, 2);
    minesInRow(3, 3);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(1, 0), Pos.at(2, 0), Pos.at(2, 1))
    );
  }
}
