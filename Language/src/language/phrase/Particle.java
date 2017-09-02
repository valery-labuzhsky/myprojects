package language.phrase;

import intelligence.*;
import intelligence.util.*;
import java.util.*;
import language.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Particle extends Phrase {
  public Particle(){
    super("частица");
  }

  public Particle(intelligence.Object word) {
    super("частица", word);
    this.addClassAttribute(new Question.Какой(), word.getClassAttributeRecursive(new Question.Какой()));
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.isInstanceOf(new language.word.Particle())){
      phs.add(new Particle(word));
    }
    return phs;
  }

  public Objects getMainObjects(){
    Objects ret = new Objects();
    ret.add(new Subject());
    return ret;
  }

  public Phrase getMoreConcrete(Phrase phrase){
    Particle ret = null;
    if (phrase.isInstanceOf(new Particle())){
      ret = new Particle();
      if (!ret.addMoreConcrete(new Question.Какой(), this, phrase)){
        return null;
      }
    }
    return ret;
  }

}