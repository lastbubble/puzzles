package com.lastbubble.puzzle.issue.oct2020;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.lastbubble.puzzle.common.Cell;
import com.lastbubble.puzzle.common.Direction;
import com.lastbubble.puzzle.common.CharRaster;
import com.lastbubble.puzzle.common.Grid;
import com.lastbubble.puzzle.common.GridPrinter;
import com.lastbubble.puzzle.common.Mover;
import com.lastbubble.puzzle.common.Pos;

public class Boomerangs implements Runnable {

  public static Boomerangs load(Iterable<String> lines) {
    Grid.Builder<Boolean> gridBuilder = Grid.builder(Boolean.class);

    Pattern sizePtn = Pattern.compile("(\\d+)x(\\d+)");
    Pattern dotPtn = Pattern.compile("(\\d+),(\\d+)");
    Matcher m;

    for (String line : lines) {
      if ((m = dotPtn.matcher(line)).matches()) {
        Pos dot = Pos.at(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(2)));
        gridBuilder.add(Cell.at(dot).withValue(true));

      } else if ((m = sizePtn.matcher(line)).matches()) {
        int width = Integer.valueOf(m.group(1));
        int height = Integer.valueOf(m.group(2));
        gridBuilder.add(Cell.at(width - 1, height - 1));
      }
    }

    return new Boomerangs(gridBuilder.build());
  }

  private final Grid<Boolean> grid;
  private final Mover move;
  private final Set<Pos> dots;

  private Boomerangs(Grid<Boolean> grid) {
    this.grid = grid;
    move = grid.mover();
    this.dots = grid.cellsMatching(c -> c.value().isPresent()).map(Cell::pos).collect(Collectors.toSet());
  }

  @Override public void run() {

    PossibleElls possibleElls = null;
    for (Pos dot : dots) {
      PossibleElls next = new PossibleElls(generateElls(dot), possibleElls);
      possibleElls = next;
    }

    EllList solution = solve(possibleElls);

    System.out.format("solution = %s%n", solution);

    if (solution != null) {
      printSolution( new Solution(solution));
    } else {
      printSolution( new Solution());
    }
  }

  protected Collection<Ell> generateElls(Pos dot) {

    Set<Ell> ells = new HashSet<>();

    Pos furthestLeft = furthest(dot, Direction.LEFT);
    Pos furthestRight = furthest(dot, Direction.RIGHT);

    for (int x1 = furthestLeft.x(); x1 <= dot.x(); x1++) {
      for (int x2 = dot.x(); x2 <= furthestRight.x(); x2++) {
        for (Ell ell : generateHorizontalElls(Pos.at(x1, dot.y()), Pos.at(x2, dot.y()))) {
          Optional<Pos> otherDot = dots.stream().filter(d -> !d.equals(dot) && ell.contains(d)).findFirst();
          if (otherDot.isEmpty()) { ells.add(ell); }
        }
      }
    }

    Pos furthestUp = furthest(dot, Direction.UP);
    Pos furthestDown = furthest(dot, Direction.DOWN);

    for (int y1 = furthestUp.y(); y1 <= dot.y(); y1++) {
      for (int y2 = dot.y(); y2 <= furthestDown.y(); y2++) {
        for (Ell ell : generateVerticalElls(Pos.at(dot.x(), y1), Pos.at(dot.x(), y2))) {
          Optional<Pos> otherDot = dots.stream().filter(d -> d != dot && ell.contains(d)).findFirst();
          if (otherDot.isEmpty()) { ells.add(ell); }
        }
      }
    }

    List<Ell> sortedElls = new ArrayList<>(ells);
    Collections.sort(sortedElls, Comparator.<Ell, Integer>comparing(ell -> ell.length).reversed());
    return sortedElls;
  }

  protected Iterable<Ell> generateHorizontalElls(Pos left, Pos right) {

    List<Ell> ells = new ArrayList<>();

    int length = right.x() - left.x();
    if (length > 0) {

      Optional<Pos> upLeft = move.up(left, length);
      if (upLeft.isPresent()) {
        ells.add( new Ell(left, length, Direction.UP, Direction.RIGHT));
      }

      Optional<Pos> downLeft = move.down(left, length);
      if (downLeft.isPresent()) {
        ells.add( new Ell(left, length, Direction.DOWN, Direction.RIGHT));
      }

      Optional<Pos> upRight = move.up(right, length);
      if (upRight.isPresent()) {
        ells.add( new Ell(right, length, Direction.UP, Direction.LEFT));
      }

      Optional<Pos> downRight = move.down(right, length);
      if (downRight.isPresent()) {
        ells.add( new Ell(right, length, Direction.DOWN, Direction.LEFT));
      }
    }

    return ells;
  }

  protected Iterable<Ell> generateVerticalElls(Pos top, Pos bottom) {

    List<Ell> ells = new ArrayList<>();

    int length = bottom.y() - top.y();
    if (length > 0) {

      Optional<Pos> leftTop = move.left(top, length);
      if (leftTop.isPresent()) {
        ells.add( new Ell(top, length, Direction.DOWN, Direction.LEFT));
      }

      Optional<Pos> rightTop = move.right(top, length);
      if (rightTop.isPresent()) {
        ells.add( new Ell(top, length, Direction.DOWN, Direction.RIGHT));
      }

      Optional<Pos> leftBottom = move.left(bottom, length);
      if (leftBottom.isPresent()) {
        ells.add( new Ell(bottom, length, Direction.UP, Direction.LEFT));
      }

      Optional<Pos> rightBottom = move.right(bottom, length);
      if (rightBottom.isPresent()) {
        ells.add( new Ell(bottom, length, Direction.UP, Direction.RIGHT));
      }
    }

    return ells;
  }

  protected Pos furthest(Pos pos, Direction direction) {

    Pos furthest = pos;
    Optional<Pos> nextPos;
    while ((nextPos = move.move(direction, furthest, 1)).isPresent()) {
      if (dots.contains(nextPos.get())) { break; }
      furthest = nextPos.get();
    }
    return furthest;
  }

  protected EllList solve(PossibleElls possibleElls) {
    for (Ell ell : possibleElls.ells) {
      EllList next = solve(possibleElls.next, new EllList(ell, null));
      if (next != null) { return next; }
    }
    return null;
  }

  protected EllList solve(PossibleElls possibleElls, EllList acc) {
    if (possibleElls == null) { return acc; }
    for (Ell ell : possibleElls.ells) {
      if (acc.intersects(ell)) { continue; }
      EllList next = solve(possibleElls.next, acc.add(ell));
      if (next != null && next.size() == grid.width() * grid.height()) { return next; }
    }
    return null;
  }

  protected void printSolution(Solution solution) {
    CharRaster raster = CharRaster.builder().ofWidth(2 * grid.width() + 1).ofHeight(2 * grid.height() + 1).build();
    GridPrinter<Boolean> printer = new GridPrinter<Boolean>(raster::set, b -> b ? '\u25CF' : ' ');
    printer.suppressBorderBetweenConnectedCells().print(grid, solution);
    raster.lines().forEach(System.out::println);
  }

  protected static class Solution implements BiPredicate<Pos, Pos> {

    private final Map<Pos, Ell> ellForPos = new HashMap<>();

    protected Solution(EllList ells) {
      EllList x = ells;
      while (x != null) {
        for (Pos cell : x.ell) { ellForPos.put(cell, x.ell); }
        x = x.next;
      }
    }
    protected Solution(Ell... ells) {
      for (Ell ell : ells) {
        for (Pos cell : ell) { ellForPos.put(cell, ell); }
      }
    }

    @Override public boolean test(Pos a, Pos b) { return ellForPos.get(a) == ellForPos.get(b); }
  }

  protected class Ell implements Iterable<Pos> {

    private final Pos vertex;
    private final int length;
    private final Direction d1;
    private final Direction d2;
    private final Set<Pos> cells = new HashSet<>();

    protected Ell(Pos vertex, int length, Direction d1, Direction d2) {
      if (d1 == d2 || d1 == d2.opposite()) {
        throw new IllegalArgumentException(String.format("%s and %s do not form an ell", d1, d2));
      }
      this.vertex = vertex;
      this.length = length;
      this.d1 = d1;
      this.d2 = d2;
      cells.add(vertex);
      for (int i = 1; i <= length; i++) {
        cells.add(move.move(d1, vertex, i).get());
        cells.add(move.move(d2, vertex, i).get());
      }
    }

    public boolean intersects(Ell that) {
      Set<Pos> thatCells = that.cells;
      for (Pos cell : cells) { if (thatCells.contains(cell)) { return true; } }
      return false;
    }

    @Override public Iterator<Pos> iterator() { return cells.iterator(); }

    public boolean contains(Pos pos) {
      if (pos.equals(vertex)) { return true; }

      if (pos.x() == vertex.x()) {
        int delta = pos.y() - vertex.y();
        if (delta < 0) { return d1 == Direction.UP && Math.abs(delta) <= length; }
        else { return d1 == Direction.DOWN && delta <= length; }
      } else if (pos.y() == vertex.y()) {
        int delta = pos.x() - vertex.x();
        if (delta < 0) { return d2 == Direction.LEFT && Math.abs(delta) <= length; }
        else { return d2 == Direction.RIGHT && delta <= length; }
      }

      return false;
    }

    @Override public int hashCode() { return Objects.hash(vertex, length, d1, d2); }

    @Override public boolean equals(Object obj) {
      if (obj == this) { return true; }
      if (obj instanceof Ell) {
        Ell that = (Ell) obj;
        return (
          Objects.equals(this.vertex, that.vertex) &&
          this.length == that.length &&
          this.d1 == that.d1 &&
          this.d2 == that.d2
        );
      }
      return false;
    }

    @Override public String toString() {
      return String.format("Ell[%s, %d, %c]", vertex, length, describe(d1, d2));
    }

    public char describe(Direction d1, Direction d2) {
      switch (d1) {
        case UP:
          if (d2 == Direction.LEFT) { return '\u2518'; }
          else if (d2 == Direction.RIGHT) { return '\u2514'; }
        case DOWN:
          if (d2 == Direction.LEFT) { return '\u2510'; }
          else if (d2 == Direction.RIGHT) { return '\u250C'; }
        case LEFT:
          if (d2 == Direction.UP) { return '\u2518'; }
          else if (d2 == Direction.DOWN) { return '\u2510'; }
        case RIGHT:
          if (d2 == Direction.UP) { return '\u2514'; }
          else if (d2 == Direction.DOWN) { return '\u250C'; }
      }
      throw new AssertionError(String.format("unsupported ell %s-%s", d1, d2));
    }
  }

  protected class EllList {
    private final Ell ell;
    private final EllList next;
    protected EllList(Ell ell, EllList next) {
      this.ell = ell;
      this.next = next;
    }
    protected EllList add(Ell ell) { return new EllList(ell, this); }
    protected boolean intersects(Ell target) {
      if (target.intersects(ell)) { return true; }
      return (next != null && next.intersects(target));
    }
    protected int size() {
      int size = ell.cells.size();
      if (next != null) { size += next.size(); }
      return size;
    }

    @Override public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(ell.toString());
      if (next != null) {
        buf.append(',').append(next.toString());
      }
      return buf.toString();
    }
  }

  protected class PossibleElls {
    private final Collection<Ell> ells;
    private final PossibleElls next;

    protected PossibleElls(Collection<Ell> ells, PossibleElls next) {
      this.ells = ells;
      this.next = next;
    }
  }
}
