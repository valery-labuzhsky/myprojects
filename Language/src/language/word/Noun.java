package language.word;

import intelligence.*;
import intelligence.util.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
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

public class Noun extends Word implements WCase, WGender, WNumber{
  protected int declension = 0;
  protected int wcase = SUBJECTIVE_CASE;
  protected int gender;
  protected int number = SINGULAR_NUMBER;
  public static final Noun NOUN = new Noun();

  public static class Number extends intelligence.Object{
    public Number(){
      super("число");
    }

    public static class Singular extends Number{
      public Singular(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("единственный"));
      }
    }

    public static class Plural extends Number{
      public Plural(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("множественный"));
      }
    }
  }

  public static class Case extends intelligence.Object{
    public Case(){
      super("падеж");
    }

    public static class Subjective extends Case{
      public Subjective(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("именительный"));
      }
    }

    public static class Genetive extends Case{
      public Genetive(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("родительный"));
      }
    }

    public static class Dative extends Case{
      public Dative(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("дательный"));
      }
    }

    public static class Accusative extends Case{
      public Accusative(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("винительный"));
      }
    }

    public static class Ablative extends Case{
      public Ablative(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("творительный"));
      }
    }

    public static class Prepositional extends Case{
      public Prepositional(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("предложный"));
      }
    }
  }

  public static class Gender extends intelligence.Object{
    public Gender(){
      super("род");
    }

    public static class Masculine extends Gender{
      public Masculine(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("мужской"));
      }
    }

    public static class Feminine extends Gender{
      public Feminine(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("женский"));
      }
    }

    public static class Neuter extends Gender{
      public Neuter(){
        super();
        addClassAttribute(new Question.Какой(), new intelligence.Object("средний"));
      }
    }

  }

  public Noun(){
    super("существительное");
    addSuperObject(new Word());
  }

  public class Const extends Noun{
    public Const(){
      addClassAttribute(new Question.Какой(), new intelligence.Object("неизменяемый"));
    }
  }

  private Noun(String word){
    super(word);
    addSuperObject(superWord=new Noun());
    addSuperObject(new ProperName());
  }

  protected class Normal extends Noun{
    public Normal(String stem, int ending){
      super(stem, ending, Noun.this.gender);
      declension = Noun.this.declension;
      gender = Noun.this.gender;
      setClassAttributes();
      intelligence.Object form = new Form();
      form.addClassAttributes(new Case(), Noun.this.superWord.getClassAttributes(new Case()));
      form.addClassAttributes(new Number(), Noun.this.superWord.getClassAttributes(new Number()));
      addAttribute(form, Noun.this);
    }
  }

  public boolean isNormal(){
    return number==SINGULAR_NUMBER && wcase==SUBJECTIVE_CASE;
  }

  public Word getNormal(){
    if (normal != null) return normal;
    switch (declension){
      case 1:
        switch (wcase) {
          case GENITIVE_CASE:
            if (stem.endsWith("й") || stem.endsWith("ь")) {
              return new Normal(stem.substring(0, stem.length() - 1), я);
            }
            else {
              return new Normal(stem, а);
            }
          case ACCUSATIVE_CASE:
            switch (ending) {
              case у:
                return new Normal(stem, а);
              case ю:
                return new Normal(stem, я);
              case и:
                return new Normal(stem, а);
            }
            break;
        }
        break;
      case 2:
        switch (gender){
          case MASCULINE_GENDER:
            return new Normal(stem, _);
          case NEUTER_GENDER:
            if (endings[ending].startsWith("о")||endings[ending].startsWith("а")||endings[ending].startsWith("у")){
              return new Normal(stem, о);
            } else {
              return new Normal(stem, е);
            }
        }
        break;
      case 3:
        if (number!=SINGULAR_NUMBER || (wcase!=SUBJECTIVE_CASE && wcase!=ACCUSATIVE_CASE)) {
          return new Normal(stem+"ь", _);
        }
        break;
    }
    return this;
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
        addWordAttribute(new Gender(), new Gender.Masculine());
        break;
      case Noun.FEMININE_GENDER:
        addWordAttribute(new Gender(), new Gender.Feminine());
        break;
      case Noun.NEUTER_GENDER:
        addWordAttribute(new Gender(), new Gender.Neuter());
        break;
    }

    switch (number){
      case Noun.SINGULAR_NUMBER:
        addWordAttribute(new Number(), new Number.Singular());
        break;
      case Noun.PLURAL_NUMBER:
        addWordAttribute(new Number(), new Number.Plural());
        break;
    }

    if (isNormal()){
      addSuperObject(new Form.Normal());
    }
    else {
      addClassAttribute(new Form.Normal(), normal=getNormal());
    }
  }

  protected Noun(String stem, int ending, int gender){
    this(stem+endings[ending]);
    this.gender = gender;
  }

  protected Noun(String word, String stem, int ending, int declension, int casew, int gender, int number) {
    this(word);
    this.word = word;
    this.stem = stem;
    this.ending = ending;
    this.declension = declension;
    this.wcase = casew;
    this.gender = gender;
    this.number = number;
    setClassAttributes();
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

  static protected Objects getPossibleWords(String word, String stem, int ending, int declension, int casew, int number) {
    Objects ret = new Objects();
    switch (declension){
      case 1:
        ret.add(new Noun(word, stem, ending, declension, casew, MASCULINE_GENDER, number));
        ret.add(new Noun(word, stem, ending, declension, casew, FEMININE_GENDER, number));
        break;
      case 2:
        ret.add(new Noun(word, stem, ending, declension, casew, MASCULINE_GENDER, number));
        ret.add(new Noun(word, stem, ending, declension, casew, NEUTER_GENDER, number));
        break;
      case 3:
        ret.add(new Noun(word, stem, ending, declension, casew, FEMININE_GENDER, number));
        break;
    }
    return ret;
  }

  static protected Objects getPossibleWords(String word, int ending){
    String stem = word.substring(0,word.length()-endings[ending].length());
    Objects ret = new Objects();
    switch (ending){
      case а:
        ret.add(getPossibleWords(word, stem, ending, 1, SUBJECTIVE_CASE, SINGULAR_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, GENITIVE_CASE, SINGULAR_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, ACCUSATIVE_CASE, PLURAL_NUMBER));
      case я:
        ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, MASCULINE_GENDER, SINGULAR_NUMBER));
//        ret.addAll(getPossibleWords(word, stem, ending, 2, ACCUSATIVE_CASE, SINGULAR_NUMBER));
        break;
      case и:
        if (stem.endsWith("и")){
          ret.add(getPossibleWords(word, stem, ending, 1, DATIVE_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 1, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 2, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
        } else {
          ret.add(getPossibleWords(word, stem, ending, 3, GENITIVE_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 3, DATIVE_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 3, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
        }
        ret.add(getPossibleWords(word, stem, ending, 3, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, ACCUSATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 1, GENITIVE_CASE, SINGULAR_NUMBER));
      case ы:
        ret.add(getPossibleWords(word, stem, ending, 1, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 1, ACCUSATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, ACCUSATIVE_CASE, PLURAL_NUMBER));
        break;
      case е:
        ret.add(new Noun(word, stem, ending, 2, SUBJECTIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        if (!stem.endsWith("и")){
          ret.add(getPossibleWords(word, stem, ending, 1, DATIVE_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 1, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 2, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
        }
        break;
      case ю:
        ret.add(getPossibleWords(word, stem, ending, 3, ABLATIVE_CASE, SINGULAR_NUMBER));
      case у:
        ret.add(getPossibleWords(word, stem, ending, 1, ACCUSATIVE_CASE, SINGULAR_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, DATIVE_CASE, SINGULAR_NUMBER));
        break;
      case ей:
        ret.add(getPossibleWords(word, stem, ending, 3, GENITIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, ACCUSATIVE_CASE, PLURAL_NUMBER));
      case ой:
      case ою:
      case ею:
        ret.add(getPossibleWords(word, stem, ending, 1, ABLATIVE_CASE, SINGULAR_NUMBER));
        break;
      case _:
        if (!Letter.isVoice(stem.charAt(stem.length()-1))){
          ret.add(new Noun(word, stem, ending, 2, SUBJECTIVE_CASE, MASCULINE_GENDER, SINGULAR_NUMBER));
          ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, MASCULINE_GENDER, SINGULAR_NUMBER));
          if (stem.charAt(stem.length()-1)!='ь'){
            ret.add(getPossibleWords(word, stem, ending, 1, GENITIVE_CASE, PLURAL_NUMBER));
          } else {
            ret.add(getPossibleWords(word, stem, ending, 3, SUBJECTIVE_CASE, SINGULAR_NUMBER));
            ret.add(getPossibleWords(word, stem, ending, 3, ACCUSATIVE_CASE, SINGULAR_NUMBER));
          }
        }
        break;
      case о:
        ret.add(new Noun(word, stem, ending, 2, SUBJECTIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        break;
      case ом:
      case ем:
        ret.add(getPossibleWords(word, stem, ending, 2, ABLATIVE_CASE, SINGULAR_NUMBER));
        break;
      case ов:
      case ев:
        ret.add(getPossibleWords(word, stem, ending, 2, GENITIVE_CASE, PLURAL_NUMBER));
        break;
      case ам:
      case ям:
        ret.add(getPossibleWords(word, stem, ending, 1, DATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, DATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, DATIVE_CASE, PLURAL_NUMBER));
        break;
      case ами:
      case ями:
        ret.add(getPossibleWords(word, stem, ending, 1, ABLATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, ABLATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, ABLATIVE_CASE, PLURAL_NUMBER));
        break;
      case ах:
      case ях:
        ret.add(getPossibleWords(word, stem, ending, 1, PREPOSITIONAL_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, PREPOSITIONAL_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, PREPOSITIONAL_CASE, PLURAL_NUMBER));
        break;
    }
    return ret;
  }

  static public final int а = 0;
  static public final int я = 1;
  static public final int и = 2;
  static public final int е = 3;
  static public final int ю = 4;
  static public final int у = 5;
  static public final int ей = 6;
  static public final int ой = 7;
  static public final int ою = 8;
  static public final int ею = 9;
  static public final int _ = 10;
  static public final int о = 11;
  static public final int ом = 12;
  static public final int ем = 13;
  static public final int ов = 14;
  static public final int ев = 15;
  static public final int ам = 16;
  static public final int ям = 17;
  static public final int ами = 18;
  static public final int ями = 19;
  static public final int ах = 20;
  static public final int ях = 21;
  static public final int ы = 22;

  public final static String[] endings = new String[]{"а", "я", "и", "е", "ю",
                                         "у", "ей", "ой", "ою", "ею", "", "о",
                                         "ом", "ем", "ов", "ев", "ам", "ям",
                                         "ами", "ями", "ах", "ях", "ы"};

  static public Objects getPossibleWords(String word){
    Objects nouns = new Objects();
    for (int i=0;i<endings.length;i++){
      if (word.endsWith(endings[i])){
        nouns.add(getPossibleWords(word,i));
      }
    }
    return nouns;
  }

  public String getDescription(){
    if (stem==null) return "";
    String ret = stem+"-"+endings[ending]+" сущ ";
    switch (declension){
      case 1:
        ret+="1-е скл ";
        break;
      case 2:
        ret+="2-е скл ";
        break;
      case 3:
        ret+="3-е скл ";
        break;
    }
    switch (wcase){
      case SUBJECTIVE_CASE:
        ret+="И.П. ";
        break;
      case GENITIVE_CASE:
        ret+="Р.П. ";
        break;
      case DATIVE_CASE:
        ret+="Д.П. ";
        break;
      case ACCUSATIVE_CASE:
        ret+="В.П. ";
        break;
      case ABLATIVE_CASE:
        ret+="Т.П. ";
        break;
      case PREPOSITIONAL_CASE:
        ret+="П.П. ";
        break;
    }
    switch(gender){
      case MASCULINE_GENDER:
        ret+="м.р. ";
        break;
      case FEMININE_GENDER:
        ret+="ж.р. ";
        break;
      case NEUTER_GENDER:
        ret+="с.р. ";
        break;
    }
    switch(number){
      case SINGULAR_NUMBER:
        ret+="ед.ч. ";
        break;
      case PLURAL_NUMBER:
        ret+="мн.ч. ";
        break;
    }
    return ret;
  }

}