package language;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class ProperName extends intelligence.Object{
  public ProperName() {
    super("имя");
    addClassAttribute(new Question.Какой(), new intelligence.Object("собственный"));
  }

}