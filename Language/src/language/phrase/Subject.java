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

public class Subject extends Phrase {
  public Subject(){
    super("подлежащее");
    this.addClassAttribute(new Noun.Case(), new Noun.Case.Subjective());
  }

  protected Subject(Subject superPhrase){
    super(superPhrase);
  }

  protected Subject(intelligence.Object word){
    super("подлежащее", word);
    this.addClassAttribute(new Noun.Case(), new Noun.Case.Subjective());
    this.addClassAttribute(new Noun.Gender(), word.getClassAttributeRecursive(new Noun.Gender()));
    this.addClassAttribute(new Noun.Number(), word.getClassAttributeRecursive(new Noun.Number()));
  }

  public LinkedList addPrePhrase(Phrase phrase){
    LinkedList ret = new LinkedList();
    Subject ph = null;
    if (phrase.isInstanceOf(new Particle())){
      ph = new Subject(this);
      ph.addAttribute(new Particle(), phrase);
      if (phrase.isInstanceOf(new language.word.Particle.Demonstrative())){
        ph.setValue(new ThisIs(this.value));
      } else if (phrase.isInstanceOf(new language.word.Particle.Negative())){
        ph.setValue(new Not(this.value));
      }
      ret.add(ph);
    } else if (phrase.isInstanceOf(new Attribute())
               && phrase.getClassAttributeRecursive(new Noun.Case()).less(this.getClassAttributeRecursive(new Noun.Case()))
               && phrase.getClassAttributeRecursive(new Noun.Gender()).less(this.getClassAttributeRecursive(new Noun.Gender()))
               && phrase.getClassAttributeRecursive(new Noun.Number()).less(this.getClassAttributeRecursive(new Noun.Number()))){
      ph = new Subject(this);
      ph.addAttribute(new Question.Какой(), phrase);
      ph.setValue(new intelligence.Object(ph.value));
      ph.value.addClassAttribute(new Question.Какой(), phrase.value);
      ret.add(ph);
    }
    return ret;
  }

  public LinkedList addPostPhrase(Phrase phrase){
    LinkedList ret = new LinkedList();
    Phrase ph = null;
    if (phrase.isInstanceOf(new IndirectObject())){
      if (phrase.hasClassAttributeRecursive(new Noun.Case(), new Noun.Case.Genetive())) {
        ph = (Phrase)this.clone();
        if (phrase.isInstanceOf(new Property())){
          intelligence.Object key = phrase.getSuperObject(new IndirectObject());
          intelligence.Object keyvalue = key.getAttribute(new Value());
          ph.addAttribute(keyvalue, phrase);
          ph.value.addClassAttribute(keyvalue, phrase.value);
          ret.add(ph);
        } else if (!ph.hasAttribute(new Question.Чей())){
          ph.addAttribute(new Question.Чей(), phrase);
          ph.value.addClassAttribute(new Question.Чей(), phrase.value);
          ret.add(ph);
        }
      }
    } else if (phrase.isInstanceOf(Predicate.PREDICATE)){
      ph = (Phrase)this.clone();
      ph.addAttribute(new Question.Делает(), phrase);
      ph.value.addClassAttribute(new Question.Делает(), phrase.value);
      ret.add(ph);
    }
    return ret;
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.hasClassAttributeRecursive(new Noun.Case(), new Noun.Case.Subjective())){
      if (word.isInstanceOf(Noun.NOUN)){
        phs.add(new Subject(word));
      } else if (word.isInstanceOf(new AdjectiveNoun())){
        phs.add(new Subject(word));
      } else if (word.isInstanceOf(new PronounAdjectiveNoun())){
        phs.add(new Subject(word));
      }
    } else if (word.isInstanceOf(new Quotation())) {
      phs.add(new Subject(word));
    }
    return phs;
  }

  public Objects getMainObjects(){
    Objects ret = new Objects();
    ret.add(new Dash());
    return ret;
  }

  public Objects getMinorObjects(){
    Objects ret = new Objects();
    intelligence.Object phrase = new IndirectObject();
    phrase.addClassAttribute(new Noun.Case(), new Noun.Case.Genetive());
    ret.add(phrase);
    return ret;
  }

  public Phrase getMoreConcrete(Phrase phrase){
    Subject ret = null;
    if (phrase.isInstanceOf(new Subject())){
      ret = new Subject();
      if (!ret.addMoreConcrete(new Noun.Gender(), this, phrase)){
        return null;
      }
      if (!ret.addMoreConcrete(new Noun.Number(), this, phrase)){
        return null;
      }
    }
    return ret;
  }

  public static void main(String[] args){
    HashMap m = new HashMap();
    m.put("key", "value");
    System.out.println(m.hashCode());
    ((HashMap)m.clone()).put("key1", "value1");
    System.out.println(m.clone().hashCode());
  }
}