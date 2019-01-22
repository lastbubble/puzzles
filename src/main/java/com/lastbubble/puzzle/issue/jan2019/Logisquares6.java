package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;
import com.lastbubble.puzzle.Pos;

import java.util.stream.Stream;

public class Logisquares6 extends Logisquares {

  @Override protected Grid.Builder<Character> definePuzzle() {
    return Grid.builder(Character.class)
      .add(Cell.at(0, 0).withValue(Arrow.SOUTH.symbol()))
      .add(Cell.at(2, 1).withValue('4'))
      .add(Cell.at(1, 3).withValue(Arrow.NORTHEAST.symbol()))
      .add(Cell.at(3, 3).withValue('7'))
      .add(Cell.at(4, 4));
  }

  @Override protected void addRowAndColumnCounts() {
    minesInColumn(1, 1);
    minesInColumn(4, 4);
    minesInRow(1, 0);
    minesInRow(2, 1);
    minesInRow(2, 4);
  }

  @Override protected Stream<Stream<Pos>> regions() {
    return Stream.of(
      Stream.of(Pos.at(3, 0), Pos.at(4, 0), Pos.at(3, 1), Pos.at(4, 1)),
      Stream.of(Pos.at(1, 1), Pos.at(0, 2), Pos.at(1, 2), Pos.at(0, 3), Pos.at(0, 4), Pos.at(1, 4), Pos.at(2, 4))
    );
  }
}
