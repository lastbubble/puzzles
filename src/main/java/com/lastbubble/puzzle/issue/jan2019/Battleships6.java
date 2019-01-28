package com.lastbubble.puzzle.issue.jan2019;

import com.lastbubble.puzzle.Cell;
import com.lastbubble.puzzle.Grid;

import java.util.stream.Stream;

public class Battleships6 extends Battleships {

  @Override protected Grid.Builder<Value> definePuzzle() {
    return Grid.builder(Value.class)
      .add(Cell.at(9, 9));
  }

  @Override protected void addRowAndColumnCounts() {
    shipSectionsInColumn(0, 0);
    shipSectionsInColumn(4, 1);
    shipSectionsInColumn(0, 2);
    shipSectionsInColumn(4, 3);
    shipSectionsInColumn(0, 4);
    shipSectionsInColumn(2, 5);
    shipSectionsInColumn(4, 6);
    shipSectionsInColumn(1, 7);
    shipSectionsInColumn(4, 8);
    shipSectionsInColumn(1, 9);
    shipSectionsInRow(0, 0);
    shipSectionsInRow(2, 1);
    shipSectionsInRow(2, 2);
    shipSectionsInRow(1, 3);
    shipSectionsInRow(1, 4);
    shipSectionsInRow(3, 5);
    shipSectionsInRow(2, 6);
    shipSectionsInRow(3, 7);
    shipSectionsInRow(4, 8);
    shipSectionsInRow(2, 9);
  }

  @Override protected void addValues() {
    solver.addExactly(1,
      Stream.of(Value.DESTROYER_START, Value.CRUISER_START, Value.BATTLESHIP_START)
        .map(v -> cell(5, 5).withValue(v))
        .map(solver::varFor)
    );
    solver.add(solver.varFor(cell(7, 8).withValue(Value.SUBMARINE)));
    solver.add(solver.varFor(cell(3, 4).withValue(Value.WATER)));
  }
}
