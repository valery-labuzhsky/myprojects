package language.word;

import intelligence.*;
import intelligence.util.*;
import java.lang.reflect.*;
import java.util.*;
import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Word extends intelligence.Object{
  protected String word;
  protected Word superWord;
  protected Word normal = null;
  protected String stem;
  protected int ending = _;
  protected String[] endings = new String[]{""};
  protected final static int _ = 0;

  public static final Word WORD = new Word();

  public Word(){
    super("часть");
    addClassAttribute(new Question.Чего(), new intelligence.Object("речь"));
  }

  protected Word(String word){
    super(word);
    this.word = word;
    this.stem = word;
  }

  protected void addWordAttribute(intelligence.Object key, intelligence.Object value){
    superWord.addClassAttribute(key, value);
  }

  protected void addWordAttributes(intelligence.Object key, intelligence.Objects values){
    superWord.addClassAttributes(key, values);
  }

  protected Objects getWordAttributes(intelligence.Object key){
    return superWord.getClassAttributes(key);
  }

  protected intelligence.Object[] getWordAttributeKeys(){
    return superWord.getClassAttributeKeys();
  }

  public Word getNormal(){
    if (isNormal()) return this;
    return normal;
  }

  public boolean isNormal(){
    return normal==null;
  }

  public String getDescription(){
    return word;
  }

  public String toString(){
    if (word==null) return super.toString();
    return getDescription()+" ("+super.toString()+")";
  }

  private static class Names{
    private class Name{
      private intelligence.Object name;
      private Objects objects = new Objects();
      private Objects norms = new Objects();
      private Objects values;

      public Name(intelligence.Object obj, intelligence.Object norm){
        this.name = new intelligence.Object(norm.getName());
        add(obj, norm);
      }

      public void add(intelligence.Object obj, intelligence.Object norm){
        objects.add(obj);
        norms.add(norm);
      }

      public boolean equals(String name){
        return this.name.getName().equals(name);
      }

      public void find(){
        values = memory.find(name);
        if (!values.isEmpty()){
          Iterator iter = norms.iterator();
          while (iter.hasNext()) {
            intelligence.Object item = (intelligence.Object) iter.next();
            item.addAttribute(new Value(), values.getOrObject());
          }
        }
      }
    }

    private LinkedList names = new LinkedList();
    private Objects objects = new Objects();
    private Memory memory;

    public Names(Memory memory){
      this.memory = memory;
    }

    public void add(intelligence.Object obj){
      intelligence.Object norm;
      if (obj.isInstanceOf(Form.NORMAL)){
        norm = obj;
      } else {
        norm = obj.getClassAttribute(Form.NORMAL);
      }
      String name = norm.getName();
      Iterator iter = names.iterator();
      boolean b = true;
      while (iter.hasNext()) {
        Name item = (Name)iter.next();
        if (item.equals(name)){
          item.add(obj, norm);
          b = false;
          break;
        }
      }
      if (b){
        names.add(new Name(obj, norm));
      }
    }

    public void find(){
      Iterator iter = names.iterator();
      while (iter.hasNext()) {
        Name item = (Name)iter.next();
        item.find();
        if (!item.values.isEmpty()) objects.add(item.objects);
      }
    }

    public boolean isEmpty(){
      return objects.isEmpty();
    }

    public Objects getObjects(){
      return objects;
    }
  }

  static public Objects getPossibleWords(Memory memory, String word){
    Objects ret = new Objects();
    intelligence.Object w = new intelligence.Object(word);
    w.addSuperObject(new ProperName());
    w.addSuperObject(new Word());
    //ищем слова в словаре
    ret = memory.find(w);
    Iterator iter = ret.iterator();
    while (iter.hasNext()) {
      intelligence.Object item = (intelligence.Object)iter.next();
      if (!item.isInstanceOf(Form.NORMAL) && !item.hasClassAttribute(Form.NORMAL)){
        iter.remove();
      }
    }
    if (ret.isEmpty()){
      ret.add(Particle.getPossibleWords(word));
      ret.add(Noun.getPossibleWords(word));
      ret.add(Verb.getPossibleWords(word));
      ret.add(Adjective.getPossibleWords(word));
      ret.add(AdjectiveNoun.getPossibleWords(word));
      ret.add(Pronoun.getPossibleWords(word));

//частичная обучаемость
      Objects founded = new Objects();
      iter = ret.iterator();
      while (iter.hasNext()){
        Word item = (Word)iter.next();
        Word n = item.getNormal();
        Objects f = memory.find(WORD, n);
        if (!f.isEmpty()){
          founded.add(item);
        }
      }
      if (!founded.isEmpty()) ret = founded;
//частичная обучаемость end

      Objects norms = new Objects();
      iter = ret.iterator();
      //ищем в словаре нормальную форму слова
      while (iter.hasNext()) {
        intelligence.Object item = (intelligence.Object)iter.next();
        intelligence.Object norm = item.getClassAttribute(Form.NORMAL);
        if (norm!=norm.unknown){
          intelligence.Objects fnorms = memory.find(norm);
          if (!fnorms.isEmpty()){
            if (!fnorms.isSingleObject()) {
              throw new RuntimeException(
                  "Found more then one normal form " + norm);
            }
            item.removeClassAttributes(Form.NORMAL);
            item.addClassAttributes(Form.NORMAL, fnorms);
            norms.add(item);
          }
        }
      }

      if (norms.isEmpty()){
        //ищем объекты, которые словами обозначаются
        Names names = new Names(memory);
        iter = ret.iterator();
        while (iter.hasNext()) {
          intelligence.Object item = (intelligence.Object) iter.next();
          names.add(item);
        }
        names.find();
        if (!names.isEmpty()) {
          ret = names.getObjects();
        }
      } else {
        ret = norms;
      }
    }
    return ret;
  }
}