package language.word;

import intelligence.util.*;
import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Quotation extends Word {
  public Quotation(){
    super("речь");
    addClassAttribute(new Question.Какой(), new intelligence.Object("прямой"));
    addSuperObject(new Word());
  }

  public Quotation(String word) {
    super(word);
    addSuperObject(superWord=new Quotation());
    addSuperObject(new ProperName());
    addSuperObject(new Form.Normal());
  }

  public String getDescription(){
    return '\"'+word+'\"';
  }
}