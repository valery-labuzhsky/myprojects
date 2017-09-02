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
    super("символ");
  }

  public Letter(char c){
    super(""+c);
    addSuperObject(new Letter());
  }

  public static boolean isVoice(char c){
    switch (c){
      case 'а':
      case 'о':
      case 'у':
      case 'э':
      case 'и':
      case 'ы':
      case 'я':
      case 'ё':
      case 'ю':
      case 'е':
        return true;
    }
    return false;
  }
}