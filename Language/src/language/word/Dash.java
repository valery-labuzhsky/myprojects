package language.word;

import intelligence.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Dash extends Word {

  public Dash() {
    super("тире");
    addSuperObject(new Word());
    this.word = "-";
  }
}