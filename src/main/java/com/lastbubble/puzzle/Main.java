package com.lastbubble.puzzle;

public class Main {

  public static void main(String[] args) throws Exception {
    ((Runnable) Class.forName(classNameFor(args)).getConstructor().newInstance()).run();
  }

  private static String classNameFor(String[] args) {

    if (args.length == 2) {

      return String.format("com.lastbubble.puzzle.issue.%s.%s", args[0], args[1]);

    } else if (args.length == 1) {

      return String.format("com.lastbubble.puzzle.%s", args[0]);
    }

    throw new IllegalArgumentException("Must supply one or two arguments");
  }
}
