package language.word;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public interface WGender {
  public static int NOT_GENDER = 0;
  public static int MASCULINE_GENDER = 1;
  public static int FEMININE_GENDER = 2;
  public static int NEUTER_GENDER = 3;
  public int getGender();
}