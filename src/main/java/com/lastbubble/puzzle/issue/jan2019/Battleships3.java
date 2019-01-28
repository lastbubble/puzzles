package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;

import java.util.stream.Stream;

public class Battleships3 extends Battleships {

  @Override protected Grid.Builder<Value> definePuzzle() {
    return Grid.builder(Value.class)
      .add(Cell.at(9, 9));
  }

  @Override protected void addRowAndColumnCounts() {
    shipSectionsInColumn(4, 0);
    shipSectionsInColumn(4, 1);
    shipSectionsInColumn(0, 2);
    shipSectionsInColumn(2, 3);
    shipSectionsInColumn(0, 4);
    shipSectionsInColumn(1, 5);
    shipSectionsInColumn(2, 6);
    shipSectionsInColumn(0, 7);
    shipSectionsInColumn(0, 8);
    shipSectionsInColumn(7, 9);
    shipSectionsInRow(2, 0);
    shipSectionsInRow(3, 1);
    shipSectionsInRow(1, 2);
    shipSectionsInRow(3, 3);
    shipSectionsInRow(1, 4);
    shipSectionsInRow(1, 5);
    shipSectionsInRow(3, 6);
    shipSectionsInRow(2, 7);
    shipSectionsInRow(2, 8);
    shipSectionsInRow(2, 9);
  }

  @Override protected void addValues() {
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(6, 0).withValue(v))
        .map(solver::varFor)
    );
    solver.add(solver.varFor(cell(0, 5).withValue(Value.SUBMARINE)));
    solver.add(solver.varFor(cell(9, 4).withValue(Value.WATER)));
  }
}
