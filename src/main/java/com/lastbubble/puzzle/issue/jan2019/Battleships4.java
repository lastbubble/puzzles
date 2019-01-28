package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;

import java.util.stream.Stream;

public class Battleships4 extends Battleships {

  @Override protected Grid.Builder<Value> definePuzzle() {
    return Grid.builder(Value.class)
      .add(Cell.at(9, 9));
  }

  @Override protected void addRowAndColumnCounts() {
    shipSectionsInColumn(3, 0);
    shipSectionsInColumn(0, 1);
    shipSectionsInColumn(3, 2);
    shipSectionsInColumn(0, 3);
    shipSectionsInColumn(3, 4);
    shipSectionsInColumn(2, 5);
    shipSectionsInColumn(2, 6);
    shipSectionsInColumn(1, 7);
    shipSectionsInColumn(2, 8);
    shipSectionsInColumn(4, 9);
    shipSectionsInRow(0, 0);
    shipSectionsInRow(2, 1);
    shipSectionsInRow(1, 2);
    shipSectionsInRow(3, 3);
    shipSectionsInRow(3, 4);
    shipSectionsInRow(2, 5);
    shipSectionsInRow(1, 6);
    shipSectionsInRow(1, 7);
    shipSectionsInRow(2, 8);
    shipSectionsInRow(5, 9);
  }

  @Override protected void addValues() {
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(8, 2).withValue(v))
        .map(solver::varFor)
    );
    solver.add(solver.varFor(cell(4, 5).withValue(Value.SUBMARINE)));
    solver.add(solver.varFor(cell(0, 3).withValue(Value.WATER)));
  }
}
