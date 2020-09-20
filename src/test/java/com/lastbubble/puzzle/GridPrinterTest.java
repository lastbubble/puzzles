package com.lastbubble.puzzle;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;

import org.junit.Test;

public class GridPrinterTest {

  private final GridPrinter<Character> printer = new GridPrinter<Character>(x -> x);

  private Grid<Character> grid;
  private BiPredicate<Pos, Pos> sameRegion = (x, y) -> true;

  @Test public void emptyGrid() {
    grid = Grid.builder(Character.class)
      .add(Cell.at(2, 2))
      .build();

    assertThatLines(
      "┏━┯━┯━┓",
      "┃ │ │ ┃",
      "┠─┼─┼─┨",
      "┃ │ │ ┃",
      "┠─┼─┼─┨",
      "┃ │ │ ┃",
      "┗━┷━┷━┛"
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
      "┏━┯━┯━┓",
      "┃A│ │B┃",
      "┠─┼─┼─┨",
      "┃ │C│ ┃",
      "┠─┼─┼─┨",
      "┃D│ │E┃",
      "┗━┷━┷━┛"
    );
  }

  @Test public void gridWithRegions() {
    Map<Pos, Character> regionAtPos = new HashMap<>();
    regionAtPos.put(Pos.at(0,0), 'A');
    regionAtPos.put(Pos.at(1,0), 'A');
    regionAtPos.put(Pos.at(2,0), 'A');
    regionAtPos.put(Pos.at(3,0), 'A');
    regionAtPos.put(Pos.at(4,0), 'A');
    regionAtPos.put(Pos.at(0,1), 'A');
    regionAtPos.put(Pos.at(1,1), 'G');
    regionAtPos.put(Pos.at(2,1), 'F');
    regionAtPos.put(Pos.at(3,1), 'E');
    regionAtPos.put(Pos.at(4,1), 'A');
    regionAtPos.put(Pos.at(0,2), 'A');
    regionAtPos.put(Pos.at(1,2), 'C');
    regionAtPos.put(Pos.at(2,2), 'B');
    regionAtPos.put(Pos.at(3,2), 'D');
    regionAtPos.put(Pos.at(4,2), 'A');
    regionAtPos.put(Pos.at(0,3), 'A');
    regionAtPos.put(Pos.at(1,3), 'C');
    regionAtPos.put(Pos.at(2,3), 'B');
    regionAtPos.put(Pos.at(3,3), 'B');
    regionAtPos.put(Pos.at(4,3), 'A');
    regionAtPos.put(Pos.at(0,4), 'A');
    regionAtPos.put(Pos.at(1,4), 'A');
    regionAtPos.put(Pos.at(2,4), 'A');
    regionAtPos.put(Pos.at(3,4), 'A');
    regionAtPos.put(Pos.at(4,4), 'A');

    Grid.Builder<Character> gridBuilder = Grid.builder(Character.class);
    for (Pos pos : regionAtPos.keySet()) {
      gridBuilder.add(Cell.at(pos).withValue(regionAtPos.get(pos)));
    }
    grid = gridBuilder.build();

    sameRegion = (x, y) -> regionAtPos.get(x) == regionAtPos.get(y);

    assertThatLines(
      "┏━┯━┯━┯━┯━┓",
      "┃A│A│A│A│A┃",
      "┠─╆━╈━╈━╅─┨",
      "┃A┃G┃F┃E┃A┃",
      "┠─╊━╋━╋━╉─┨",
      "┃A┃C┃B┃D┃A┃",
      "┠─╂─╂─╄━╉─┨",
      "┃A┃C┃B│B┃A┃",
      "┠─╄━╇━┿━╃─┨",
      "┃A│A│A│A│A┃",
      "┗━┷━┷━┷━┷━┛"
    );
  }

  private void assertThatLines(String... lines) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    PrintWriter writer = new PrintWriter(out, true);
    printer.printTo(writer, grid, sameRegion);
    writer.close();

    assertThat(out.toString().split("\n"), arrayContaining(lines));
  }
}
