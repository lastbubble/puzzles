package com.lastbubble.puzzle.issue.oct2017;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.toList;

import com.lastbubble.puzzle.Grid;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class OddLetterOut2 extends OddLetterOut {

  @Override protected void addConstraints() {

    Map<Character, List<Character>> adjacentLetters = new HashMap<>();
    adjacentLetters.put('B', Arrays.asList('L', 'M', 'U'));
    adjacentLetters.put('C', Arrays.asList('O'));
    adjacentLetters.put('D', Arrays.asList('R'));
    adjacentLetters.put('E', Arrays.asList('G', 'K', 'L', 'N', 'R','Y'));
    adjacentLetters.put('G', Arrays.asList('E', 'N'));
    adjacentLetters.put('H', Arrays.asList('O'));
    adjacentLetters.put('I', Arrays.asList('N', 'T'));
    adjacentLetters.put('K', Arrays.asList('E', 'R'));
    adjacentLetters.put('L', Arrays.asList('B', 'E', 'O'));
    adjacentLetters.put('M', Arrays.asList('B', 'U'));
    adjacentLetters.put('N', Arrays.asList('E', 'G', 'I', 'O', 'Y'));
    adjacentLetters.put('O', Arrays.asList('C', 'H', 'L', 'N', 'R', 'W'));
    adjacentLetters.put('R', Arrays.asList('D', 'E', 'K', 'O'));
    adjacentLetters.put('S', Arrays.asList('T'));
    adjacentLetters.put('T', Arrays.asList('I', 'S'));
    adjacentLetters.put('U', Arrays.asList('B', 'M'));
    adjacentLetters.put('W', Arrays.asList('O'));
    adjacentLetters.put('Y', Arrays.asList('E', 'N'));

    Stream.of('B', 'C', 'D', 'H', 'J', 'K', 'P', 'S', 'V', 'W').forEach(c ->
      solver.addExactly(1, posStream().map(p -> cell(p, c)).map(solver::varFor))
    );

    alphabetStream().forEach(
      c -> {
        solver.addAtMost(1, posStream().map(p -> cell(p, c)).map(solver::varFor));

        List<Character> adjacent = adjacentLetters.get(c);
        if (adjacent != null) {
          adjacent.stream().forEach(a ->
            posStream().forEach(p ->
              solver.add(
                implies(
                  solver.varFor(cell(p, c)),
                  anyOf(neighborsOf(p).map(n -> cell(n, a)).map(solver::varFor))
                )
              )
            )
          );
        }
      }
    );

    posStream().forEach(p ->
      solver.addExactly(1, alphabetStream().map(c -> cell(p, c)).map(solver::varFor))
    );

    solver.add(solver.varFor(cell(3, 1, 'T')));

    solver.add(solver.varFor(cell(2, 4, 'G')));

    posStream().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'C')),
          not(anyOf(neighborsOf(p).map(n -> cell(n, 'V')).map(solver::varFor)))
        )
      )
    );

    posStream().forEach(p -> {
      solver.add(implies(
          solver.varFor(cell(p, 'F')),
          anyOf(IntStream.range(0, 5).mapToObj(n -> cell(n, p.y(), 'M')).map(solver::varFor))
        )
      );
      solver.add(implies(
          solver.varFor(cell(p, 'M')),
          anyOf(IntStream.range(0, 5).mapToObj(n -> cell(n, p.y(), 'F')).map(solver::varFor))
        )
      );
    });

    posStream().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'P')),
          anyOf(
            IntStream.range(0, 5).filter(x -> Math.abs(p.x() - x) > 2).mapToObj(x -> cell(x, p.y(), 'W')).map(solver::varFor)
          )
        )
      )
    );

    posStream().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'K')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'I')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'L')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'O')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(x -> cell(x, p.y(), 'S')).map(solver::varFor)
            )
          )
        )
      )
    );

    posStream().forEach(p ->
      solver.add(implies(
          solver.varFor(cell(p, 'J')),
          allOf(
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'U')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'M')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'P')).map(solver::varFor)
            ),
            anyOf(
              IntStream.range(0, 5).mapToObj(y -> cell(p.x(), y, 'S')).map(solver::varFor)
            )
          )
        )
      )
    );
  }

  @Override protected boolean isValid(Grid<Character> grid) {

    List<Character> firstRow = IntStream.range(0, 5).mapToObj(x -> grid.valueAt(x, 0).get()).collect(toList());
    boolean firstRowInOrder =firstRow.equals(firstRow.stream().sorted(Collections.reverseOrder()).collect(toList()));

    return firstRowInOrder;
  }
}
