package language.word;

import intelligence.*;
import language.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public class PronounAdjectiveNoun extends Pronoun{
  private Adjective noun;

  public PronounAdjectiveNoun() {
    addSuperObject(new Pronoun());
    addClassAttribute(new Question.Какой(), new AdjectiveNoun());
  }

  public PronounAdjectiveNoun(Adjective noun){
    super(noun.getName());
    this.noun = noun;
    addSuperObject(superWord=new PronounAdjectiveNoun());
    setAttributes(noun);
    if (!noun.isNormal()){
      normal = new PronounAdjectiveNoun((Adjective)noun.getNormal());
      addClassAttribute(new Form.Normal(), normal);
    } else {
      normal = this;
      addSuperObject(new Form.Normal());
    }
  }

  public static Objects getPossibleWords(String word){
    Objects ans = Adjective.getPossibleWords(word);
    Objects ret = new Objects();
    Iterator iter = ans.iterator();
    while (iter.hasNext()) {
      Adjective item = (Adjective)iter.next();
      if (!item.shortf && item.category==Adjective.QUALITATIVE_CATEGORY) {
        String normal = item.getNormal().getName();
        if (normal.equals("который")){
          ret.add(new PronounAdjectiveNoun(item).setRelative());
        }
      }
    }
    return ret;
  }

  public String getDescription(){
    if (noun == null) return "";
    String ret = noun.getDescription();
    ret = ret.replaceAll("качеств. прил.", "мест.");
    return ret;
  }
}