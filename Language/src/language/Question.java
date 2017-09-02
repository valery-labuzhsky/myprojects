package language;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class Question extends intelligence.Object{
  public Question(){
    super("вопрос");
  }

  public Question(intelligence.Object superObject){
    this();
    addSuperObject(superObject);
  }

  public static class ЧтоИ extends intelligence.Object{
    public ЧтоИ(){
      super("кточто", new Question());
    }
  }

  public static class Чего extends intelligence.Object{
    public Чего(){
      super("чего", new Question());
    }
  }

  public static class ЧтоВ extends intelligence.Object{
    public ЧтоВ(){
      super("что", new Question());
    }
  }

  public static class Какой extends intelligence.Object{
    public Какой(){
      super("какой", new Question());
    }
  }

  public static class Чей extends intelligence.Object{
    public Чей(){
      super("чей", new Question());
    }
  }

  public static class Делает extends intelligence.Object{
    public Делает(){
      super("делает", new Question());
    }
  }
}

