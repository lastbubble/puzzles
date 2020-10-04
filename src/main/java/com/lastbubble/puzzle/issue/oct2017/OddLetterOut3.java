package com.lastbubble.puzzle.issue.oct2017;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.joining;

import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.Mover;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OddLetterOut3 extends OddLetterOut {

  @Override protected void addConstraints() {

    Stream.of('A', 'B', 'C', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z').forEach(c ->
      solver.addExactly(1, blankGrid.positions().map(p -> cell(p, c)).map(solver::varFor))
    );

    Mover move = blankGrid.mover();

    allValues().forEach(
      c -> {
        solver.addAtMost(1, blankGrid.positions().map(p -> cell(p, c)).map(solver::varFor));

        if (c != 'Z') {
          blankGrid.positions().forEach(p ->
            solver.add(
              implies(
                solver.varFor(cell(p, c)),
                not(anyOf(move.neighborsOf(p).map(n -> cell(n, (char) (c + 1))).map(solver::varFor)))
              )
            )
          );
        }

        if (c != 'A') {
          blankGrid.positions().forEach(p ->
            solver.add(
              implies(
                solver.varFor(cell(p, c)),
                not(anyOf(move.neighborsOf(p).map(n -> cell(n, (char) (c - 1))).map(solver::varFor)))
              )
            )
          );
        }
      }
    );

    blankGrid.positions().forEach(p ->
      solver.addExactly(1, allValues().map(c -> cell(p, c)).map(solver::varFor))
    );

    solver.add(solver.varFor(cell(1, 4, 'V')));

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'G')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'L')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'Y')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'P')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'H')).map(solver::varFor)
            )
          )
        )
      )
    );

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'M')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'O')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'A')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'B')).map(solver::varFor)
            )
          )
        )
      )
    );

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'T')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'R')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'E')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'K')).map(solver::varFor)
            )
          )
        )
      )
    );

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'C')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'U')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'R')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'B')).map(solver::varFor)
            )
          )
        )
      )
    );

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'F')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'L')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'I')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'X')).map(solver::varFor)
            )
          )
        )
      )
    );

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'J')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'O')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'K')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'Y')).map(solver::varFor)
            )
          )
        )
      )
    );

    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'Z')),
          not(anyOf(IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'F')).map(solver::varFor)))
        )
      )
    );
    blankGrid.positions().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'Z')),
          not(anyOf(IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'F')).map(solver::varFor)))
        )
      )
    );

    Stream.of('P', 'E', 'A', 'N', 'U', 'T', 'S').forEach(c ->
      solver.addExactly(1,
        blankGrid.positions()
          .filter(p -> p.x() == 0 || p.x() == 4 || p.y() == 0 || p.y() == 4)
          .map(p -> cell(p, c))
          .map(solver::varFor)
      )
    );
  }

  @Override protected boolean isValid(Grid<Character> grid) {

    String basepath = Stream.of(
        grid.valueAt(4, 4).get(),
        grid.valueAt(4, 3).get(),
        grid.valueAt(4, 2).get(),
        grid.valueAt(4, 1).get(),
        grid.valueAt(4, 0).get(),
        grid.valueAt(3, 0).get(),
        grid.valueAt(2, 0).get(),
        grid.valueAt(1, 0).get(),
        grid.valueAt(0, 0).get(),
        grid.valueAt(0, 1).get(),
        grid.valueAt(0, 2).get(),
        grid.valueAt(0, 3).get(),
        grid.valueAt(0, 4).get(),
        grid.valueAt(1, 4).get(),
        grid.valueAt(2, 4).get(),
        grid.valueAt(3, 4).get()
      ).filter(c -> "PEANUTS".indexOf(c) > -1).map(String::valueOf).collect(joining());

      String diag = Stream.of(
        grid.valueAt(0, 0).get(),
        grid.valueAt(1, 1).get(),
        grid.valueAt(2, 2).get(),
        grid.valueAt(3, 3).get(),
        grid.valueAt(4, 4).get()
      ).map(String::valueOf).collect(joining());

      return basepath.equals("PEANUTS") && diag.equals("WORLD");
  }
}
