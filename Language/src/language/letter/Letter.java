package language.letter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Letter extends intelligence.Object{
  public Letter(){
    super("������");
  }

  public Letter(char c){
    super(""+c);
    addSuperObject(new Letter());
  }

  public static boolean isVoice(char c){
    switch (c){
      case '�':
      case '�':
      case '�':
      case '�':
      case '�':
      case '�':
      case '�':
      case '�':
      case '�':
      case '�':
        return true;
    }
    return false;
  }
}