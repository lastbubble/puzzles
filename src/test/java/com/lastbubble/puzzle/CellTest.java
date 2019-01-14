package com.lastbubble.puzzle;

import static com.lastbubble.puzzle.RandomNumbers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import co.unruly.matchers.OptionalMatchers;
import org.junit.Test;

public class CellTest {

  private final Pos pos = Pos.at(naturalNumber(), naturalNumber());
  private final String value = "value";

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
    cell = cellWithoutValue.withValue(value);

    assertThat(cell, not(sameInstance(cellWithoutValue)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.contains(value));
  }

  @Test public void assignValueOfDifferentType() {
    Cell<Integer> cellWithInt = Cell.at(pos).withValue(1);
    cell = cellWithInt.withValue(value);

    assertThat(cell, not(sameInstance(cellWithInt)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.contains(value));
  }

  @Test public void reassignValue() {
    Cell<String> cellWithValue1 = Cell.at(pos).withValue(value);
    String otherValue = "otherValue";
    cell = cellWithValue1.withValue(otherValue);

    assertThat(cell, not(sameInstance(cellWithValue1)));
    assertThat(cell.pos(), is(pos));
    assertThat(cell.value(), OptionalMatchers.contains(otherValue));
  }

  @Test public void hashCodeImplemented() {
    cell = Cell.at(pos).withValue(value);

    assertThat(cell.hashCode(), is(Cell.at(pos).withValue(value).hashCode()));
  }

  @Test public void equalsImplemented() {
    cell = Cell.at(pos);

    assertThat(cell, equalTo(cell));
    assertThat(cell, not(equalTo( new Object())));
    assertThat(cell, not(equalTo(Cell.at(pos.x(), naturalNumberOtherThan(pos.y())))));
    assertThat(cell, not(equalTo(Cell.at(naturalNumberOtherThan(pos.x()), pos.y()))));
    assertThat(cell, not(equalTo(Cell.at(pos).withValue(value))));
    assertThat(cell, equalTo(Cell.at(pos)));
    assertThat(cell.withValue(value), equalTo(Cell.at(pos).withValue(value)));
    assertThat(cell.withValue(value), not(equalTo(Cell.at(pos).withValue(23))));
  }

  @Test public void toStringImplemented() {
    cell = Cell.at(pos);

    assertThat(cell.toString(), is(String.format("(%d,%d)", pos.x(), pos.y())));
    assertThat(cell.withValue(value).toString(), is(String.format("(%d,%d)=%s", pos.x(), pos.y(), value)));
  }
}
