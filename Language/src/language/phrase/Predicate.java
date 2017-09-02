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

public class Predicate extends Phrase {
  public static final Predicate PREDICATE = new Predicate();

  public Predicate(){
    super("���������");
  }

  protected Predicate(intelligence.Object word){
    super("���������", word);
    value.addSuperObject(new Action());
    if (word.isInstanceOf(new Verb())) {
      if (word.hasClassAttribute(new Verb.Mood(), new Verb.Mood.Imperative())) {
        value.addClassAttribute(new Question.�����(), Action.imperative);
      }
    }
  }

  protected Predicate(Predicate superPhrase){
    super(superPhrase);
  }

  public LinkedList addPostPhrase(Phrase phrase){
    LinkedList ret = new LinkedList();
    Phrase ph = null;
    if (phrase.isInstanceOf(new DirectObject()) && this.getAnyAttributeRecursive(new Question.����())==unknown){
      ph = new Predicate(this);
      ph.addAttribute(new Question.����(), phrase);
      ph.setValue(new intelligence.Object(ph.value));
      ph.value.addClassAttribute(new Question.����(), phrase.value);
      ret.add(ph);
    }
    return ret;
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.isInstanceOf(new Verb())){
      phs.add(new Predicate(word));
    }
    return phs;
  }

  public Objects getMinorObjects(){
    Objects ret = new Objects();
    if (this.getAnyAttributeRecursive(new Question.����())==unknown){
      ret.add(new DirectObject());
    }
    return ret;
  }

/*  public Phrase getMoreConcrete(Phrase phrase){
    if (phrase instanceof Predicate) return new Predicate();
    return null;
  }*/
}