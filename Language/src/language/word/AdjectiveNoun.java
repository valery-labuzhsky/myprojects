package language.word;

import intelligence.*;
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

public class AdjectiveNoun extends Noun{
  private Adjective adjective;

  public AdjectiveNoun(){
  }

  public AdjectiveNoun(Adjective adjective){
    super(adjective.word, adjective.stem, adjective.ending, 0, adjective.getCase(),
          adjective.getGender(), adjective.getNumber());
    this.adjective = adjective;
    if (!isNormal()){
      removeClassAttributes(Form.NORMAL);
      normal = new AdjectiveNoun((Adjective)adjective.getForm(SUBJECTIVE_CASE, gender, SINGULAR_NUMBER));
      addClassAttribute(new Form.Normal(), normal);
    }
//    setClassAttributes();
  }

  public final static String[] endings = Adjective.endings;

/*  protected void setClassAttributes(){
    switch (wcase){
      case Noun.SUBJECTIVE_CASE:
        addWordAttribute(new Noun.Case(), new Noun.Case.Subjective());
        break;
      case Noun.GENITIVE_CASE:
        addWordAttribute(new Noun.Case(), new Noun.Case.Genetive());
        break;
      case Noun.DATIVE_CASE:
        addWordAttribute(new Noun.Case(), new Noun.Case.Dative());
        break;
      case Noun.ACCUSATIVE_CASE:
        addWordAttribute(new Noun.Case(), new Noun.Case.Accusative());
        break;
      case Noun.ABLATIVE_CASE:
        addWordAttribute(new Noun.Case(), new Noun.Case.Ablative());
        break;
      case Noun.PREPOSITIONAL_CASE:
        addWordAttribute(new Noun.Case(), new Noun.Case.Prepositional());
        break;
    }

    switch (gender){
      case Noun.MASCULINE_GENDER:
        addWordAttribute(new Noun.Gender(), new Noun.Gender.Masculine());
        break;
      case Noun.FEMININE_GENDER:
        addWordAttribute(new Noun.Gender(), new Noun.Gender.Feminine());
        break;
      case Noun.NEUTER_GENDER:
        addWordAttribute(new Noun.Gender(), new Noun.Gender.Neuter());
        break;
    }

    switch (number){
      case Noun.SINGULAR_NUMBER:
        addWordAttribute(new Noun.Number(), new Noun.Number.Singular());
        break;
      case Noun.PLURAL_NUMBER:
        addWordAttribute(new Noun.Number(), new Noun.Number.Plural());
        break;
    }

    if (!isNormal()){
      removeClassAttributes(Form.NORMAL);
      normal = new AdjectiveNoun((Adjective)adjective.getForm(SUBJECTIVE_CASE, gender, SINGULAR_NUMBER));
      addClassAttribute(new Form.Normal(), normal);
    }
  }*/

  public String getDescription(){
    if (stem==null) return "";
    String w = stem+'-';
    String d = " ";
    d+="сущ. ";
    w+=endings[ending];
    switch (wcase){
      case Noun.SUBJECTIVE_CASE:
        d+="И.П. ";
        break;
      case Noun.GENITIVE_CASE:
        d+="Р.П. ";
        break;
      case Noun.DATIVE_CASE:
        d+="Д.П. ";
        break;
      case Noun.ACCUSATIVE_CASE:
        d+="В.П. ";
        break;
      case Noun.ABLATIVE_CASE:
        d+="Т.П. ";
        break;
      case Noun.PREPOSITIONAL_CASE:
        d+="П.П. ";
        break;
    }
    switch(gender){
      case Noun.MASCULINE_GENDER:
        d+="м.р. ";
        break;
      case Noun.FEMININE_GENDER:
        d+="ж.р. ";
        break;
      case Noun.NEUTER_GENDER:
        d+="с.р. ";
        break;
    }
    switch(number){
      case Noun.SINGULAR_NUMBER:
        d+="ед.ч. ";
        break;
      case Noun.PLURAL_NUMBER:
        d+="мн.ч. ";
        break;
    }
    return w+d;
  }

  static public Objects getPossibleWords(String word){
    Objects nouns = new Objects();
    Objects adjs = Adjective.getPossibleWords(word);
    Iterator iter = adjs.iterator();
    while (iter.hasNext()) {
      Adjective adj = (Adjective)iter.next();
      if (!adj.shortf && adj.category==Adjective.QUALITATIVE_CATEGORY) {
        nouns.add(new AdjectiveNoun(adj));
      }
    }
    return nouns;
  }
}