package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;

import java.util.stream.Stream;

public class Battleships5 extends Battleships {

  @Override protected Grid.Builder<Value> definePuzzle() {
    return Grid.builder(Value.class)
      .add(Cell.at(9, 9));
  }

  @Override protected void addRowAndColumnCounts() {
    shipSectionsInColumn(3, 0);
    shipSectionsInColumn(0, 1);
    shipSectionsInColumn(1, 2);
    shipSectionsInColumn(3, 3);
    shipSectionsInColumn(3, 4);
    shipSectionsInColumn(1, 5);
    shipSectionsInColumn(2, 6);
    shipSectionsInColumn(4, 7);
    shipSectionsInColumn(1, 8);
    shipSectionsInColumn(2, 9);
    shipSectionsInRow(3, 0);
    shipSectionsInRow(1, 1);
    shipSectionsInRow(5, 2);
    shipSectionsInRow(1, 3);
    shipSectionsInRow(1, 4);
    shipSectionsInRow(2, 5);
    shipSectionsInRow(1, 6);
    shipSectionsInRow(1, 7);
    shipSectionsInRow(4, 8);
    shipSectionsInRow(1, 9);
  }

  @Override protected void addValues() {
    solver.add(solver.varFor(cell(4, 0).withValue(Value.SHIP_SECTION)));
    solver.add(solver.varFor(cell(0, 2).withValue(Value.WATER)));
    solver.add(solver.varFor(cell(8, 7).withValue(Value.SUBMARINE)));
  }
}
