package language.word;

import intelligence.*;
import intelligence.util.*;
import java.util.*;
import language.*;
import language.letter.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Adjective extends Word implements WCase, WGender, WNumber{
  protected int gender = MASCULINE_GENDER;
  protected int number = SINGULAR_NUMBER;
  protected int wcase = SUBJECTIVE_CASE;
  protected boolean shortf = false;

  protected int degree;
  public static final int SIMPLE_DEGREE = 1;
  public static final int COMPARATIVE_DEGREE = 2;
  public static final int SUPERLATIVE_DEGREE = 3;

  protected int category;
  public static final int QUALITATIVE_CATEGORY = 1;
  public static final int POSSESSIVE_CATEGORY = 3;

  public Adjective(){
    super("прилагательное");
    addSuperObject(new Word());
  }

  protected Adjective(String word){
    super(word);
    addSuperObject(superWord=new Adjective());
    addSuperObject(new ProperName());
  }

  public Word getNormal(){
    if (normal != null) return normal;
    if (endings[ending].startsWith("е")) return new Normal(stem, ий);
    else return new Normal(stem, ый);
  }

  public Word getForm(int wcase, int gender, int number){
    switch (wcase){
      case SUBJECTIVE_CASE:
        switch (gender){
          case NEUTER_GENDER:
            switch (number){
              case SINGULAR_NUMBER:
                if (endings[ending].startsWith("о") || endings[ending].startsWith("ы"))
                  return new Adjective(stem, ое, category, gender, number, wcase, shortf);
                else
                  return new Adjective(stem, ее, category, gender, number, wcase, shortf);
            }
            break;
        }
        break;
      case DATIVE_CASE:
        switch (gender){
          case NEUTER_GENDER:
            switch (number){
              case SINGULAR_NUMBER:
                if (endings[ending].startsWith("о") || endings[ending].startsWith("ы"))
                  return new Adjective(stem, ому, category, gender, number, wcase, shortf);
                else
                  return new Adjective(stem, ему, category, gender, number, wcase, shortf);
            }
            break;
        }
        break;
    }
    return new Adjective(stem, ending, category, gender, number, wcase, shortf);
  }

  private class Normal extends Adjective{
    public Normal(String stem, int ending){
      super(stem, ending);
      setClassAttributes();
      intelligence.Object form = new Form();
      form.addClassAttributes(new Noun.Case(), Adjective.this.superWord.getClassAttributes(new Noun.Case()));
      form.addClassAttributes(new Noun.Number(), Adjective.this.superWord.getClassAttributes(new Noun.Number()));
      form.addClassAttributes(new Noun.Gender(), Adjective.this.superWord.getClassAttributes(new Noun.Gender()));
      addAttribute(form, Adjective.this);
    }
  }

  protected void setClassAttributes(){
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

    if (isNormal()){
      addSuperObject(new Form.Normal());
    }
    else {
      addClassAttribute(new Form.Normal(), normal=getNormal());
    }
  }

  public boolean isNormal(){
    return gender==MASCULINE_GENDER && number==SINGULAR_NUMBER && wcase==SUBJECTIVE_CASE;
  }

  protected Adjective(String word, String stem){
    this(word);
    this.stem = stem;
    this.category = QUALITATIVE_CATEGORY;
    this.degree = COMPARATIVE_DEGREE;
    setClassAttributes();
  }

  protected Adjective(String stem, int ending){
    this(stem+endings[ending]);
  }

  protected Adjective(String stem, int ending, int category, int gender, int number, int casew, boolean shortf){
    this(stem, ending);
    this.ending = ending;
    this.category = category;
    this.gender = gender;
    this.number = number;
    this.wcase = casew;
    this.shortf = shortf;
    this.stem = stem;
    if (category==QUALITATIVE_CATEGORY&&(stem.endsWith("ейш")||stem.endsWith("айш"))){
      this.degree = SUPERLATIVE_DEGREE;
    } else {
      this.degree = SIMPLE_DEGREE;
    }
    setClassAttributes();
  }

  protected static Objects getPossibleWords(String word, int ending){
    Objects ret = new Objects();
    String stem = word.substring(0,word.length()-endings[ending].length());
    switch (ending){
      case ая:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        break;
      case ою:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        break;
      case ой:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        }
        break;
      case ую:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ий:
      case ый:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ого:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        }
        break;
      case ому:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        }
        break;
      case им:
      case ым:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.DATIVE_CASE,false));
        if (stem.endsWith("ь")||stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.GENITIVE_CASE,false));
        }
        break;
      case ом:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        }
        break;
      case ее:
      case ое:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ые:
      case ие:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ых:
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
      case их:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        if (stem.endsWith("ь")||stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
        break;
      case ыми:
      case ими:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        if (stem.endsWith("ь")||stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
        break;
      case _:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,true));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,true));
        if (stem.endsWith("ий")||stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case а:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,true));
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case у:
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,true));
        break;
      case о:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,true));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,true));
        if (stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case и:
      case ы:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.SUBJECTIVE_CASE,true));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,true));
        if (stem.endsWith("ь")||stem.endsWith("ин")||stem.endsWith("ын")||stem.endsWith("ов")||stem.endsWith("ев")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case я:
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        }
        break;
      case е:
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case ей:
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        } else {
          ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY,
                                FEMININE_GENDER, Noun.SINGULAR_NUMBER,
                                GENITIVE_CASE, false));
          ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY,
                                FEMININE_GENDER, Noun.SINGULAR_NUMBER,
                                DATIVE_CASE, false));
          ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY,
                                FEMININE_GENDER, SINGULAR_NUMBER,
                                ABLATIVE_CASE, false));
          ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY,
                                FEMININE_GENDER, SINGULAR_NUMBER,
                                PREPOSITIONAL_CASE, false));
        }
        break;
      case его:
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        } else {
          ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY,
                                Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER,
                                Noun.GENITIVE_CASE, false));
          ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY,
                                Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER,
                                Noun.GENITIVE_CASE, false));
        }
        break;
      case ему:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        }
        break;
      case ю:
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case ем:
        if (stem.endsWith("ь")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, MASCULINE_GENDER, SINGULAR_NUMBER, PREPOSITIONAL_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, NEUTER_GENDER, SINGULAR_NUMBER, PREPOSITIONAL_CASE,false));
        }
        break;
    }
    return ret;
  }

  public final static String[] endings = new String[]{"", "ая", "ою", "ой",
                                         "ую", "ий", "ый", "ого", "ому", "им",
                                         "ым", "ом", "ое", "ые", "ие", "ых",
                                         "их", "ыми", "ими", "а", "у", "о", "и",
                                         "ы", "я", "е", "ей", "его", "ему", "ю",
                                         "ем", "ее"};

  protected final static int ая = 1;
  protected final static int ою = 2;
  protected final static int ой = 3;
  protected final static int ую = 4;
  protected final static int ий = 5;
  protected final static int ый = 6;
  protected final static int ого = 7;
  protected final static int ому = 8;
  protected final static int им = 9;
  protected final static int ым = 10;
  protected final static int ом = 11;
  protected final static int ое = 12;
  protected final static int ые = 13;
  protected final static int ие = 14;
  protected final static int ых = 15;
  protected final static int их = 16;
  protected final static int ыми = 17;
  protected final static int ими = 18;
  protected final static int а = 19;
  protected final static int у = 20;
  protected final static int о = 21;
  protected final static int и = 22;
  protected final static int ы = 23;
  protected final static int я = 24;
  protected final static int е = 25;
  protected final static int ей = 26;
  protected final static int его = 27;
  protected final static int ему = 28;
  protected final static int ю = 29;
  protected final static int ем = 30;
  protected final static int ее = 31;

  public static Objects getPossibleWords(String word){
    Objects adjs = new Objects();
    for (int i=0;i<endings.length;i++){
      try {
        if (word.endsWith(endings[i])&&!Letter.isVoice(word.charAt(word.length()-endings[i].length()-1))){
          adjs.add(getPossibleWords(word,i));
        }
      } catch (StringIndexOutOfBoundsException ex){
        System.err.println("Adjective: "+ex.getMessage());
      }
    }
    if (word.endsWith("ее")||word.endsWith("ей")||word.endsWith("ше")){
      adjs.add(new Adjective(word, word));
    } else if (word.endsWith("е")&&!Letter.isVoice(word.charAt(word.length()-2))){
      adjs.add(new Adjective(word, word));
    }
    return adjs;
  }

  public int getCase(){
    return wcase;
  }

  public int getGender(){
    return gender;
  }

  public int getNumber(){
    return number;
  }

  public String getDescription(){
    if (stem==null) return "";
    String w = stem+'-';
    String d = " ";
    if (shortf){
      d+="кр. ";
    }
    switch(category){
      case QUALITATIVE_CATEGORY:
        d+="качеств. ";
        break;
      case POSSESSIVE_CATEGORY:
        d+="притяж. ";
        break;
    }
    d+="прил. ";
    if (degree==COMPARATIVE_DEGREE){
      d+="сравн.ст. ";
    } else {
      if (degree==SUPERLATIVE_DEGREE){
        d+="превосх.ст. ";
      }
      w+=endings[ending];
      switch (wcase){
        case SUBJECTIVE_CASE:
          d+="И.П. ";
          break;
        case GENITIVE_CASE:
          d+="Р.П. ";
          break;
        case DATIVE_CASE:
          d+="Д.П. ";
          break;
        case ACCUSATIVE_CASE:
          d+="В.П. ";
          break;
        case ABLATIVE_CASE:
          d+="Т.П. ";
          break;
        case PREPOSITIONAL_CASE:
          d+="П.П. ";
          break;
      }
      switch(gender){
        case MASCULINE_GENDER:
          d+="м.р. ";
          break;
        case FEMININE_GENDER:
          d+="ж.р. ";
          break;
        case NEUTER_GENDER:
          d+="с.р. ";
          break;
      }
      switch(number){
        case SINGULAR_NUMBER:
          d+="ед.ч. ";
          break;
        case PLURAL_NUMBER:
          d+="мн.ч. ";
          break;
      }
    }
    return w+d;
  }

}