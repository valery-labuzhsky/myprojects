package language.word;

import intelligence.*;
import intelligence.util.*;
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

public class Particle extends Word {
  private int sort;
  public static final int NOTDEFINED_SORT = -1;
  public static final int DEMONSTRATIVE_SORT = 3;
  public static final int NEGATIVE_SORT = 5;

  public Particle(){
    super("�������");
    addSuperObject(new Word());
  }

  public static class Demonstrative extends Particle{
    public Demonstrative(){
      super();
      this.addClassAttribute(new Question.�����(), new intelligence.Object("������������"));
    }

  }

  public static class Negative extends Particle{
    public Negative(){
      super();
      this.addClassAttribute(new Question.�����(), new intelligence.Object("�������������"));
    }

  }

  protected Particle(String word, int sort) {
    super(word);
    addSuperObject(superWord=new Particle());
    addSuperObject(new ProperName());
    addSuperObject(new Form.Normal());
    this.sort = sort;
    switch(sort){
      case DEMONSTRATIVE_SORT:
        this.addWordAttribute(new Question.�����(), new intelligence.Object("������������"));
        break;
      case NEGATIVE_SORT:
        this.addWordAttribute(new Question.�����(), new intelligence.Object("�������������"));
        break;
    }
  }

  public static Objects getPossibleWords(String word){
    Objects aw = new Objects();
    if (word.equals("���")){
      aw.add(new Particle(word, DEMONSTRATIVE_SORT));
    } else if (word.equals("��")){
      aw.add(new Particle(word, NEGATIVE_SORT));
    }
    return aw;
  }
}