package language.word;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author uniCorn
 * @version 1.0
 */

public interface WCase {
  public static int NOT_CASE = 0;
  public static int SUBJECTIVE_CASE = 1;
  public static int GENITIVE_CASE = 2;
  public static int DATIVE_CASE = 3;
  public static int ACCUSATIVE_CASE = 4;
  public static int ABLATIVE_CASE = 5;
  public static int PREPOSITIONAL_CASE = 6;

  public int getCase();
}