package com.lastbubble.puzzle;

import co.unruly.matchers.StreamMatchers;
import static org.junit.Assert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CharRasterTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private CharRaster raster;

  @Test public void whenSizeNotSpecified() {
    raster = CharRaster.builder().build();
    assertEmpty();
  }

  @Test public void whenHeightNotSpecified() {
    raster = CharRaster.builder().ofWidth(3).build();
    assertEmpty();
  }

  @Test public void whenInvalidHeightSpecified() {
    raster = CharRaster.builder().ofWidth(3).ofHeight(-1).build();
    assertEmpty();
  }

  @Test public void whenWidthNotSpecified() {
    raster = CharRaster.builder().ofHeight(2).build();
    assertEmpty();
  }

  @Test public void whenInvalidWidthSpecified() {
    raster = CharRaster.builder().ofWidth(-1).ofHeight(2).build();
    assertEmpty();
  }

  private void assertEmpty() {
    assertThat(raster.lines(), StreamMatchers.empty());
  }

  @Test public void whenEmpty() {
    raster = CharRaster.builder().ofWidth(3).ofHeight(2).build();
    assertLinesAre("   ", "   ");
  }

  @Test public void withValues() {
    raster = CharRaster.builder().ofWidth(3).ofHeight(2).build();
    raster.set(Pos.at(2,0), 'a');
    raster.set(Pos.at(0,1), 'b');
    assertLinesAre("  a", "b  ");
  }

  @Test public void overwriteValues() {
    raster = CharRaster.builder().ofWidth(3).ofHeight(2).build();
    raster.set(Pos.at(2,0), 'a');
    raster.set(Pos.at(0,1), 'b');
    raster.set(Pos.at(2,0), 'c');
    raster.set(Pos.at(0,1), 'd');
    assertLinesAre("  c", "d  ");
  }

  private void assertLinesAre(String... lines) {
    assertThat(raster.lines(), StreamMatchers.contains(lines));
  }

  @Test public void setValueForInvalidPosition() {
    raster = CharRaster.builder().ofWidth(3).ofHeight(2).build();
    thrown.expect(ArrayIndexOutOfBoundsException.class);
    raster.set(Pos.at(3,2), 'a');
  }
}
