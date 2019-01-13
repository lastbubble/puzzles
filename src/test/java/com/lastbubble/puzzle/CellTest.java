package com.lastbubble.puzzle;

import static com.lastbubble.puzzle.RandomNumbers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import co.unruly.matchers.OptionalMatchers;
import org.junit.Test;

public class CellTest {

  private final Pos pos = Pos.at(naturalNumber(), naturalNumber());

  private Cell<String> cell;

  @Test public void assignPosWithoutValue() {
    cell = Cell.at(pos);

    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.empty());
  }

  @Test public void assignPosWithXAndY() {
    cell = Cell.at(pos.x(), pos.y());

    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.empty());
  }

  @Test public void assignNullValue() {
    Cell<String> cellWithoutValue = Cell.at(pos);
    cell = cellWithoutValue.withValue(null);

    assertThat(cell, not(sameInstance(cellWithoutValue)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.empty());
  }

  @Test public void assignValue() {
    Cell<String> cellWithoutValue = Cell.at(pos);
    String value = "value";
    cell = cellWithoutValue.withValue(value);

    assertThat(cell, not(sameInstance(cellWithoutValue)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.contains(value));
  }

  @Test public void assignValueOfDifferentType() {
    Cell<Integer> cellWithInt = Cell.at(pos).withValue(1);
    String value = "value";
    cell = cellWithInt.withValue(value);

    assertThat(cell, not(sameInstance(cellWithInt)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.contains(value));
  }

  @Test public void reassignValue() {
    String value1 = "value1";
    Cell<String> cellWithValue1 = Cell.at(pos).withValue(value1);
    String value2 = "value2";
    cell = cellWithValue1.withValue(value2);

    assertThat(cell, not(sameInstance(cellWithValue1)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.contains(value2));
  }
}
