package com.lastbubble.puzzle;

import static com.lastbubble.puzzle.RandomNumbers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import co.unruly.matchers.OptionalMatchers;
import co.unruly.matchers.StreamMatchers;

import org.junit.Test;

public class GridTest {

  private final Grid.Builder<String> builder = Grid.builder(String.class);

  private Grid<String> grid;

  @Test public void defaultGrid() {

    grid = builder.build();

    assertThat(grid.width(), is(0));
    assertThat(grid.height(), is(0));
  }

  @Test public void emptyGrid() {

    int x = positiveNumber();
    int y = positiveNumberOtherThan(x);

    grid = builder.add(Cell.at(x, y)).build();

    assertThat(grid.width(), is(x + 1));
    assertThat(grid.height(), is(y + 1));
  }

  @Test public void withValues() {
    grid = builder
      .add(Cell.at(0, 1).withValue("abc"))
      .add(Cell.at(1, 0).withValue("def"))
      .build();

    assertThat(grid.valueAt(0, 0), OptionalMatchers.empty());
    assertThat(grid.valueAt(0, 1), OptionalMatchers.contains("abc"));
    assertThat(grid.valueAt(1, 0), OptionalMatchers.contains("def"));
    assertThat(grid.valueAt(1, 1), OptionalMatchers.empty());
  }

  @Test public void valueAt_usingPos() {
    grid = builder
      .add(Cell.at(0, 1).withValue("abc"))
      .add(Cell.at(1, 0).withValue("def"))
      .build();

    assertThat(grid.valueAt(0, 0), is(grid.valueAt(Pos.at(0, 0))));
    assertThat(grid.valueAt(1, 0), is(grid.valueAt(Pos.at(1, 0))));
    assertThat(grid.valueAt(0, 1), is(grid.valueAt(Pos.at(0, 1))));
    assertThat(grid.valueAt(1, 1), is(grid.valueAt(Pos.at(1, 1))));
  }

  @Test public void positions_whenSquare() {
    grid = builder.add(Cell.at(2, 2)).build();

    assertThat(grid.positions(), StreamMatchers.contains(
        Pos.at(0, 0), Pos.at(1, 0), Pos.at(2, 0),
        Pos.at(0, 1), Pos.at(1, 1), Pos.at(2, 1),
        Pos.at(0, 2), Pos.at(1, 2), Pos.at(2, 2)
      )
    );
  }

  @Test public void positions_whenRectangular() {
    grid = builder.add(Cell.at(2, 1)).build();

    assertThat(grid.positions(), StreamMatchers.contains(
        Pos.at(0, 0), Pos.at(1, 0), Pos.at(2, 0),
        Pos.at(0, 1), Pos.at(1, 1), Pos.at(2, 1)
      )
    );
  }

  @Test public void neighborsOf() {
    grid = builder.add(Cell.at(2, 2)).build();

    assertThat(grid.neighborsOf(Pos.at(0, 0)), StreamMatchers.contains(
        Pos.at(1, 0), Pos.at(1, 1), Pos.at(0, 1)
      )
    );

    assertThat(grid.neighborsOf(Pos.at(1, 0)), StreamMatchers.contains(
        Pos.at(2, 0), Pos.at(2, 1), Pos.at(1, 1), Pos.at(0, 1), Pos.at(0, 0)
      )
    );

    assertThat(grid.neighborsOf(Pos.at(2, 0)), StreamMatchers.contains(
        Pos.at(2, 1), Pos.at(1, 1), Pos.at(1, 0)
      )
    );

    assertThat(grid.neighborsOf(Pos.at(1, 1)), StreamMatchers.contains(
        Pos.at(0, 0), Pos.at(1, 0), Pos.at(2, 0), Pos.at(2, 1),
        Pos.at(2, 2), Pos.at(1, 2), Pos.at(0, 2), Pos.at(0, 1)
      )
    );

    assertThat(grid.neighborsOf(Pos.at(0, 2)), StreamMatchers.contains(
        Pos.at(0, 1), Pos.at(1, 1), Pos.at(1, 2)
      )
    );

    assertThat(grid.neighborsOf(Pos.at(1, 2)), StreamMatchers.contains(
        Pos.at(0, 1), Pos.at(1, 1), Pos.at(2, 1), Pos.at(2, 2), Pos.at(0, 2)
      )
    );

    assertThat(grid.neighborsOf(Pos.at(2, 2)), StreamMatchers.contains(
        Pos.at(1, 1), Pos.at(2, 1), Pos.at(1, 2)
      )
    );
  }

  @Test public void filledCells() {
    grid = builder
      .add(Cell.at(0, 0).withValue("a"))
      .add(Cell.at(2, 0).withValue("b"))
      .add(Cell.at(1, 1).withValue("c"))
      .add(Cell.at(0, 2).withValue("d"))
      .add(Cell.at(2, 2).withValue("e"))
      .build();

    assertThat(grid.filledCells(), StreamMatchers.contains(
        Cell.at(0, 0).withValue("a"),
        Cell.at(2, 0).withValue("b"),
        Cell.at(1, 1).withValue("c"),
        Cell.at(0, 2).withValue("d"),
        Cell.at(2, 2).withValue("e")
      )
    );
  }
}
