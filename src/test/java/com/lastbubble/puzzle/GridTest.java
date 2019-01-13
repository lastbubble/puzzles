package com.lastbubble.puzzle;

import static com.lastbubble.puzzle.RandomNumbers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import co.unruly.matchers.OptionalMatchers;
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
}
