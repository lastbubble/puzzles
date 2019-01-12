package com.lastbubble.puzzle.logic;

import static java.util.stream.Collectors.joining;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class FormulaMatchers {

  public static Matcher<? super Formula> matching(String s) {
    return new StringMatcher(s);
  }

  private static class StringMatcher extends TypeSafeMatcher<Formula> {
    private final String s;

    private StringMatcher(String s) { this.s = s; }

    @Override protected boolean matchesSafely(Formula formula) {
      return asString(formula).equals(s);
    }

    @Override public void describeTo(Description description) {
      description.appendText("matching ");
      description.appendText(s);
    }
  
    @Override protected void describeMismatchSafely(Formula formula, Description description) {
      description.appendText("was ");
      description.appendText(asString(formula));
    }
  
    private static String asString(Formula formula) {
      return formula.<String>match(
        var -> var.data().toString(),
        not -> "-(" + asString(not.target()) + ")",
        and -> "(" + asString(and.left()) + " & " + asString(and.right()) + ")",
        or -> "(" + asString(or.left()) + " | " + asString(or.right()) + ")",
        implies -> asString(implies.left()) + " -> " + asString(implies.right()),
        allOf -> allOf.targets().map(StringMatcher::asString).collect(joining(" & ")),
        anyOf -> anyOf.targets().map(StringMatcher::asString).collect(joining(" | "))
      );
    }
  }
}
