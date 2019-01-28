package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;

import java.util.stream.Stream;

public class Battleships1 extends Battleships {

  @Override protected Grid.Builder<Value> definePuzzle() {
    return Grid.builder(Value.class)
      .add(Cell.at(9, 9));
  }

  @Override protected void addRowAndColumnCounts() {
    shipSectionsInColumn(6, 0);
    shipSectionsInColumn(0, 1);
    shipSectionsInColumn(4, 2);
    shipSectionsInColumn(1, 3);
    shipSectionsInColumn(3, 4);
    shipSectionsInColumn(1, 5);
    shipSectionsInColumn(2, 6);
    shipSectionsInColumn(0, 7);
    shipSectionsInColumn(3, 8);
    shipSectionsInColumn(0, 9);
    shipSectionsInRow(2, 0);
    shipSectionsInRow(1, 1);
    shipSectionsInRow(2, 2);
    shipSectionsInRow(1, 3);
    shipSectionsInRow(1, 4);
    shipSectionsInRow(0, 5);
    shipSectionsInRow(5, 6);
    shipSectionsInRow(2, 7);
    shipSectionsInRow(5, 8);
    shipSectionsInRow(1, 9);
  }

  @Override protected void addValues() {
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(2, 0).withValue(v))
        .map(solver::varFor)
    );
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(2, 8).withValue(v))
        .map(solver::varFor)
    );
    solver.add(solver.varFor(cell(0, 6).withValue(Value.WATER)));
  }
}
