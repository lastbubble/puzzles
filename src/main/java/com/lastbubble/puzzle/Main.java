package com.lastbubble.puzzle;

public class Main {
  public static void main(String[] args) throws Exception {
    String className = String.format("com.lastbubble.puzzle.issue.%s.%s", args[0], args[1]);
    Runnable r = (Runnable) Class.forName(className).newInstance();
    r.run();
  }
}
