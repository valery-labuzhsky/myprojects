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
    super("��������������");
    addSuperObject(new Word());
  }

  protected Adjective(String word){
    super(word);
    addSuperObject(superWord=new Adjective());
    addSuperObject(new ProperName());
  }

  public Word getNormal(){
    if (normal != null) return normal;
    if (endings[ending].startsWith("�")) return new Normal(stem, ��);
    else return new Normal(stem, ��);
  }

  public Word getForm(int wcase, int gender, int number){
    switch (wcase){
      case SUBJECTIVE_CASE:
        switch (gender){
          case NEUTER_GENDER:
            switch (number){
              case SINGULAR_NUMBER:
                if (endings[ending].startsWith("�") || endings[ending].startsWith("�"))
                  return new Adjective(stem, ��, category, gender, number, wcase, shortf);
                else
                  return new Adjective(stem, ��, category, gender, number, wcase, shortf);
            }
            break;
        }
        break;
      case DATIVE_CASE:
        switch (gender){
          case NEUTER_GENDER:
            switch (number){
              case SINGULAR_NUMBER:
                if (endings[ending].startsWith("�") || endings[ending].startsWith("�"))
                  return new Adjective(stem, ���, category, gender, number, wcase, shortf);
                else
                  return new Adjective(stem, ���, category, gender, number, wcase, shortf);
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
    if (category==QUALITATIVE_CATEGORY&&(stem.endsWith("���")||stem.endsWith("���"))){
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
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        break;
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        break;
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        }
        break;
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ��:
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ���:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
        }
        break;
      case ���:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        }
        break;
      case ��:
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.DATIVE_CASE,false));
        if (stem.endsWith("�")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ABLATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.GENITIVE_CASE,false));
        }
        break;
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        }
        break;
      case ��:
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ��:
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.SUBJECTIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,false));
        break;
      case ��:
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
        if (stem.endsWith("�")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
      case ��:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.GENITIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.PREPOSITIONAL_CASE,false));
        if (stem.endsWith("�")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
        break;
      case ���:
      case ���:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        if (stem.endsWith("�")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ABLATIVE_CASE,false));
        }
        break;
      case _:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,true));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,true));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case �:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,true));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.GENITIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case �:
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,true));
        break;
      case �:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,true));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,true));
        if (stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case �:
      case �:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.SUBJECTIVE_CASE,true));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,true));
        if (stem.endsWith("�")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")||stem.endsWith("��")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NOT_GENDER, Noun.PLURAL_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case �:
        if (stem.endsWith("�")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
        }
        break;
      case �:
        if (stem.endsWith("�")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.SUBJECTIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case ��:
        if (stem.endsWith("�")){
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
      case ���:
        if (stem.endsWith("�")){
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
      case ���:
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        ret.add(new Adjective(stem, ending, QUALITATIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        if (stem.endsWith("�")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER, Noun.DATIVE_CASE,false));
        }
        break;
      case �:
        if (stem.endsWith("�")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER, Noun.ACCUSATIVE_CASE,false));
        }
        break;
      case ��:
        if (stem.endsWith("�")){
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, MASCULINE_GENDER, SINGULAR_NUMBER, PREPOSITIONAL_CASE,false));
          ret.add(new Adjective(stem, ending, POSSESSIVE_CATEGORY, NEUTER_GENDER, SINGULAR_NUMBER, PREPOSITIONAL_CASE,false));
        }
        break;
    }
    return ret;
  }

  public final static String[] endings = new String[]{"", "��", "��", "��",
                                         "��", "��", "��", "���", "���", "��",
                                         "��", "��", "��", "��", "��", "��",
                                         "��", "���", "���", "�", "�", "�", "�",
                                         "�", "�", "�", "��", "���", "���", "�",
                                         "��", "��"};

  protected final static int �� = 1;
  protected final static int �� = 2;
  protected final static int �� = 3;
  protected final static int �� = 4;
  protected final static int �� = 5;
  protected final static int �� = 6;
  protected final static int ��� = 7;
  protected final static int ��� = 8;
  protected final static int �� = 9;
  protected final static int �� = 10;
  protected final static int �� = 11;
  protected final static int �� = 12;
  protected final static int �� = 13;
  protected final static int �� = 14;
  protected final static int �� = 15;
  protected final static int �� = 16;
  protected final static int ��� = 17;
  protected final static int ��� = 18;
  protected final static int � = 19;
  protected final static int � = 20;
  protected final static int � = 21;
  protected final static int � = 22;
  protected final static int � = 23;
  protected final static int � = 24;
  protected final static int � = 25;
  protected final static int �� = 26;
  protected final static int ��� = 27;
  protected final static int ��� = 28;
  protected final static int � = 29;
  protected final static int �� = 30;
  protected final static int �� = 31;

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
    if (word.endsWith("��")||word.endsWith("��")||word.endsWith("��")){
      adjs.add(new Adjective(word, word));
    } else if (word.endsWith("�")&&!Letter.isVoice(word.charAt(word.length()-2))){
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
      d+="��. ";
    }
    switch(category){
      case QUALITATIVE_CATEGORY:
        d+="�������. ";
        break;
      case POSSESSIVE_CATEGORY:
        d+="������. ";
        break;
    }
    d+="����. ";
    if (degree==COMPARATIVE_DEGREE){
      d+="�����.��. ";
    } else {
      if (degree==SUPERLATIVE_DEGREE){
        d+="�������.��. ";
      }
      w+=endings[ending];
      switch (wcase){
        case SUBJECTIVE_CASE:
          d+="�.�. ";
          break;
        case GENITIVE_CASE:
          d+="�.�. ";
          break;
        case DATIVE_CASE:
          d+="�.�. ";
          break;
        case ACCUSATIVE_CASE:
          d+="�.�. ";
          break;
        case ABLATIVE_CASE:
          d+="�.�. ";
          break;
        case PREPOSITIONAL_CASE:
          d+="�.�. ";
          break;
      }
      switch(gender){
        case MASCULINE_GENDER:
          d+="�.�. ";
          break;
        case FEMININE_GENDER:
          d+="�.�. ";
          break;
        case NEUTER_GENDER:
          d+="�.�. ";
          break;
      }
      switch(number){
        case SINGULAR_NUMBER:
          d+="��.�. ";
          break;
        case PLURAL_NUMBER:
          d+="��.�. ";
          break;
      }
    }
    return w+d;
  }

}