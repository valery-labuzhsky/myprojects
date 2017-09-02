package language.phrase;

import intelligence.*;
import intelligence.util.*;
import java.util.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Apposition extends Phrase {
  public Apposition(){
    super("приложение");
  }

  public Apposition(intelligence.Object word) {
    super("приложение", word);
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.isInstanceOf(new Quotation())){
      phs.add(new Apposition(word));
    }
    return phs;
  }

}