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

public class DirectObject extends Phrase {
  public DirectObject() {
    super("дополнение");
    addClassAttribute(new Question.Какой(), new intelligence.Object("прямой"));
    addClassAttribute(new Noun.Case(), new Noun.Case.Accusative());
  }

  protected DirectObject(DirectObject superPhrase){
    super(superPhrase);
  }

  protected DirectObject(intelligence.Object word) {
    super("дополнение", word);
    addClassAttribute(new Question.Какой(), new intelligence.Object("прямой"));
    addClassAttribute(new Noun.Case(), new Noun.Case.Accusative());
    addClassAttribute(new Noun.Gender(), word.getClassAttributeRecursive(new Noun.Gender()));
    addClassAttribute(new Noun.Number(), word.getClassAttributeRecursive(new Noun.Number()));
  }

  public LinkedList addPrePhrase(Phrase phrase) {
    LinkedList ret = new LinkedList();
    Phrase ph = null;
    if (phrase.isInstanceOf(new Attribute()) &&
        phrase.getAnyAttributeRecursive(new Noun.Case()).less(this.getAnyAttributeRecursive(new Noun.Case())) &&
        phrase.getAnyAttributeRecursive(new Noun.Gender()).less(this.getAnyAttributeRecursive(new Noun.Gender())) &&
        phrase.getAnyAttributeRecursive(new Noun.Number()).less(this.getAnyAttributeRecursive(new Noun.Number()))) {
      ph = new DirectObject(this);
      ph.addAttribute(new Question.Какой(), phrase);
      ph.setValue(new intelligence.Object(ph.value));
      ph.value.addClassAttribute(new Question.Какой(), phrase.value);
      ret.add(ph);
    }
    return ret;
  }

  public LinkedList addPostPhrase(Phrase phrase) {
    LinkedList ret = new LinkedList();
    Phrase obj = null;
    if (phrase.isInstanceOf(new Apposition())){
      obj = new DirectObject(this);
      obj.addAttribute(new Question.Какой(), phrase);
      intelligence.Object val = phrase.value.cloneStructure();
      val.addSuperObject(obj.value);
      obj.setValue(val);
      ret.add(obj);
    }
    return ret;
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.isInstanceOf(Noun.NOUN)){
      Noun noun = (Noun)word;
      if (noun.getCase()==Noun.ACCUSATIVE_CASE){
        phs.add(new DirectObject(word));
      }
    }
    return phs;
  }

  public Objects getMinorObjects(){
    Objects ret = new Objects();
    ret.add(new Apposition());
    return ret;
  }

}