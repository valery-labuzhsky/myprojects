package language.phrase;

import intelligence.*;
import java.util.*;
import language.*;
import language.word.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class IndirectObject extends Phrase {
  public IndirectObject(){
    super("дополнение");
    addClassAttribute(new Question.Какой(), new intelligence.Object("косвенный"));
  }

  protected IndirectObject(IndirectObject superPhrase){
    super(superPhrase);
    this.casep = superPhrase.casep;
    this.gender = superPhrase.gender;
    this.number = superPhrase.number;
  }

  private intelligence.Object casep;
  private intelligence.Object gender;
  private intelligence.Object number;

  protected IndirectObject(intelligence.Object word) {
    super("дополнение", word);
    addClassAttribute(new Question.Какой(), new intelligence.Object("косвенный"));
    casep = word.getClassAttributeRecursive(new Noun.Case());
    this.addClassAttribute(new Noun.Case(), casep);
    gender = word.getClassAttributeRecursive(new Noun.Gender());
    this.addClassAttribute(new Noun.Gender(), gender);
    number = word.getClassAttributeRecursive(new Noun.Number());
    this.addClassAttribute(new Noun.Number(), number);
  }

  public LinkedList addPrePhrase(Phrase phrase) {
    LinkedList ret = new LinkedList();
    Phrase ph = null;
    if (phrase.isInstanceOf(new Attribute()) &&
        phrase.getClassAttributeRecursive(new Noun.Case()).less(casep) &&
        phrase.getClassAttributeRecursive(new Noun.Gender()).less(gender) &&
        phrase.getClassAttributeRecursive(new Noun.Number()).less(number)) {
      ph = new IndirectObject(this);
      ph.addAttribute(new Question.Какой(), phrase);
      ph.setValue(new intelligence.Object(ph.value));
      ph.value.addClassAttribute(new Question.Какой(), phrase.value);
      ret.add(ph);
      if (casep.isInstanceOf(new Noun.Case.Genetive())){
//        ph = new Property(phrase);
        ph.addSuperObject(new Property());
        ret.add(ph);
      }
    }
    return ret;
  }

  public LinkedList addPostPhrase(Phrase phrase) {
    LinkedList ret = new LinkedList();
    Phrase obj = null;
    if (phrase.isInstanceOf(new Apposition())){
      obj = new IndirectObject(this);
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
      if (noun.getCase()!=Noun.ACCUSATIVE_CASE && noun.getCase()!=Noun.SUBJECTIVE_CASE){
        phs.add(new IndirectObject(word));
      }
    }
    return phs;
  }

  public Phrase getMoreConcrete(Phrase phrase){
    IndirectObject ret = null;
    if (phrase.isInstanceOf(new IndirectObject())){
      ret = new IndirectObject();
      if (!ret.addMoreConcrete(new Noun.Case(), this, phrase)){
        return null;
      }
    }
    return ret;
  }

  public Objects getMinorObjects(){
    Objects ret = new Objects();
    ret.add(new Apposition());
    return ret;
  }

}
