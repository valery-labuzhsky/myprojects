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
    super("�����");
  }

  public static class Normal extends Form{
    public Normal(){
      addClassAttribute(new Question.�����(), new intelligence.Object("����������"));
    }
  }

  public static final Normal NORMAL = new Normal();

}