package language;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Form extends intelligence.Object{
  public Form() {
    super("форма");
  }

  public static class Normal extends Form{
    public Normal(){
      addClassAttribute(new Question.Какой(), new intelligence.Object("нормальный"));
    }
  }

  public static final Normal NORMAL = new Normal();

}