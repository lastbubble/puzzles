package com.lastbubble.puzzle.issue.feb2019;

import static com.lastbubble.puzzle.logic.Formula.or;

import com.lastbubble.puzzle.Battleships;
import com.lastbubble.puzzle.Grid;

import java.util.stream.IntStream;

public class Battleships5 extends Battleships {

  @Override protected IntStream rowCounts()    { return IntStream.of(1, 2, 1, 1, 1, 4, 2, 3, 1, 4); }

  @Override protected IntStream columnCounts() { return IntStream.of(2, 3, 1, 3, 3, 4, 1, 2, 0, 1); }

  @Override protected void addValues() {
    addShipSectionAt(1, 4);
    addShipSectionAt(4, 7);
    addShipStartAt(1, 3);
    addWaterAt(5, 1);
    solver.add(
      or(
        solver.varFor(cell(4, 8).withValue(Value.SHIP_SECTION)),
        solver.varFor(cell(5, 7).withValue(Value.SHIP_SECTION))
      )
    );
  }

  @Override protected boolean isValid(Grid<Value> grid) {
    return isFilled(grid, 1, 5);
  }
}
