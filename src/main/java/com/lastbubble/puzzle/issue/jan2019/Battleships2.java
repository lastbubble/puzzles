package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;

import java.util.stream.Stream;

public class Battleships2 extends Battleships {

  @Override protected Grid.Builder<Value> definePuzzle() {
    return Grid.builder(Value.class)
      .add(Cell.at(9, 9));
  }

  @Override protected void addRowAndColumnCounts() {
    shipSectionsInColumn(1, 0);
    shipSectionsInColumn(2, 1);
    shipSectionsInColumn(1, 2);
    shipSectionsInColumn(2, 3);
    shipSectionsInColumn(5, 4);
    shipSectionsInColumn(2, 5);
    shipSectionsInColumn(3, 6);
    shipSectionsInColumn(1, 7);
    shipSectionsInColumn(2, 8);
    shipSectionsInColumn(1, 9);
    shipSectionsInRow(1, 0);
    shipSectionsInRow(3, 1);
    shipSectionsInRow(2, 2);
    shipSectionsInRow(1, 3);
    shipSectionsInRow(4, 4);
    shipSectionsInRow(0, 5);
    shipSectionsInRow(1, 6);
    shipSectionsInRow(4, 7);
    shipSectionsInRow(1, 8);
    shipSectionsInRow(3, 9);
  }

  @Override protected void addValues() {
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(3, 1).withValue(v))
        .map(solver::varFor)
    );
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(1, 2).withValue(v))
        .map(solver::varFor)
    );
    solver.add(solver.varFor(cell(9, 0).withValue(Value.SUBMARINE)));
  }
}
