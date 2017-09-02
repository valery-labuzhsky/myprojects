package intelligence;

import language.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ThisIs extends Object{
  public ThisIs() {
    super("@это");
  }

  public ThisIs(Object obj){
    this();
    addClassAttribute(new Question.ЧтоИ(), obj);
  }
}