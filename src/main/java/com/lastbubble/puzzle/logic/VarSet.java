package com.lastbubble.puzzle.logic;

import static com.lastbubble.puzzle.logic.Formula.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarSet<T> {
  private final Map<T, Var<T>> varsByData = new HashMap<>();
  private final Map<Var<T>, Integer> idsByVar = new HashMap<>();
  private final List<Var<T>> vars = new ArrayList<>();

  public Var<T> add(T data) {
    checkArgument(data != null, "Data cannot be null");

    Var<T> var = varsByData.get(data);

    if (var == null) {

      var = var(data);
      varsByData.put(data, var);
      vars.add(var);
      idsByVar.put(var, vars.size());
    }

    return var;
  }

  public int idFor(Var<?> var) {
    checkArgument(var != null, "Var cannot be null");
    checkArgument(idsByVar.containsKey(var), "Var not registered");

    return idsByVar.get(var);
  }

  public Var<T> varFor(int id) {
    checkArgument(id != 0, "Illegal id");

    return vars.get(Math.abs(id) - 1);
  }

  private static void checkArgument(boolean condition, String reason) {

    if (!condition) { throw new IllegalArgumentException(reason); }
  }
}
