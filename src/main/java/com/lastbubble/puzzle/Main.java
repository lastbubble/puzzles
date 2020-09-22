package com.lastbubble.puzzle;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

  private static void usage(PrintStream out) {
    out.println("Usage:");
    out.println("  Main <solverClassName>");
    out.println("    or");
    out.println("  Main <solverClassName> <puzzleResourcePath>");
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      usage(System.out);
      System.exit(1);
    }

    Class<?> solverClass = Class.forName(String.format("com.lastbubble.puzzle.%s", args[0]));

    if (args.length == 1) {
      ((Runnable) solverClass.getConstructor().newInstance()).run();

    } else {
      URL puzzleResource = solverClass.getResource(args[1]);
      if (puzzleResource == null) {
        System.err.format("Puzzle %s not found for solver %s%n", args[1], solverClass);
        System.exit(1);
      }

      Iterable<String> puzzleLines = Files.readAllLines(Paths.get(puzzleResource.toURI()), UTF_8);

      Method loadMethod = solverClass.getMethod("load", Iterable.class);

      ((Runnable) loadMethod.invoke(null, puzzleLines)).run();
    }
  }
}
