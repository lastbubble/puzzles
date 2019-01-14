package com.lastbubble.puzzle.logic;

import static com.lastbubble.puzzle.logic.Formula.*;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CNF {
  public static Stream<List<AtomicFormula<?>>> convert(Formula formula) {
    List<List<AtomicFormula<?>>> clauses = new ArrayList<>();
    distributeOr(negationsIn(implicationsOut(formula))).match(
      var -> clauses.add(toClause(var)),
      not -> clauses.add(toClause(not)),
      null, // and
      null, // or
      null, // implies
      allOf -> { allOf.targets().forEach(x -> clauses.add(toClause(x))); return true; },
      anyOf -> clauses.add(toClause(anyOf))
    );
    return clauses.stream();
  }

  static Formula implicationsOut(Formula formula) {
    return formula.<Formula>match(
      var -> var,
      not -> not(implicationsOut(not.target())),
      and -> and(implicationsOut(and.left()), implicationsOut(and.right())),
      or -> or(implicationsOut(or.left()), implicationsOut(or.right())),
      implies -> or(not(implicationsOut(implies.left())), implicationsOut(implies.right())),
      allOf -> allOf(allOf.targets().map(CNF::implicationsOut)),
      anyOf -> anyOf(anyOf.targets().map(CNF::implicationsOut))
    );
  }

  static Formula negationsIn(Formula formula) {
    return formula.<Formula>match(
      var -> var,
      not -> not.target().match(
        var -> not(var),
        not2 -> negationsIn(not2.target()),
        and -> or(negationsIn(not(and.left())), negationsIn(not(and.right()))),
        or -> and(negationsIn(not(or.left())), negationsIn(not(or.right()))),
        null, // implies
        allOf -> anyOf(allOf.targets().map(Formula::not).map(CNF::negationsIn)),
        anyOf -> allOf(anyOf.targets().map(Formula::not).map(CNF::negationsIn))
      ),
      and -> and(negationsIn(and.left()), negationsIn(and.right())),
      or -> or(negationsIn(or.left()), negationsIn(or.right())),
      null, // implies
      allOf -> allOf(allOf.targets().map(CNF::negationsIn)),
      anyOf -> anyOf(anyOf.targets().map(CNF::negationsIn))
    );
  }

  static Formula distributeOr(Formula formula) {
    return formula.<Formula>match(
      var -> var,
      not -> not,
      and -> allOf(distributeOr(and.left()), distributeOr(and.right())),
      or -> {
        Formula left = distributeOr(or.left());
        Formula right = distributeOr(or.right());
        if (left instanceof AllOf) {
          return allOf(((AllOf) left).targets().map(x -> or(x, right)));
        } else if (right instanceof AllOf) {
          return allOf(((AllOf) right).targets().map(x -> or(left, x)));
        } else {
          return anyOf(left, right);
        }
      },
      null, // implies
      allOf -> allOf(allOf.targets().map(CNF::distributeOr)),
      anyOf -> anyOf(anyOf.targets().map(CNF::distributeOr))
    );
  }

  static List<AtomicFormula<?>> toClause(Formula formula) {
    return formula.match(
      var -> Arrays.asList(var),
      not -> Arrays.asList((NotVar<?>) not),
      null, // and
      or -> {
        List<AtomicFormula<?>> clause = new ArrayList<>();
        clause.addAll(toClause(or.left()));
        clause.addAll(toClause(or.right()));
        return clause;
      },
      null, // implies
      null, // allOf
      anyOf -> anyOf.targets().map(CNF::toClause).flatMap(List::stream).collect(toList())
    );
  }
}
