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

public class Action extends Object{
  public Action() {
    super("действие");
  }

  public Action(String name){
    super(name);
    addSuperObject(new Action());
  }

  public static final Object imperative = new Object("повелительный");

  public static class Remember extends Action{
    public Remember(){
      super("запомнить");
    }
  }

}