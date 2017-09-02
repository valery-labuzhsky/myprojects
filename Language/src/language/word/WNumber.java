package language.word;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public interface WNumber {
  public static int NOT_NUMBER = 0;
  public static int SINGULAR_NUMBER = 1;
  public static int PLURAL_NUMBER = 2;

  public int getNumber();
}