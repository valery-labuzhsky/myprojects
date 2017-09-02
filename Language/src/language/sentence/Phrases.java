package language.sentence;

import intelligence.*;
import java.util.*;
import language.phrase.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Phrases implements Cloneable{
  LinkedList phrases = new LinkedList();

  private class PossiblePhrase{
    private Phrase phrase;
    private Objects main;
    private Objects minor;

    public PossiblePhrase(Phrase phrase){
      this.phrase = phrase;
      this.main = phrase.getMainObjects();
      this.minor = phrase.getMinorObjects();
    }

    public PossiblePhrase(Phrase phrase, Objects main, Objects minor){
      this.phrase = phrase;
      this.main = new Objects();
      this.main.add(intersection(main, phrase.getMainObjects()));
      this.main.add(intersection(minor, phrase.getMainObjects()));
      if (found(minor)) this.minor = phrase.getMinorObjects();
      else this.minor = new Objects();
    }

    private boolean found(Objects obj){
      Iterator iter = obj.iterator();
      while (iter.hasNext()) {
        intelligence.Object item = (intelligence.Object)iter.next();
        if (phrase.isInstanceOf(item)){
          return true;
        }
      }
      return false;
    }

    private Objects intersection(Objects obj1, Objects obj2){
      Objects conc = new Objects();
      Iterator iter = obj1.iterator();
      while (iter.hasNext()) {
        Phrase item = (Phrase)iter.next();
        Iterator iter0 = obj2.iterator();
        while (iter0.hasNext()) {
          Phrase item0 = (Phrase)iter0.next();
          Phrase c = item.getMoreConcrete(item0);
          if (c!=null) conc.add(c);
        }
      }
      return conc;
    }
  }

  public Phrases() {
  }

  public Phrase removeLast(){
    return ((PossiblePhrase)phrases.removeLast()).phrase;
  }

  public void addLast(Phrase phrase){
    if (phrases.isEmpty()) {
      phrases.addLast(new PossiblePhrase(phrase));
    } else {
      PossiblePhrase phr = (PossiblePhrase)phrases.getLast();
      phrases.addLast(new PossiblePhrase(phrase, phr.main, phr.minor));
    }
  }

  public Phrase getFirst(){
    return ((PossiblePhrase)phrases.getFirst()).phrase;
  }

  public String toString(){
    String str = "";
    Iterator iter = phrases.iterator();
    while (iter.hasNext()) {
      PossiblePhrase item = (PossiblePhrase)iter.next();
      str+=item.phrase.toStringWithAttribute()+" ";
      str+=item.main+" ";
      str+=item.minor+" ";
    }
    return str;
  }

  public boolean isValid(){
    return phrases.size()==1&&((PossiblePhrase)phrases.getFirst()).phrase.isValid();
  }

  public boolean isEmpty(){
    return phrases.isEmpty();
  }

  public boolean isPossible(){
    return isValid()||!(((PossiblePhrase)phrases.getLast()).main.isEmpty()&&
                        ((PossiblePhrase)phrases.getLast()).minor.isEmpty());
  }

  private LinkedList concatenatePhrase(Phrase phrase){
    LinkedList sens = new LinkedList();
    if (!phrases.isEmpty()){
      PossiblePhrase ph = (PossiblePhrase)phrases.removeLast();
      LinkedList phr;
      phr = ph.phrase.addPostPhrase(phrase);
      sens.addAll(this.getPossibleSentences(phr));
      phr = phrase.addPrePhrase(ph.phrase);
      sens.addAll(this.getPossibleSentences(phr));
      phrases.addLast(ph);
    }
    return sens;
  }

  public LinkedList getPossibleSentences(LinkedList list){
    LinkedList ret = new LinkedList();
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      Phrase item = (Phrase)iter.next();
      ret.addAll(this.getPossibleSentences(item));
    }
    return ret;
  }

  public LinkedList getPossibleSentences(Phrase phrase){
    LinkedList sens = new LinkedList();
    sens.addAll(((Phrases)this.clone()).concatenatePhrase(phrase));
    Phrases ns = (Phrases)this.clone();
    ns.addLast(phrase);
    if (ns.isPossible()){
      sens.add(ns);
    }
    return sens;
  }

  public java.lang.Object clone(){
    Phrases clone = null;
    try {
      clone = (Phrases)super.clone();
      clone.phrases = (LinkedList)phrases.clone();
    }
    catch (CloneNotSupportedException ex) {
      ex.printStackTrace();
    }
    return clone;
  }

}