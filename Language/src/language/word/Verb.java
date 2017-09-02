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

public class Verb extends Word{
  private String reflex = null;
  private String stem;
  private int ending = _;
  private int person = 0;
  private int conjugation;
  private int gender = 0;
  private int number = 0;
  private String infsuffix = null;

  private int tense = NOT_TENSE;
  public static final int NOT_TENSE = 0;
  public static final int PAST_TENSE = 1;
  public static final int PRESENT_TENSE = 2;
  public static final int FUTURE_TENSE = 3;

  protected int aspect;
  public static final int NOTDEFINED_ASPECT = -1;
  public static final int PERFECTIVE_ASPECT = 1;
  public static final int IMPERFECTIVE_ASPECT = 2;

  protected int mood;
  public static final int INFINITIVE_MOOD = 0;
  public static final int INDICATIVE_MOOD = 1;
  public static final int IMPERATIVE_MOOD = 2;
  public static final int CONJUNCTIVE_MOOD = 3;

  public static class Aspect extends intelligence.Object{
    public Aspect(){
      super("���");
    }

    public static class Imperfective extends Aspect{
      public Imperfective(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�������������"));
      }
    }

    public static class Perfective extends Aspect{
      public Perfective(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�����������"));
      }
    }
  }

  public static class Mood extends intelligence.Object{
    public Mood(){
      super("����������");
    }

    public static class Imperative extends Mood{
      public Imperative(){
        super();
        addClassAttribute(new Question.�����(), new intelligence.Object("�������������"));
      }
    }
  }

  public Verb(){
    super("������");
    addSuperObject(new Word());
  }

  private Verb(String word){
    super(word);
    addSuperObject(superWord=new Verb());
    addSuperObject(new ProperName());
  }

  private class Normal extends Verb{
    public Normal(){
      super(Verb.this.toNormal());
      mood = Verb.INFINITIVE_MOOD;
      aspect = Verb.this.aspect;
      setClassAttributes();
      intelligence.Object form = new Form();
      form.addClassAttributes(new Mood(), Verb.this.superWord.getClassAttributes(new Mood()));
      addAttribute(form, Verb.this);
    }
  }

  private String toNormal(){
    if (mood==IMPERATIVE_MOOD&&stem.endsWith("��")){
      word = stem.substring(0,stem.length()-2)+"���";
      return word;
    }
    return word;
  }

  protected void setClassAttributes(){
    switch(mood){
      case IMPERATIVE_MOOD:
        addWordAttribute(new Mood(), new Mood.Imperative());
        break;
    }
    switch(aspect){
      case PERFECTIVE_ASPECT:
        addWordAttribute(new Aspect(), new Aspect.Perfective());
        break;
      case IMPERFECTIVE_ASPECT:
        addWordAttribute(new Aspect(), new Aspect.Imperfective());
        break;
    }

    if (mood==Verb.INFINITIVE_MOOD){
      addSuperObject(new Form.Normal());
    }
    else {
      addClassAttribute(new Form.Normal(), new Normal());
    }
  }

  protected Verb(String word, String stem, String reflex, int conjugation, int aspect, int mood){
    this(word);
    this.stem = stem;
    this.reflex = reflex;
    this.conjugation = conjugation;
    this.aspect = aspect;
    this.mood = mood;
  }

  protected Verb(String word, String stem, String infsuffix, String reflex, int conjugation, int aspect){
    this(word, stem, reflex, conjugation, aspect, INFINITIVE_MOOD);
    this.infsuffix = infsuffix;
    setClassAttributes();
  }

  protected Verb(String word, String stem, String reflex, int ending, int person, int conjugation, int number, int tense, int aspect){
    this(word,stem,reflex,conjugation,aspect,INDICATIVE_MOOD);
    this.ending = ending;
    this.person = person;
    this.number = number;
    this.tense = tense;
    setClassAttributes();
  }

  protected Verb(String word, String stem, String reflex, int conjugation, int aspect, int ending, int gender, int number){
    this(word, stem, reflex, conjugation, aspect, INDICATIVE_MOOD);
    this.ending = ending;
    this.gender = gender;
    this.number = number;
    this.tense = PAST_TENSE;
    setClassAttributes();
  }

  public boolean isReflex(){
    return reflex!=null;
  }

  public int getMood(){
    return mood;
  }

  public int getAspect(){
    return aspect;
  }

  static protected Objects getPossibleInfinitive(String word, String stem, String infsuffix, String reflex, int aspect){
    Objects verbs = new Objects();
//
    verbs.add(new Verb(word,stem, infsuffix, reflex, -1, aspect));
//
/*    verbs.add(new Verb(word,stem, infsuffix, reflex, 1, aspect));
    verbs.add(new Verb(word,stem, infsuffix, reflex, 2, aspect));*/
    return verbs;
  }

  static protected Objects getPossibleWords(String word, String stem, String reflex){
    Objects verbs = new Objects();
    if ((stem.endsWith("��")&&!Letter.isVoice(stem.charAt(stem.length()-3)))||
        (stem.endsWith("��")&&Letter.isVoice(stem.charAt(stem.length()-3)))){
//
      verbs.add(getPossibleInfinitive(word, stem.substring(0,stem.length()-2),stem.substring(stem.length()-2),reflex, NOTDEFINED_ASPECT));
//
/*      verbs.addAll(getPossibleInfinitive(word, stem.substring(0,stem.length()-2),stem.substring(stem.length()-2),reflex, PERFECTIVE_ASPECT));
      verbs.addAll(getPossibleInfinitive(word, stem.substring(0,stem.length()-2),stem.substring(stem.length()-2),reflex, IMPERFECTIVE_ASPECT));
*/
    } else if (stem.endsWith("��")) {
//
      verbs.add(getPossibleInfinitive(word, word, null, reflex, NOTDEFINED_ASPECT));
//
/*
      verbs.addAll(getPossibleInfinitive(word, word, null, reflex, PERFECTIVE_ASPECT));
      verbs.addAll(getPossibleInfinitive(word, word, null, reflex, IMPERFECTIVE_ASPECT));
*/
    } else {
      for (int i=0;i<endings.length;i++){
        if (stem.endsWith(endings[i])){
          try {
            verbs.add(getPossibleWords(word,stem,reflex,i));
          } catch (StringIndexOutOfBoundsException ex){
            System.err.println("Verb: "+ex.getMessage());
          }
        }
      }
    }
    return verbs;
  }

  static protected Objects getPossibleWords(String word, String stem, String reflex, int ending, int person, int conjugation, int number){
    Objects verbs = new Objects();
    verbs.add(new Verb(word, stem, reflex, ending, person, conjugation, number, FUTURE_TENSE, PERFECTIVE_ASPECT));
    verbs.add(new Verb(word, stem, reflex, ending, person, conjugation, number, PRESENT_TENSE, IMPERFECTIVE_ASPECT));
    return verbs;
  }

  static protected Objects getPossibleWords(String word, String stem, String reflex, int ending, int gender, int number){
    Objects verbs = new Objects();
//
    verbs.add(new Verb(word, stem, reflex, -1, NOTDEFINED_ASPECT, ending, gender, number));
//
/*
    verbs.add(new Verb(word, stem, reflex, 1, PERFECTIVE_ASPECT, ending, gender, number));
    verbs.add(new Verb(word, stem, reflex, 1, IMPERFECTIVE_ASPECT, ending, gender, number));
    verbs.add(new Verb(word, stem, reflex, 2, PERFECTIVE_ASPECT, ending, gender, number));
    verbs.add(new Verb(word, stem, reflex, 2, IMPERFECTIVE_ASPECT, ending, gender, number));
*/
    return verbs;
  }

  protected Verb(String word, String stem, String reflex, int conjugation, int aspect, int ending, int number){
    this(word, stem, reflex, conjugation, aspect, IMPERATIVE_MOOD);
    this.ending = ending;
    this.number = number;
    setClassAttributes();
  }

  static protected Objects getPossibleWords(String word, String stem, String reflex, int ending, int number){
    Objects verbs = new Objects();
//
    verbs.add(new Verb(word, stem, reflex, -1, NOTDEFINED_ASPECT, ending, number));
//
//    verbs.add(new Verb(word, stem, reflex, -1, PERFECTIVE_ASPECT, IMPERATIVE_MOOD));
//    verbs.add(new Verb(word, stem, reflex, -1, IMPERFECTIVE_ASPECT, IMPERATIVE_MOOD));
/*
    verbs.add(new Verb(word, stem, reflex, 1, PERFECTIVE_ASPECT, IMPERATIVE_MOOD));
    verbs.add(new Verb(word, stem, reflex, 1, IMPERFECTIVE_ASPECT, IMPERATIVE_MOOD));
    verbs.add(new Verb(word, stem, reflex, 2, PERFECTIVE_ASPECT, IMPERATIVE_MOOD));
    verbs.add(new Verb(word, stem, reflex, 2, IMPERFECTIVE_ASPECT, IMPERATIVE_MOOD));
*/
    return verbs;
  }

  static protected Objects getPossibleWords(String word, String stem, String reflex, int ending){
    Objects verbs = new Objects();
    stem = stem.substring(0,stem.length()-endings[ending].length());
    switch (ending){
      case �:
      case �:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 1, 1, Noun.SINGULAR_NUMBER));
        verbs.add(getPossibleWords(word, stem, reflex, ending, 1, 2, Noun.SINGULAR_NUMBER));
        break;
      case ���:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 2, 1, Noun.SINGULAR_NUMBER));
        break;
      case ��:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 3, 1, Noun.SINGULAR_NUMBER));
        break;
      case ��:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 1, 1, Noun.PLURAL_NUMBER));
        break;
      case ���:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 2, 1, Noun.PLURAL_NUMBER));
        break;
      case ��:
      case ��:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 3, 2, Noun.PLURAL_NUMBER));
        break;
      case ���:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 2, 2, Noun.SINGULAR_NUMBER));
        break;
      case ��:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 3, 2, Noun.SINGULAR_NUMBER));
        break;
      case ��:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 1, 2, Noun.PLURAL_NUMBER));
        break;
      case ���:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 2, 2, Noun.PLURAL_NUMBER));
        break;
      case ��:
      case ��:
        verbs.add(getPossibleWords(word, stem, reflex, ending, 3, 2, Noun.PLURAL_NUMBER));
        break;
      case _:
        if (stem.endsWith("�")||stem.endsWith("�")||stem.endsWith("�"))
          verbs.add(getPossibleWords(word, stem, reflex, ending, Noun.SINGULAR_NUMBER));
        else if (stem.charAt(stem.length()-1) == '�' || stem.charAt(stem.length()-1) == '�')
//        else if (!Letter.isVoice(stem.charAt(stem.length()-1)))
          verbs.add(getPossibleWords(word, stem, reflex, ending, Noun.MASCULINE_GENDER, Noun.SINGULAR_NUMBER));
        break;
      case �:
        if (stem.charAt(stem.length()-1)=='�')
          verbs.add(getPossibleWords(word, stem, reflex, ending, Noun.FEMININE_GENDER, Noun.SINGULAR_NUMBER));
        break;
      case �:
        if (stem.charAt(stem.length()-1)=='�')
          verbs.add(getPossibleWords(word, stem, reflex, ending, Noun.NEUTER_GENDER, Noun.SINGULAR_NUMBER));
        break;
      case �:
        if (stem.charAt(stem.length()-1)=='�')
          verbs.add(getPossibleWords(word, stem, reflex, ending, Noun.NOT_GENDER, Noun.PLURAL_NUMBER));
        break;
      case ��:
        if (stem.endsWith("�")||stem.endsWith("�")||stem.endsWith("�"))
          verbs.add(getPossibleWords(word, stem, reflex, ending, Noun.PLURAL_NUMBER));
        break;
    }
    return verbs;
  }

  static public final String[] endings = new String[]
  {"","�","���","���","��","��","��","��","���","���","��","��","�","��","��","�","�","�","��"};

  public static final int _ = 0;
  public static final int � = 1;
  public static final int ��� = 2;
  public static final int ��� = 3;
  public static final int �� = 4;
  public static final int �� = 5;
  public static final int �� = 6;
  public static final int �� = 7;
  public static final int ��� = 8;
  public static final int ��� = 9;
  public static final int �� = 10;
  public static final int �� = 11;
  public static final int � = 12;
  public static final int �� = 13;
  public static final int �� = 14;
  public static final int � = 15;
  public static final int � = 16;
  public static final int � = 17;
  public static final int �� = 18;


  static public Objects getPossibleWords(String word){
    Objects verbs = new Objects();
    if ((word.endsWith("��")&&!Letter.isVoice(word.charAt(word.length()-3)))||
        (word.endsWith("��")&&Letter.isVoice(word.charAt(word.length()-3)))){
      verbs.add(getPossibleWords(word, word.substring(0,word.length()-2), word.substring(word.length()-2)));
    }
    else {
      verbs.add(getPossibleWords(word, word, null));
    }
    return verbs;
  }

  public String getDescription(){
    if (stem==null) return "";
    String ret = "";
    String suf = "";
    suf+=" �� ";
    switch(mood){
      case INFINITIVE_MOOD:
        ret+=stem+'-'+infsuffix;
        suf+="��������� ";
        break;
      case INDICATIVE_MOOD:
        ret+=stem+'-'+endings[ending];
        suf+="�������.����. ";
        switch(tense){
          case PAST_TENSE:
            suf+="��.��. ";
            switch(gender){
              case Noun.MASCULINE_GENDER:
                suf+="�.�. ";
                break;
              case Noun.FEMININE_GENDER:
                suf+="�.�. ";
                break;
              case Noun.NEUTER_GENDER:
                suf+="�.�. ";
                break;
            }
            switch(number){
              case Noun.SINGULAR_NUMBER:
                suf+="��.�. ";
                break;
              case Noun.PLURAL_NUMBER:
                suf+="��.�. ";
                break;
            }
            break;
          case PRESENT_TENSE:
            suf+="����.��. ";
            suf+=conjugation+"-�.���. ";
            suf+=person+"-�.���� ";
            switch(number){
              case Noun.SINGULAR_NUMBER:
                suf+="��.�. ";
                break;
              case Noun.PLURAL_NUMBER:
                suf+="��.�. ";
                break;
            }
            break;
          case FUTURE_TENSE:
            suf+="���.��. ";
            suf+=conjugation+"-�.���. ";
            suf+=person+"-�.���� ";
            switch(number){
              case Noun.SINGULAR_NUMBER:
                suf+="��.�. ";
                break;
              case Noun.PLURAL_NUMBER:
                suf+="��.�. ";
                break;
            }
            break;
        }
        break;
      case IMPERATIVE_MOOD:
        ret+=stem+'-'+endings[ending];
        suf+="�������.����. ";
        switch(number){
          case Noun.SINGULAR_NUMBER:
            suf+="��.�. ";
            break;
          case Noun.PLURAL_NUMBER:
            suf+="��.�. ";
            break;
        }
        break;
    }
    if (reflex!=null){
      ret+='-'+reflex;
      suf+="���������� ";
    }
    switch (aspect){
      case PERFECTIVE_ASPECT:
        suf+="���.�. ";
        break;
      case IMPERFECTIVE_ASPECT:
        suf+="�����.�. ";
        break;
    }
    return ret+suf;
  }

}