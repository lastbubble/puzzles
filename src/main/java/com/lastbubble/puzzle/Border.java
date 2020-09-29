package com.lastbubble.puzzle;

import java.util.HashMap;
import java.util.Map;

public class Border {

  public enum Weight { LIGHT, HEAVY, NONE; }

  public static char horizontal(Weight w) { return builder().left(w).right(w).build(); }

  public static char vertical(Weight w) { return builder().top(w).bottom(w).build(); }

  public static Builder builder() { return new Builder(); }

  public static class Builder {
    private Weight top = Weight.NONE,
                   left = Weight.NONE,
                   bottom = Weight.NONE,
                   right = Weight.NONE;

    public Builder top(Weight w) { top = w; return this; }
    public Builder bottom(Weight w) { bottom = w; return this; }
    public Builder left(Weight w) { left = w; return this; }
    public Builder right(Weight w) { right = w; return this; }

    public char build() {
      String key = String.format("%c%c%c%c",
        top.name().charAt(0),
        left.name().charAt(0),
        bottom.name().charAt(0),
        right.name().charAt(0)
      );
      return chars.getOrDefault(key, ' ');
    }
  }

  private static final Map<String, Character> chars = new HashMap<>();

  static {
    chars.put("NLNL", '\u2500'); // light horizontal
    chars.put("NHNH", '\u2501'); // heavy horizontal
    chars.put("LNLN", '\u2502'); // light vertical
    chars.put("HNHN", '\u2503'); // heavy vertical
    chars.put("NNLL", '\u250C'); // light down and right
    chars.put("NNLH", '\u250D'); // down light and right heavy
    chars.put("NNHL", '\u250E'); // down heavy and right light
    chars.put("NNHH", '\u250F'); // down heavy and right heavy
    chars.put("NLLN", '\u2510'); // light down and left
    chars.put("NHLN", '\u2511'); // down light and left heavy
    chars.put("NLHN", '\u2512'); // down heavy and left light
    chars.put("NHHN", '\u2513'); // down heavy and left heavy
    chars.put("LNNL", '\u2514'); // light up and right
    chars.put("LNNH", '\u2515'); // up light and right heavy
    chars.put("HNNL", '\u2516'); // up heavy and right light
    chars.put("HNNH", '\u2517'); // up heavy and right heavy
    chars.put("LLNN", '\u2518'); // light up and left
    chars.put("LHNN", '\u2519'); // up light and left heavy
    chars.put("HLNN", '\u251A'); // up heavy and left light
    chars.put("HHNN", '\u251B'); // up heavy and left heavy
    chars.put("LNLL", '\u251C'); // light vertical and right
    chars.put("LNLH", '\u251D'); // vertical right and right heavy
    chars.put("HNLL", '\u251E'); // up heavy and right down light
    chars.put("LNHL", '\u251F'); // down heavy and right up light
    chars.put("HNHL", '\u2520'); // vertical heavy and right light
    chars.put("HNLH", '\u2521'); // down light and right up heavy
    chars.put("LNHH", '\u2522'); // up light and right down heavy
    chars.put("HNHH", '\u2523'); // heavy vertical and right
    chars.put("LLLN", '\u2524'); // light vertical and left
    chars.put("LHLN", '\u2525'); // vertical light and left heavy
    chars.put("HLLN", '\u2526'); // up heavy and left down light
    chars.put("LLHN", '\u2527'); // down heavy and left up light
    chars.put("HLHN", '\u2528'); // vertical heavy and left light
    chars.put("HHLN", '\u2529'); // down light and left up heavy
    chars.put("LHHN", '\u252A'); // up light and left down heavy
    chars.put("HHHN", '\u252B'); // heavy vertical and left
    chars.put("NLLL", '\u252C'); // light down and horizontal
    chars.put("NHLL", '\u252D'); // left heavy and right down light
    chars.put("NLLH", '\u252E'); // right heavy and left down light
    chars.put("NHLH", '\u252F'); // down light and horizontal heavy
    chars.put("NLHL", '\u2530'); // down heavy and horizontal light
    chars.put("NHHL", '\u2531'); // right light and left down heavy
    chars.put("NLHH", '\u2532'); // left light and right down heavy
    chars.put("NHHH", '\u2533'); // heavy down and horizontal
    chars.put("LLNL", '\u2534'); // light up and horizontal
    chars.put("LHNL", '\u2535'); // left heavy and right up light
    chars.put("LLNH", '\u2536'); // right heavy and left up light
    chars.put("LHNH", '\u2537'); // up light and horizontal heavy
    chars.put("HLNL", '\u2538'); // up heavy and horizontal light
    chars.put("HHNL", '\u2539'); // right light and left up heavy
    chars.put("HLNH", '\u253A'); // left light and right up heavy
    chars.put("HHNH", '\u253B'); // heavy up and horizontal
    chars.put("LLLL", '\u253C'); // light vertical and horizontal
    chars.put("LHLL", '\u253D'); // left heavy and right vertical light
    chars.put("LLLH", '\u253E'); // right heavy and left vertical light
    chars.put("LHLH", '\u253F'); // vertical light and horizontal heavy
    chars.put("HLLL", '\u2540'); // up heavy and down horizontal light
    chars.put("LHLL", '\u2541'); // down heavy and up horizontal light
    chars.put("HLHL", '\u2542'); // vertical heavy and horizontal light
    chars.put("HHLL", '\u2543'); // left up heavy and right down light
    chars.put("HLLH", '\u2544'); // right up heavy and left down light
    chars.put("LHHL", '\u2545'); // left down heavy and right up light
    chars.put("LLHH", '\u2546'); // right down heavy and left up light
    chars.put("HHLH", '\u2547'); // down light and up horizontal heavy
    chars.put("LHHH", '\u2548'); // up light and down horizontal heavy
    chars.put("HHHL", '\u2549'); // right light and left vertical heavy
    chars.put("HLHH", '\u254A'); // left light and right vertical heavy
    chars.put("HHHH", '\u254B'); // heavy vertical and horizontal
  }
}
