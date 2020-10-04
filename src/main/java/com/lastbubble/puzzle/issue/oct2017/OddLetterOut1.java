package com.lastbubble.puzzle.issue.oct2017;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.toList;

import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.Mover;
import com.lastbubble.puzzle.common.Pos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OddLetterOut1 extends OddLetterOut {

  @Override protected void addConstraints() {

    Map<Character, List<Character>> adjacentLetters = new HashMap<>();
    adjacentLetters.put('A', Arrays.asList('Y'));
    adjacentLetters.put('B', Arrays.asList('L'));
    adjacentLetters.put('C', Arrays.asList('H', 'K'));
    adjacentLetters.put('E', Arrays.asList('J', 'N', 'Y'));
    adjacentLetters.put('H', Arrays.asList('I'));
    adjacentLetters.put('I', Arrays.asList('C'));
    adjacentLetters.put('J', Arrays.asList('A'));
    adjacentLetters.put('K', Arrays.asList('E'));
    adjacentLetters.put('L', Arrays.asList('T', 'U'));
    adjacentLetters.put('O', Arrays.asList('S', 'W'));
    adjacentLetters.put('R', Arrays.asList('E', 'I', 'K'));
    adjacentLetters.put('S', Arrays.asList('T'));
    adjacentLetters.put('T', Arrays.asList('R', 'U'));
    adjacentLetters.put('U', Arrays.asList('E', 'L', 'R'));
    adjacentLetters.put('V', Arrays.asList('U'));
    adjacentLetters.put('W', Arrays.asList('L'));

    Stream.of('B', 'C', 'O', 'T', 'V', 'Z').forEach(c ->
      solver.addExactly(1, blankGrid.positions().map(p -> cell(p, c)).map(solver::varFor))
    );

    Mover move = blankGrid.mover();

    allValues().forEach(
      c -> {
        solver.addAtMost(1, blankGrid.positions().map(p -> cell(p, c)).map(solver::varFor));

        List<Character> adjacent = adjacentLetters.get(c);
        if (adjacent != null) {
          adjacent.stream().forEach(a ->
            blankGrid.positions().forEach(p ->
              solver.add(
                implies(
                  solver.varFor(cell(p, c)),
                  anyOf(move.neighborsOf(p).map(n -> cell(n, a)).map(solver::varFor))
                )
              )
            )
          );
        }
      }
    );

    blankGrid.positions().forEach(p ->
      solver.addExactly(1, allValues().map(c -> cell(p, c)).map(solver::varFor))
    );

    solver.addExactly(1, Stream.of(cell(0, 0, 'M'), cell(1, 0, 'M'), cell(2, 0, 'M')).map(solver::varFor));
    IntStream.range(0, 3).forEach(x ->
      solver.add(implies(
          solver.varFor(cell(x, 0, 'M')),
          and(
            solver.varFor(cell(x + 1, 0, 'O')),
            solver.varFor(cell(x + 2, 0, 'W'))
          )
        )
      )
    );

    solver.addExactly(1, Stream.of(cell(0, 4, 'K'), cell(1, 4, 'K'), cell(2, 4, 'K')).map(solver::varFor));
    IntStream.range(0, 3).forEach(x ->
      solver.add(implies(
          solver.varFor(cell(x, 4, 'K')),
          and(
            solver.varFor(cell(x + 1, 4, 'I')),
            solver.varFor(cell(x + 2, 4, 'P'))
          )
        )
      )
    );

    solver.add(solver.varFor(cell(3, 1, 'L')));

    solver.add(solver.varFor(cell(0, 3, 'J')));

    blankGrid.positions().filter(p -> p.y() < 4).forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'B')),
          solver.varFor(cell(p.x(), p.y() + 1, 'Q'))
        )
      )
    );

    blankGrid.positions().forEach(p -> {
      solver.add(implies(
          solver.varFor(cell(p, 'X')),
          anyOf(IntStream.range(0, 5).mapToObj(n -> cell(n, p.y(), 'F')).map(solver::varFor))
        )
      );
      solver.add(implies(
          solver.varFor(cell(p, 'F')),
          anyOf(IntStream.range(0, 5).mapToObj(n -> cell(n, p.y(), 'X')).map(solver::varFor))
        )
      );
    });

    IntStream.range(0, 3).forEach(n ->
      solver.add(implies(
          solver.varFor(cell(n, 0, 'M')),
          and(
            anyOf(IntStream.range(1, 5).mapToObj(x -> cell(n, x, 'A')).map(solver::varFor)),
            anyOf(IntStream.range(1, 5).mapToObj(x -> cell(n, x, 'D')).map(solver::varFor))
          )
        )
      )
    );
  }

  @Override protected boolean isValid(Grid<Character> grid) {

    Optional<Pos> fPos = findValueIn(grid, 'F');
    Optional<Pos> xPos = findValueIn(grid, 'X');
    Optional<Pos> zPos = findValueIn(grid, 'Z');

    boolean zInDifferentColumnFromFAndX = (
      fPos.isPresent() && xPos.isPresent() && zPos.isPresent() &&
      fPos.get().x() != zPos.get().x() &&
      xPos.get().x() != zPos.get().x()
    );

    List<Character> clockwiseCorners = clockwiseCornersOf(grid);
    List<Character> counterClockwiseCorners = counterClockwiseCornersOf(grid);
    boolean cornersInOrder = (
      clockwiseCorners.equals(clockwiseCorners.stream().sorted().collect(toList())) ||
      counterClockwiseCorners.equals(counterClockwiseCorners.stream().sorted().collect(toList()))
    );

    return zInDifferentColumnFromFAndX && cornersInOrder;
  }

  private List<Character> clockwiseCornersOf(Grid<Character> grid) {
    return Stream.of(
      grid.valueAt(0, 0).get(),
      grid.valueAt(4, 0).get(),
      grid.valueAt(4, 4).get(),
      grid.valueAt(0, 4).get()).collect(toList());
  }

  private List<Character> counterClockwiseCornersOf(Grid<Character> grid) {
    return Stream.of(
      grid.valueAt(0, 0).get(),
      grid.valueAt(0, 4).get(),
      grid.valueAt(4, 4).get(),
      grid.valueAt(4, 0).get()).collect(toList());
  }
}
