package com.lastbubble.puzzle;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

public class GridPrinterTest {

  private final GridPrinter<Character> printer = new GridPrinter<Character>(x -> x);

  private Grid<Character> grid;

  @Test public void emptyGrid() {
    grid = Grid.builder(Character.class)
      .add(Cell.at(2, 2))
      .build();

    assertThatLines(
      "┌─┬─┬─┐",
      "│ │ │ │",
      "├─┼─┼─┤",
      "│ │ │ │",
      "├─┼─┼─┤",
      "│ │ │ │",
      "└─┴─┴─┘"
    );
  }

  @Test public void gridWithValues() {
    grid = Grid.builder(Character.class)
      .add(Cell.at(0, 0).withValue('A'))
      .add(Cell.at(2, 0).withValue('B'))
      .add(Cell.at(1, 1).withValue('C'))
      .add(Cell.at(0, 2).withValue('D'))
      .add(Cell.at(2, 2).withValue('E'))
      .build();

    assertThatLines(
      "┌─┬─┬─┐",
      "│A│ │B│",
      "├─┼─┼─┤",
      "│ │C│ │",
      "├─┼─┼─┤",
      "│D│ │E│",
      "└─┴─┴─┘"
    );
  }

  private void assertThatLines(String... lines) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    PrintWriter writer = new PrintWriter(out, true);
    printer.printTo(writer, grid);
    writer.close();

    assertThat(out.toString().split("\n"), arrayContaining(lines));
  }
}
