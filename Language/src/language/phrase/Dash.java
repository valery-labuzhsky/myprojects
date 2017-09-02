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

public class Dash extends Phrase {
  public Dash(){
    super("тире");
  }

  protected Phrase left = null;
  protected Phrase right = null;

  public boolean isValid(){
    return this.getAnyAttributeRecursive(new Subject())!=unknown &&
        this.getAnyAttributeRecursive(new Predicate())!=unknown;
  }

  public LinkedList addPrePhrase(Phrase phrase){
    LinkedList ret = new LinkedList();
    Dash ph = null;
    if (phrase.isInstanceOf(new Subject()) && left==null &&
        phrase.hasClassAttributeRecursive(new Noun.Case(), new Noun.Case.Subjective())){
      ph = (Dash)this.clone();
      ph.addAttribute(new Subject(), phrase);
      ph.left = phrase;
      ret.add(ph);
    }
    return ret;
  }

  public LinkedList addPostPhrase(Phrase phrase){
    LinkedList ret = new LinkedList();
    Dash ph = null;
    if (phrase.isInstanceOf(new Subject()) && right==null && left!=null){
      ph = (Dash)this.clone();
      intelligence.Object obj = new Predicate();
      obj.addAttribute(new Phrase(), phrase);
      ph.addAttribute(new Predicate(), obj);
      ph.setValue((intelligence.Object)left.value.clone());
      ph.value.addAttribute(new ThisIs(), phrase.value);
      ph.right = phrase;
      ret.add(ph);
    }
    return ret;
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    if (word.isInstanceOf(new language.word.Dash())){
      phs.add(new Dash());
    }
    return phs;
  }

  public Objects getMinorObjects(){
    Objects ret = new Objects();
    if (this.getAnyAttributeRecursive(new Predicate())==unknown &&
        this.getAnyAttributeRecursive(new Subject())!=unknown){
      ret.add(new Subject());
    }
    return ret;
  }
}