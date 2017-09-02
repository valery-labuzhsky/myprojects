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

public class Phrase extends intelligence.Object{
  public Phrase(){
    super("член");
    addClassAttribute(new Question.Чего(), new intelligence.Object("предложение"));
  }

  protected Phrase(String name){
    super(name);
    addSuperObject(new Phrase());
  }

  protected intelligence.Object word = null;
  protected intelligence.Object value = null;
  public static final Phrase PHRASE = new Phrase();

  protected Phrase(Phrase superPhrase){
    super(superPhrase);
    setValue(superPhrase.value);
  }

  protected Phrase(String name, intelligence.Object word) {
    this(name);
    this.addClassAttribute(new Word(), word);
    this.word = word;
    this.value = getObject(word);
    this.addAttribute(new Value(), value);
  }

  protected void setValue(intelligence.Object value){
    this.value = value;
    if (value.getName()=="@неизвестно") {
      value = value;
    }
    this.removeAttributes(new Value());
    this.addAttribute(new Value(), value);
  }

  public LinkedList addPostPhrase(Phrase phrase){
    return new LinkedList();
  }

  public LinkedList addPrePhrase(Phrase phrase){
    return new LinkedList();
  }

  public boolean isValid(){
    return true;
  }

  public static LinkedList concatenatePhrases(Phrase first, Phrase second){
    LinkedList phs = new LinkedList();
    LinkedList phrase;
    phrase = first.addPostPhrase(second);
    phs.addAll(phrase);
    phrase = second.addPrePhrase(first);
    phs.addAll(phrase);
    return phs;
  }

  public static LinkedList getPossiblePhrases(intelligence.Object word){
    LinkedList phs = new LinkedList();
    phs.addAll(Subject.getPossiblePhrases(word));
    phs.addAll(Predicate.getPossiblePhrases(word));
    phs.addAll(DirectObject.getPossiblePhrases(word));
    phs.addAll(IndirectObject.getPossiblePhrases(word));
    phs.addAll(Attribute.getPossiblePhrases(word));
    phs.addAll(Apposition.getPossiblePhrases(word));
    phs.addAll(Particle.getPossiblePhrases(word));
    phs.addAll(Dash.getPossiblePhrases(word));
    return phs;
  }

  protected intelligence.Object getObject(intelligence.Object word){
    intelligence.Object norm;
    if (word.isInstanceOf(Form.NORMAL)){
      norm = word;
    } else {
      norm = word.getClassAttribute(Form.NORMAL);
    }
    intelligence.Object value = norm.getAttribute(new Value());
    if (value == intelligence.Object.unknown) {
      value = new intelligence.Object(norm.getName());
      if (norm.isInstanceOf(new language.word.Quotation())){
        value.addSuperObject(new ProperName());
      }
    }
    value = value.cloneStructure();
    if (!value.hasAttribute(new Word(), norm)){
      value.addAttribute(new Word(), norm);
    }
    return value;
  }

  protected boolean addMoreConcrete(intelligence.Object key, intelligence.Object obj1, intelligence.Object obj2){
    intelligence.Object l1 = obj1.getClassAttributeRecursive(key);
    intelligence.Object l2 = obj2.getClassAttributeRecursive(key);
    if (l1==l1.unknown){
      if (l2!=l2.unknown){
        addClassAttribute(key, l2);
      }
    } else {
      if (l2!=l2.unknown){
        if (!l2.equals(l1)){
          return false;
        }
      }
      addClassAttribute(key, l1);
    }
    return true;
  }

  public Objects getMainObjects(){
    return new Objects();
  }

  public Objects getMinorObjects(){
    return new Objects();
  }

  public Phrase getMoreConcrete(Phrase phrase){
    if (this.getClass().equals(phrase.getClass())) return (Phrase)clone();
    return null;
  }

  public String toString(){
    String str = super.toString();
    if (word!=null) str+=" "+word;
    return str;
  }

  public java.lang.Object clone(){
    Phrase ret = (Phrase)super.clone();
    if (value!=null){
      ret.value = (intelligence.Object)value.clone();
      ret.setValue(ret.value);
    }
    return ret;
  }

}