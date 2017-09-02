package intelligence.util;

import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ArrayEnumeration implements Enumeration{
  private java.lang.Object[] array;
  private int i = 0;

  public ArrayEnumeration(java.lang.Object[] array){
    this.array = array;
  }

  public boolean hasMoreElements(){
    return i<array.length;
  }

  public java.lang.Object nextElement(){
    return hasMoreElements()?array[i]:null;
  }
}