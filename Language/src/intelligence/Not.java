package intelligence;

import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public class Not extends Object{
  public Not() {
    super("@��");
  }

  public Not(Object obj){
    this();
    addClassAttribute(new Question.����(), obj);
  }
}