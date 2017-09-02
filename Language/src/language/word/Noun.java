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
      super("�����");
    }

    public static class Singular extends Number{
      public Singular(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("������������"));
      }
    }

    public static class Plural extends Number{
      public Plural(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�������������"));
      }
    }
  }

  public static class Case extends intelligence.Object{
    public Case(){
      super("�����");
    }

    public static class Subjective extends Case{
      public Subjective(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("������������"));
      }
    }

    public static class Genetive extends Case{
      public Genetive(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�����������"));
      }
    }

    public static class Dative extends Case{
      public Dative(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("���������"));
      }
    }

    public static class Accusative extends Case{
      public Accusative(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�����������"));
      }
    }

    public static class Ablative extends Case{
      public Ablative(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("������������"));
      }
    }

    public static class Prepositional extends Case{
      public Prepositional(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("����������"));
      }
    }
  }

  public static class Gender extends intelligence.Object{
    public Gender(){
      super("���");
    }

    public static class Masculine extends Gender{
      public Masculine(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�������"));
      }
    }

    public static class Feminine extends Gender{
      public Feminine(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�������"));
      }
    }

    public static class Neuter extends Gender{
      public Neuter(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�������"));
      }
    }

  }

  public Noun(){
    super("���������������");
    addSuperObject(new Word());
  }

  public class Const extends Noun{
    public Const(){
      addClassAttribute(new Question.�����(), new intelligence.Object("������������"));
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
            if (stem.endsWith("�") || stem.endsWith("�")) {
              return new Normal(stem.substring(0, stem.length() - 1), �);
            }
            else {
              return new Normal(stem, �);
            }
          case ACCUSATIVE_CASE:
            switch (ending) {
              case �:
                return new Normal(stem, �);
              case �:
                return new Normal(stem, �);
              case �:
                return new Normal(stem, �);
            }
            break;
        }
        break;
      case 2:
        switch (gender){
          case MASCULINE_GENDER:
            return new Normal(stem, _);
          case NEUTER_GENDER:
            if (endings[ending].startsWith("�")||endings[ending].startsWith("�")||endings[ending].startsWith("�")){
              return new Normal(stem, �);
            } else {
              return new Normal(stem, �);
            }
        }
        break;
      case 3:
        if (number!=SINGULAR_NUMBER || (wcase!=SUBJECTIVE_CASE && wcase!=ACCUSATIVE_CASE)) {
          return new Normal(stem+"�", _);
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
      case �:
        ret.add(getPossibleWords(word, stem, ending, 1, SUBJECTIVE_CASE, SINGULAR_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, GENITIVE_CASE, SINGULAR_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, ACCUSATIVE_CASE, PLURAL_NUMBER));
      case �:
        ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, MASCULINE_GENDER, SINGULAR_NUMBER));
//        ret.addAll(getPossibleWords(word, stem, ending, 2, ACCUSATIVE_CASE, SINGULAR_NUMBER));
        break;
      case �:
        if (stem.endsWith("�")){
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
      case �:
        ret.add(getPossibleWords(word, stem, ending, 1, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 1, ACCUSATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, SUBJECTIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, ACCUSATIVE_CASE, PLURAL_NUMBER));
        break;
      case �:
        ret.add(new Noun(word, stem, ending, 2, SUBJECTIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        if (!stem.endsWith("�")){
          ret.add(getPossibleWords(word, stem, ending, 1, DATIVE_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 1, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
          ret.add(getPossibleWords(word, stem, ending, 2, PREPOSITIONAL_CASE, SINGULAR_NUMBER));
        }
        break;
      case �:
        ret.add(getPossibleWords(word, stem, ending, 3, ABLATIVE_CASE, SINGULAR_NUMBER));
      case �:
        ret.add(getPossibleWords(word, stem, ending, 1, ACCUSATIVE_CASE, SINGULAR_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, DATIVE_CASE, SINGULAR_NUMBER));
        break;
      case ��:
        ret.add(getPossibleWords(word, stem, ending, 3, GENITIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, ACCUSATIVE_CASE, PLURAL_NUMBER));
      case ��:
      case ��:
      case ��:
        ret.add(getPossibleWords(word, stem, ending, 1, ABLATIVE_CASE, SINGULAR_NUMBER));
        break;
      case _:
        if (!Letter.isVoice(stem.charAt(stem.length()-1))){
          ret.add(new Noun(word, stem, ending, 2, SUBJECTIVE_CASE, MASCULINE_GENDER, SINGULAR_NUMBER));
          ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, MASCULINE_GENDER, SINGULAR_NUMBER));
          if (stem.charAt(stem.length()-1)!='�'){
            ret.add(getPossibleWords(word, stem, ending, 1, GENITIVE_CASE, PLURAL_NUMBER));
          } else {
            ret.add(getPossibleWords(word, stem, ending, 3, SUBJECTIVE_CASE, SINGULAR_NUMBER));
            ret.add(getPossibleWords(word, stem, ending, 3, ACCUSATIVE_CASE, SINGULAR_NUMBER));
          }
        }
        break;
      case �:
        ret.add(new Noun(word, stem, ending, 2, SUBJECTIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        ret.add(new Noun(word, stem, ending, 2, ACCUSATIVE_CASE, NEUTER_GENDER, SINGULAR_NUMBER));
        break;
      case ��:
      case ��:
        ret.add(getPossibleWords(word, stem, ending, 2, ABLATIVE_CASE, SINGULAR_NUMBER));
        break;
      case ��:
      case ��:
        ret.add(getPossibleWords(word, stem, ending, 2, GENITIVE_CASE, PLURAL_NUMBER));
        break;
      case ��:
      case ��:
        ret.add(getPossibleWords(word, stem, ending, 1, DATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, DATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, DATIVE_CASE, PLURAL_NUMBER));
        break;
      case ���:
      case ���:
        ret.add(getPossibleWords(word, stem, ending, 1, ABLATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, ABLATIVE_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, ABLATIVE_CASE, PLURAL_NUMBER));
        break;
      case ��:
      case ��:
        ret.add(getPossibleWords(word, stem, ending, 1, PREPOSITIONAL_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 2, PREPOSITIONAL_CASE, PLURAL_NUMBER));
        ret.add(getPossibleWords(word, stem, ending, 3, PREPOSITIONAL_CASE, PLURAL_NUMBER));
        break;
    }
    return ret;
  }

  static public final int � = 0;
  static public final int � = 1;
  static public final int � = 2;
  static public final int � = 3;
  static public final int � = 4;
  static public final int � = 5;
  static public final int �� = 6;
  static public final int �� = 7;
  static public final int �� = 8;
  static public final int �� = 9;
  static public final int _ = 10;
  static public final int � = 11;
  static public final int �� = 12;
  static public final int �� = 13;
  static public final int �� = 14;
  static public final int �� = 15;
  static public final int �� = 16;
  static public final int �� = 17;
  static public final int ��� = 18;
  static public final int ��� = 19;
  static public final int �� = 20;
  static public final int �� = 21;
  static public final int � = 22;

  public final static String[] endings = new String[]{"�", "�", "�", "�", "�",
                                         "�", "��", "��", "��", "��", "", "�",
                                         "��", "��", "��", "��", "��", "��",
                                         "���", "���", "��", "��", "�"};

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
    String ret = stem+"-"+endings[ending]+" ��� ";
    switch (declension){
      case 1:
        ret+="1-� ��� ";
        break;
      case 2:
        ret+="2-� ��� ";
        break;
      case 3:
        ret+="3-� ��� ";
        break;
    }
    switch (wcase){
      case SUBJECTIVE_CASE:
        ret+="�.�. ";
        break;
      case GENITIVE_CASE:
        ret+="�.�. ";
        break;
      case DATIVE_CASE:
        ret+="�.�. ";
        break;
      case ACCUSATIVE_CASE:
        ret+="�.�. ";
        break;
      case ABLATIVE_CASE:
        ret+="�.�. ";
        break;
      case PREPOSITIONAL_CASE:
        ret+="�.�. ";
        break;
    }
    switch(gender){
      case MASCULINE_GENDER:
        ret+="�.�. ";
        break;
      case FEMININE_GENDER:
        ret+="�.�. ";
        break;
      case NEUTER_GENDER:
        ret+="�.�. ";
        break;
    }
    switch(number){
      case SINGULAR_NUMBER:
        ret+="��.�. ";
        break;
      case PLURAL_NUMBER:
        ret+="��.�. ";
        break;
    }
    return ret;
  }

}