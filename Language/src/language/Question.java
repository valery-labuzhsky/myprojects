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
    super("������");
  }

  public Question(intelligence.Object superObject){
    this();
    addSuperObject(superObject);
  }

  public static class ���� extends intelligence.Object{
    public ����(){
      super("������", new Question());
    }
  }

  public static class ���� extends intelligence.Object{
    public ����(){
      super("����", new Question());
    }
  }

  public static class ���� extends intelligence.Object{
    public ����(){
      super("���", new Question());
    }
  }

  public static class ����� extends intelligence.Object{
    public �����(){
      super("�����", new Question());
    }
  }

  public static class ��� extends intelligence.Object{
    public ���(){
      super("���", new Question());
    }
  }

  public static class ������ extends intelligence.Object{
    public ������(){
      super("������", new Question());
    }
  }
}

