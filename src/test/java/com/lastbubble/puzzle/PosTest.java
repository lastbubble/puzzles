package com.lastbubble.puzzle;

import static com.lastbubble.puzzle.RandomNumbers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PosTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private Pos pos;

  @Test public void negativeXnotAllowed() {
    thrown.expect(IllegalArgumentException.class);

    Pos.at(negativeNumber(), naturalNumber());
  }

  @Test public void negativeYnotAllowed() {
    thrown.expect(IllegalArgumentException.class);

    Pos.at(naturalNumber(), negativeNumber());
  }

  @Test public void assignXandY() {
    int x = naturalNumber();
    int y = naturalNumberOtherThan(x);

    pos = Pos.at(x, y);

    assertThat(pos.x(), is(x));
    assertThat(pos.y(), is(y));
  }

  @Test public void hashCodeImplemented() {
    pos = randomPos();

    assertThat(pos.hashCode(), is(Pos.at(pos.x(), pos.y()).hashCode()));
  }

  @Test public void equalsImplemented() {
    Pos pos = randomPos();

    assertThat(pos, equalTo(pos));
    assertThat(pos, not(equalTo( new Object())));
    assertThat(pos, not(equalTo(Pos.at(pos.x(), naturalNumberOtherThan(pos.y())))));
    assertThat(pos, not(equalTo(Pos.at(naturalNumberOtherThan(pos.x()), pos.y()))));
    assertThat(pos, equalTo(Pos.at(pos.x(), pos.y())));
  }

  @Test public void toStringImplemented() {
    pos = randomPos();

    assertThat(pos.toString(), is(String.format("(%d,%d)", pos.x(), pos.y())));
  }

  private Pos randomPos() { return Pos.at(naturalNumber(), naturalNumber()); }
}
