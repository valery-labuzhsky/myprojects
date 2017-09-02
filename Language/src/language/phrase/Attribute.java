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

public class Attribute extends Phrase {
  public Attribute(){
    super("определение");
  }

  public Attribute(intelligence.Object word) {
    super("определение", word);
    this.addClassAttribute(new Noun.Case(), word.getAnyAttributeRecursive(new Noun.Case()));
    this.addClassAttribute(new Noun.Gender(), word.getAnyAttributeRecursive(new Noun.Gender()));
    this.addClassAttribute(new Noun.Number(), word.getAnyAttributeRecursive(new Noun.Number()));
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.isInstanceOf(new Adjective())){
      phs.add(new Attribute(word));
    }
    return phs;
  }

  public Phrase getMoreConcrete(Phrase phrase){
    Attribute ret = null;
    if (phrase.isInstanceOf(new Attribute())){
      ret = new Attribute();
      if (!ret.addMoreConcrete(new Noun.Gender(), this, phrase)){
        return null;
      }
      if (!ret.addMoreConcrete(new Noun.Case(), this, phrase)){
        return null;
      }
    }
    return ret;
  }

  public Objects getMainObjects(){
    Objects ret = new Objects();
    intelligence.Object cs = this.getClassAttributeRecursive(new Noun.Case());
    intelligence.Object phrase;
    if (cs.isInstanceOf(new Noun.Case.Subjective())){
      phrase = new Subject();
      phrase.addClassAttribute(new Noun.Case(), cs);
      phrase.addClassAttributes(new Noun.Gender(),
                                this.getClassAttributesRecursive(new Noun.Gender()));
      ret.add(phrase);
    } else if (cs.isInstanceOf(new Noun.Case.Accusative())){
      phrase = new DirectObject();
      phrase.addClassAttribute(new Noun.Case(), cs);
      phrase.addClassAttributes(new Noun.Gender(),
                                this.getClassAttributesRecursive(new Noun.Gender()));
      ret.add(phrase);
    } else {
      phrase = new IndirectObject();
      phrase.addClassAttribute(new Noun.Case(), cs);
      phrase.addClassAttributes(new Noun.Gender(),
                                this.getClassAttributesRecursive(new Noun.Gender()));
      ret.add(phrase);
    }
    return ret;
  }

}