package com.lastbubble.puzzle.logic;

import static com.lastbubble.puzzle.logic.Formula.var;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import com.lastbubble.puzzle.logic.Formula.Var;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VarSetTest {

  @Rule public ExpectedException thrown = ExpectedException.none();

  private final VarSet<String> vars = new VarSet<String>();

  @Test public void add_requiresNonNullData() {
    thrown.expect(IllegalArgumentException.class);

    vars.add(null);
  }

  @Test public void add_sameVarForSameData() {
    String data = "data";

    assertThat(vars.add(data), is(sameInstance(vars.add(data))));
  }

  @Test public void add_sameVarForEqualData() {
    assertThat(vars.add("data"), is(sameInstance(vars.add("data"))));
  }

  @Test public void add_differentVarsForDifferentData() {
    String data1 = "data1";
    String data2 = "data2";

    assertThat(vars.add(data1), is(not(vars.add(data2))));
  }

  @Test public void idFor_requiresNonNullVar() {
    thrown.expect(IllegalArgumentException.class);

    vars.idFor(null);
  }

  @Test public void idFor_unrecognizedVar() {
    thrown.expect(IllegalArgumentException.class);

    vars.idFor(var("test"));
  }

  @Test public void idFor_registeredVar() {
    String data = "data";
    Var<String> var = vars.add(data);

    assertThat(vars.idFor(var), is(1));
    assertThat(vars.idFor(vars.add(data)), is(1));
  }

  @Test public void idFor_differentVars() {
    Var<String> var1 = vars.add("a");
    Var<String> var2 = vars.add("b");

    assertThat(vars.idFor(var1), is(1));
    assertThat(vars.idFor(var2), is(2));
  }

  @Test public void varFor_illegalId() {
    thrown.expect(IllegalArgumentException.class);

    vars.varFor(0);
  }

  @Test public void varFor_invalidId() {
    thrown.expect(IndexOutOfBoundsException.class);

    vars.varFor(1);
  }

  @Test public void varFor_registeredId() {
    Var<String> var1 = vars.add("a");
    Var<String> var2 = vars.add("b");

    assertThat(vars.varFor(vars.idFor(var1)), is(var1));
    assertThat(vars.varFor(vars.idFor(var2)), is(var2));
  }

  @Test public void varFor_negativeId() {
    Var<String> var1 = vars.add("a");
    Var<String> var2 = vars.add("b");

    assertThat(vars.varFor(-vars.idFor(var1)), is(var1));
    assertThat(vars.varFor(-vars.idFor(var2)), is(var2));
  }
}
